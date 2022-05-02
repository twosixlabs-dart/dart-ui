import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import better.files.Resource
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.twosixlabs.dart.arangodb.{ Arango, ArangoConf }
import com.twosixlabs.dart.arangodb.tables.CanonicalDocsTable
import com.twosixlabs.dart.auth.controllers.SecureDartController
import com.twosixlabs.dart.auth.tenant.indices.ArangoCorpusTenantIndex
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixlabs.dart.ontologies.OntologyRegistryService
import com.twosixlabs.dart.ontologies.dao.sql.SqlOntologyArtifactTable
import com.twosixlabs.dart.ontologies.kafka.KafkaOntologyUpdatesNotifier
import com.twosixtech.dart.concepts.client.RestClusteringClient
import com.twosixtech.dart.taxonomy.explorer.api.{ OntologyPublicationApiDI, RootApiDI, StateAccessApiDI }
import com.twosixtech.dart.taxonomy.explorer.clustering.{ RestClusteringServiceDI, TestClusteringServiceDI }
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{ CuratedClusterDI, DartClusterDI, DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI }
import com.twosixtech.dart.taxonomy.explorer.provider.KafkaProvider
import com.twosixtech.dart.taxonomy.explorer.publication.OntologyRegistryOntologyPublicationServiceDI
import com.twosixtech.dart.taxonomy.explorer.routes.{ AuthRouterDI, ClusterRoutesDI, OntologyOutputRouteDI, OntologyPublicationRoutesDI, StateAccessRoutesDI, StaticRoutes, TaxonomyRoutesDI, UserDataRoutesDI }
import com.twosixtech.dart.taxonomy.explorer.serialization.{ OntologyReaderDI, WmDartSerializationDI, WmOntologyWriterDI }
import com.twosixtech.dart.taxonomy.explorer.userdata.VersionedUserDataStore
import com.twosixtech.dart.taxonomy.explorer.userdata.UserDataStore
import com.twosixtech.dart.taxonomy.explorer.userdata.postgres.{ PgUserDataStore, PgVersionedUserDataStoreDI }
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationDouble
import scala.io.StdIn
import scala.util.Try

