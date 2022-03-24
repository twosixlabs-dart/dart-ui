package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twosixlabs.dart.auth.controllers.SecureDartController
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.taxonomy.explorer.api.{RootApiDeps, StateAccessApiDI}
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterDI, DartTaxonomyDI, DartTaxonomyTestDataDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI
import com.twosixtech.dart.taxonomy.explorer.userdata.{InMemoryVersionedUserDataStore, VersionedUserDataStore}
import com.twosixtech.dart.taxonomy.explorer.utils.AsyncUtils
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import upickle.default._

import scala.concurrent.duration.DurationInt

class StateAccessRoutesTest
  extends AsyncUtils( 5.seconds )
    with AnyFlatSpecLike
    with Matchers
    with ScalatestRouteTest
    with BeforeAndAfterEach
    with StateAccessRoutesDI
    with StateAccessApiDI
    with CuratedClusterDI
    with WmDartSerializationDI
    with UUIDTaxonomyIdSerializationDI
    with UUIDTaxonomyIdDI
    with WmDartConceptDI
    with WmDartClusterConceptBridgeDI
    with DartTaxonomyDI
    with DartClusterDI
    with RootApiDeps
    with AuthRouterDI
    with DartTaxonomyTestDataDI {

    val authDeps : SecureDartController.Dependencies = new SecureDartController.Dependencies {
        override val serviceName : String = "state-access"
        override val secretKey : Option[ String ] = None
        override val useDartAuth : Boolean = false
        override val basicAuthCredentials : Seq[ (String, String) ] = Nil
    }

    override val userDataStore : VersionedUserDataStore[ DartUser, StateAccessApi.ConceptsState ] =
        new InMemoryVersionedUserDataStore[ DartUser, StateAccessApi.ConceptsState ]

    override val RootApi : RootApi = new RootApi {
        override val BASE_PATH : String = "test"
    }

    override def beforeEach( ) : Unit = {
        super.beforeEach()
        userDataStore
          .asInstanceOf[ InMemoryVersionedUserDataStore[ DartUser, StateAccessApi.ConceptsState ] ]
          .userMap
          .clear()
    }

    val testUser = AuthRouter.authBypassUser

    val testKey1 = "key-1"
    val testKey2 = "key-2"

    def testConceptsState( taxonomy : DartTaxonomy ) : StateAccessApi.ConceptsState =
        StateAccessApi.ConceptsState(
            taxonomy,
            None,
        )

    val testTaxonomy = DartTaxonomyData.taxonomy

    behavior of "GET /concepts/explorer/state"

    it should "return empty object when no keys have been added" in {
        Get( "/concepts/explorer/state" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            responseAs[ String ] shouldBe "{}"
        }
    }

    it should "return a dict with keys mapped to version count when keys have been added" in {
        userDataStore.save( testUser, testKey1, testConceptsState( testTaxonomy ) ).awaitWrite
        Get( "/concepts/explorer/state" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr : String = responseAs[ String ]
            val resMap : Map[ String, Int ] = read[ Map[ String, Int ] ]( resStr )
            resMap shouldBe Map( testKey1 -> 1 )
        }

        userDataStore.save( testUser, testKey2, testConceptsState( testTaxonomy ) ).awaitWrite
        Get( "/concepts/explorer/state" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr : String = responseAs[ String ]
            val resMap : Map[ String, Int ] = read[ Map[ String, Int ] ]( resStr )
            resMap shouldBe Map( testKey1 -> 1, testKey2 -> 1 )
        }

        userDataStore.save( testUser, testKey1, testConceptsState( testTaxonomy.removeConcept( Seq( DartTaxonomyData.concept1.name, DartTaxonomyData.concept1b.name ) ).get ) ).awaitWrite
        Get( "/concepts/explorer/state" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr : String = responseAs[ String ]
            val resMap : Map[ String, Int ] = read[ Map[ String, Int ] ]( resStr )
            resMap shouldBe Map( testKey1 -> 2, testKey2 -> 1 )
        }
    }

    behavior of "GET /concepts/explorer/state/key"

    it should "return 404 for non-existent key" in {
        Get( "/concepts/explorer/state/non-existent-key" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.NotFound
        }
    }

    it should "return latest version when key exists" in {
        import StateAccessApi.DeserializableConceptsStateJson
        val testData1 = testConceptsState( testTaxonomy )
        userDataStore.save( testUser, testKey1, testData1 ).awaitWrite
        Get( s"/concepts/explorer/state/${testKey1}" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr = responseAs[ String ]
            val resState = resStr.unmarshalConceptsState
            resState shouldBe testData1
        }

        val testData2 = testConceptsState( testTaxonomy.removeConcept( Seq( DartTaxonomyData.concept1.name, DartTaxonomyData.concept1b.name ) ).get )
        userDataStore.save( testUser, testKey1, testData2 ).awaitWrite
        Get( s"/concepts/explorer/state/${testKey1}" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr = responseAs[ String ]
            val resState = resStr.unmarshalConceptsState
            resState shouldBe testData2
        }
    }

    behavior of "GET /concepts/explorer/state/key/version"

    it should "return 404 for non-existent key" in {
        Get( "/concepts/explorer/state/non-existent-key/5" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.NotFound
        }
    }

    it should "return 404 for non-existent version" in {
        val testData1 = testConceptsState( testTaxonomy )
        userDataStore.save( testUser, testKey1, testData1 ).awaitWrite
        Get( s"/concepts/explorer/state/${testKey1}/5" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.NotFound
        }
    }

    it should "return 400 for version of zero" in {
        val testData1 = testConceptsState( testTaxonomy )
        userDataStore.save( testUser, testKey1, testData1 ).awaitWrite
        Get( s"/concepts/explorer/state/${testKey1}/0" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.BadRequest
        }
    }

    it should "return state corresponding to valid version" in {
        import StateAccessApi.DeserializableConceptsStateJson

        val testData1 = testConceptsState( testTaxonomy )
        userDataStore.save( testUser, testKey1, testData1 ).awaitWrite
        val testData2 = testConceptsState( testTaxonomy.removeConcept( Seq( DartTaxonomyData.concept1.name, DartTaxonomyData.concept1b.name ) ).get )
        userDataStore.save( testUser, testKey1, testData2 ).awaitWrite

        Get( s"/concepts/explorer/state/${testKey1}/1" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr = responseAs[ String ]
            val resState = resStr.unmarshalConceptsState
            resState shouldBe testData1
        }

        Get( s"/concepts/explorer/state/${testKey1}/2" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resStr = responseAs[ String ]
            val resState = resStr.unmarshalConceptsState
            resState shouldBe testData2
        }

        Get( s"/concepts/explorer/state/${testKey1}/3" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.NotFound
        }
    }

    behavior of "POST /concepts/explorer/state/key"

    it should "return 400 if body is not valid ConceptsState json" in {
        Post( s"/concepts/explorer/state/$testKey1", """{"json_key":"json_value"}""" ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.BadRequest
        }
    }

    it should "return 200 and number of versions if body is valid ConceptState json, and should save state" in {
        import StateAccessApi.SerializableConceptsState

        val testData1 = testConceptsState( testTaxonomy )
        val jsonBody1 : String = testData1.marshalJson
        Post( s"/concepts/explorer/state/$testKey1", jsonBody1 ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resInt = responseAs[ String ].toInt
            resInt shouldBe 1
        }

        userDataStore.getKeys( testUser ).await shouldBe Map( testKey1 -> 1 )
        userDataStore.getLatestVersion( testUser, testKey1 ).await shouldBe Some( testData1 )

        val testData2 =  testConceptsState( testTaxonomy.removeConcept( Seq( DartTaxonomyData.concept1.name ) ).get )
        val jsonBody2 : String = testData2.marshalJson
        Post( s"/concepts/explorer/state/$testKey1", jsonBody2 ) ~> StateAccessRoutes.route ~> check {
            status shouldBe StatusCodes.OK
            val resInt = responseAs[ String ].toInt
            resInt shouldBe 2
        }

        userDataStore.getKeys( testUser ).await shouldBe Map( testKey1 -> 2 )
        userDataStore.getLatestVersion( testUser, testKey1 ).await shouldBe Some( testData2 )
        userDataStore.getOldestVersion( testUser, testKey1 ).await shouldBe Some( testData1 )
    }
    override val authDependencies : SecureDartController.Dependencies = SecureDartController.deps(
        "cluster",
        SecureDartController.authDeps( None, useDartAuthIn = false, basicAuthCredsIn = Nil )
    )
}
