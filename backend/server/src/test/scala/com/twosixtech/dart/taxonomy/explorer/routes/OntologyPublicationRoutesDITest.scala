package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twosixlabs.dart.auth.controllers.SecureDartController
import com.twosixtech.dart.taxonomy.explorer.api.{OntologyPublicationApiDI, RootApiDeps}
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.publication.OntologyPublicationServiceDeps
import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import scala.util.Try

class OntologyPublicationRoutesDITest
  extends AnyFlatSpecLike
    with Matchers
    with ScalatestRouteTest
    with MockFactory
    with OntologyPublicationRoutesDI
    with AuthRouterDI
    with DartTaxonomyDI
    with OntologyPublicationServiceDeps
    with OntologyPublicationApiDI
    with WmDartSerializationDI
    with RootApiDeps
    with WmDartConceptDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI {

    override lazy val authDependencies : SecureDartController.Dependencies = {
        val authdeps = SecureDartController.authDeps( None, true )
        SecureDartController.deps( "test", authdeps )
    }

    override lazy val OntologyPublicationService : OntologyPublicationService = stub[ OntologyPublicationService ]

    override lazy val RootApi : RootApi = new RootApi {
        override val BASE_PATH : String = "/test"
    }

    val testTaxonomy = DartTaxonomy( Set.empty ).addConcept( DartConcept( "test", Set.empty ), Seq.empty ).get

    behavior of "POST /stage"

    it should "return 201 with staging version" in {
        ( OntologyPublicationService.stage( _, _ ) ).when( *, * ).returns( Future.successful( 3 ) )

        Post( s"${submitStagedPath}/test-tenant", testTaxonomy.stageSubmissionJson ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 201
            responseAs[ String ].parseStageResponse shouldBe 3
        }
    }

    behavior of "POST /publish"

    it should "return 201 with new version if ontology publication service returns defined option" in {
        ( OntologyPublicationService.publishStaged _ ).when( * ).returns( Future.successful( Some( 3 ) ) )

        Post( s"${publishPath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 201
            responseAs[ String ].parseStageResponse shouldBe 3
        }
    }

    it should "return 404 if ontology publication service returns empty option" in {
        ( OntologyPublicationService.publishStaged _ ).when( * ).returns( Future.successful( None ) )

        Post( s"${publishPath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 404
            responseAs[ String ] shouldBe ( "no staged version exists for tenant test-tenant" )
        }
    }

    behavior of "GET /staged"

    it should "return 200 with taxonomy if ontology publication service returns result" in {
        ( OntologyPublicationService.retrieveStaged _ ).when( *, * ).returns( Future.successful( Some( testTaxonomy ) ) )

        Get( s"${retrieveStagedPath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 200
            responseAs[ String ].parseStageRequest shouldBe testTaxonomy
        }
    }

    it should "return 404 with if ontology publication service returns empty option" in {
        ( OntologyPublicationService.retrieveStaged _ ).when( *, * ).returns( Future.successful( None ) )

        Get( s"${retrieveStagedPath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 404
            responseAs[ String ] shouldBe "no staged version exists for tenant test-tenant"
        }
    }

    behavior of "GET /published"

    it should "return 200 with taxonomy if ontology publication service returns result" in {
        ( OntologyPublicationService.retrieve _ ).when( *, * ).returns( Future.successful( Some( testTaxonomy ) ) )

        Get( s"${retrievePath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 200
            responseAs[ String ].parseStageRequest shouldBe testTaxonomy
        }
    }

    it should "return 404 with if ontology publication service returns empty option" in {
        ( OntologyPublicationService.retrieve _ ).when( *, * ).returns( Future.successful( None ) )

        Get( s"${retrievePath}/test-tenant" ) ~> OntologyPublicationRoutes.route ~> check {
            status.intValue() shouldBe 404
            responseAs[ String ] shouldBe "no published version exists for tenant test-tenant"
        }
    }


}