object Main
  extends OntologyReaderDI
    with WmDartSerializationDI
    with WmDartConceptDI
    with TaxonomyRoutesDI
    with OntologyOutputRouteDI
    with WmOntologyWriterDI
    with DartTaxonomyDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI
    with StateAccessRoutesDI
    with StateAccessApiDI
    with RootApiDI
    with DartClusterDI
    with CuratedClusterDI
    with RestClusteringServiceDI
    with ClusterRoutesDI
    with TestClusteringServiceDI
    with AuthRouterDI
    with WmDartClusterConceptBridgeDI
    with PgVersionedUserDataStoreDI
    with OntologyPublicationRoutesDI
    with OntologyPublicationApiDI
    with OntologyRegistryOntologyPublicationServiceDI
    with UserDataRoutesDI {

    val config = ConfigFactory.load( "dart-ui-application.conf" )

    implicit val system : ActorSystem[ Nothing ] = ActorSystem( Behaviors.empty, "my-system" )
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext : ExecutionContextExecutor = system.executionContext

    override lazy val ClusteringService : ClusteringService = {
        if ( config.getBoolean( "clustering.test.mode" ) ) {
            println( "TEST MODE!" )
            TestClusteringService
        } else {
            println( "NOT TEST MODE!" )
            new RestClusteringService(
                new RestClusteringClient(
                    config.getString( "clustering.host" ),
                    config.getInt( "clustering.port" ),
                    "dart/api/v1",
                    config.getString( "clustering.scheme" ),
                )
            )
        }
    }

    val dataSource : ComboPooledDataSource = {
        val ds = new ComboPooledDataSource()
        ds.setDriverClass( config.getString( "postgres.driver.class" ) )
        val pgHost = config.getString( "postgres.host" )
        val pgPort = config.getInt( "postgres.port" )
        val pgDb = config.getString( "postgres.database" )
        ds.setJdbcUrl( s"jdbc:postgresql://$pgHost:$pgPort/$pgDb" )
        ds.setUser( config.getString( "postgres.user" ) )
        ds.setPassword( config.getString( "postgres.password" ) )
        Try( config.getInt( "postgres.minPoolSize" )  ).foreach( v => ds.setMinPoolSize( v ) )
        Try( config.getInt( "postgres.acquireIncrement" )  ).foreach( v => ds.setAcquireIncrement( v ) )
        Try( config.getInt( "postgres.maxPoolSize" )  ).foreach( v => ds.setMaxPoolSize( v ) )
        ds
    }

    lazy private val tenantIndex = ArangoCorpusTenantIndex( config )

    lazy private val arango = new Arango( ArangoConf(
        host = config.getString( "arangodb.host" ),
        port = config.getInt( "arangodb.port" ),
        database = config.getString( "arangodb.database" )
    ) )

    lazy private val docsTable = new CanonicalDocsTable( arango )

    lazy private val kafkaProducer = new KafkaProvider( config.getConfig( "kafka" ) ).newProducer[ String, String ]

    lazy val timeoutMinutes : Double = config.getDouble( "postgres.timeout.minutes" )

    import com.twosixlabs.dart.ontologies.dao.sql.PgSlickProfile.api.{Database => OntologiesDb}

    val ontologyDb : OntologiesDb =
        OntologiesDb.forDataSource( dataSource, Try( config.getInt( "postgres.max.connections" ) ).toOption )

    override val ontologyPublicationServiceParams : OntologyPublicationServiceParams =
        OntologyPublicationServiceParams(
            system.executionContext,
            tenantIndex,
            new OntologyRegistryService( new SqlOntologyArtifactTable( ontologyDb, timeoutMinutes.minutes, system.executionContext ) ),
            new KafkaOntologyUpdatesNotifier(
                tenantIndex,
                docsTable,
                kafkaProducer,
                config.getString( "updates.topic" ),
            )
        )

    private val taxonomy = OntologyReader.ymlToOntology( Resource.getAsString( "wm-ontology.yml" ) ).get
    private val taxonomy2 = OntologyReader.ymlToOntology( Resource.getAsString( "wm2-ontology.yml" ) ).get

    private val authRouterDeps = SecureDartController.deps(
        serviceNameIn = "dart-ui",
        config,
    )

    override val authDependencies : SecureDartController.Dependencies = authRouterDeps

    import com.twosixtech.dart.taxonomy.explorer.userdata.postgres.PgSlickProfile.api.{Database => UserDataDb}

    val userDataDb : UserDataDb =
        UserDataDb.forDataSource( dataSource, Try( config.getInt( "postgres.max.connections" ) ).toOption )

    override val userDataStore : VersionedUserDataStore[ DartUser, Main.StateAccessApi.ConceptsState ] = {
        new PgVersionedUserDataStore( "dart-ui-concepts", userDataDb )
    }

    lazy val dartUiUserDataStore : UserDataStore[ DartUser, String ] =
        new PgUserDataStore[ DartUser, String ](
            "dart-ui-user-data",
            userDataDb,
        ) {
            override def marshalUser(user: DartUser): String = user.userName
            override def unmarshalUser(user: String): DartUser = DartUser( user, Set.empty )
            override def marshalData(data: String): String = data
            override def unmarshalData(data: String): String = data
        }

    def main( args : Array[ String ] ) : Unit = {

        val taxonomyRoute = new TaxonomyRoutes( Map( "wm" -> taxonomy, "wm2" -> taxonomy2 ) ).route
        val clusterRoute = ClusterRoutes.route
        val publicDir = config.getString( "backend.public.dir" )
        val staticRoute = new StaticRoutes(
            publicDir = publicDir,
            defaultFile = publicDir + "/index.html",
        ).route
        val ontologyOutputRoute = OntologyOutputRoute.route
        val stateAccessRoutes = StateAccessRoutes.route
        val ontologyPublicationRoutes = OntologyPublicationRoutes.route
        lazy val userDataRoutes =
            new UserDataRoutes[ String ]( dartUiUserDataStore, v => v, v => v ).route

        val bindingFuture = Http()
          .newServerAt( "0.0.0.0", 8080 )
          .bind( stateAccessRoutes ~
                userDataRoutes ~
                ontologyPublicationRoutes ~
                ontologyOutputRoute ~
                clusterRoute ~
                taxonomyRoute ~
                staticRoute )

        println( s"Server online at http://0.0.0.0:8080/" )
        if ( args.map( _.trim ).contains( "-i" ) ) {
            println( "Press return to stop..." )
            StdIn.readLine() // let it run until user presses return
            bindingFuture
              .flatMap( _.unbind() ) // trigger unbinding from the port
              .onComplete( _ => system.terminate() ) // and shutdown when done
        }

    }

}
