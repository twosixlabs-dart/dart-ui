//import Main.OntologyReader
//import akka.actor.typed.ActorSystem
//import akka.actor.typed.scaladsl.Behaviors
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.Http
//import better.files.Resource
//import com.mchange.v2.c3p0.ComboPooledDataSource
//import com.twosixlabs.dart.auth.controllers.SecureDartController
//import com.twosixlabs.dart.auth.tenant.CorpusTenant
//import com.twosixlabs.dart.auth.tenant.indices.{ArangoCorpusTenantIndex, InMemoryCorpusTenantIndex}
//import com.twosixlabs.dart.auth.user.DartUser
//import com.twosixlabs.dart.ontologies.OntologyRegistryService
//import com.twosixlabs.dart.ontologies.api.{OntologyArtifact, OntologyRegistry}
//import com.twosixlabs.dart.ontologies.dao.sql.SqlOntologyArtifactTable
//import com.twosixlabs.dart.ontologies.kafka.KafkaOntologyUpdatesNotifier
//import com.twosixtech.dart.concepts.client.RestClusteringClient
//import com.twosixtech.dart.taxonomy.explorer.api.{OntologyPublicationApiDI, RootApiDI, StateAccessApiDI}
//import com.twosixtech.dart.taxonomy.explorer.clustering.{RestClusteringServiceDI, TestClusteringServiceDI}
//import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterDI, DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
//import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
//import com.twosixtech.dart.taxonomy.explorer.provider.KafkaProvider
//import com.twosixtech.dart.taxonomy.explorer.publication.{OntologyPublicationServiceDeps, OntologyRegistryOntologyPublicationServiceDI}
//import com.twosixtech.dart.taxonomy.explorer.routes.{AuthRouterDI, ClusterRoutesDI, OntologyOutputRouteDI, OntologyPublicationRoutesDI, StateAccessRoutesDI, StaticRoutes, TaxonomyRoutesDI}
//import com.twosixtech.dart.taxonomy.explorer.serialization.{OntologyReaderDI, WmDartSerializationDI, WmOntologyWriterDI}
//import com.twosixtech.dart.taxonomy.explorer.userdata.{InMemoryUserDataStore, UserDataStore}
//import com.typesafe.config.ConfigFactory
//import org.apache.kafka.common.protocol.types.Field.UUID
//
//import scala.collection.mutable
//import scala.concurrent.duration.Duration
//import scala.concurrent.{Await, ExecutionContextExecutor, Future}
//import scala.io.StdIn
//import scala.util.{Failure, Try}
//
//trait DevOntologyPublicationServiceDI extends OntologyPublicationServiceDeps {
//    this : DartTaxonomyDI =>
//
//    class Register {
//        private var published : List[ DartTaxonomy ] = Nil
//        private var staged : Option[ DartTaxonomy ] = None
//
//        def stage( ontology : DartTaxonomy ) : Int = {
//            staged = Some( ontology )
//            1
//        }
//        def publish( ) : Option[ Int ] = {
//            val newPublishedOption = staged.map( _ :: published )
//            newPublishedOption.foreach( v => { published = v; staged = None } )
//            newPublishedOption.map( _.length )
//        }
//        def retrieveVersion( version : Option[ Int ] ) : Option[ DartTaxonomy ] = version match {
//            case None => published.headOption
//            case Some( v ) => published.lift( v - 1 )
//        }
//        def retrieveStaged() : Option[ DartTaxonomy ] = staged
//
//        def publishedCount : Option[ Int ] = if ( published.nonEmpty ) Some( published.length ) else None
//        def stagedCount : Option[ Int ] = staged.map( _ => 1 )
//    }
//
//    override val OntologyPublicationService : OntologyPublicationService = new OntologyPublicationService {
//
//        private val tenantMap : Map[ String, Register ] = Map( "tenant-1" -> new Register, "tenant-2" -> new Register )
//
//        import scala.concurrent.ExecutionContext.Implicits.global
//
//        override def stage(
//            tenant: String, ontology: DartTaxonomy
//        ): Future[ Int ] = Future( tenantMap( tenant ).stage( ontology ) )
//
//        override def publishStaged(
//            tenant: String
//        ): Future[ Option[ Int ] ] = Future( tenantMap( tenant ).publish() )
//
//        override def retrieve(
//            tenant: String, version: Option[ Int ]
//        ): Future[ Option[ DartTaxonomy ] ] = Future( tenantMap( tenant ).retrieveVersion( version ) )
//
//        override def retrieveStaged(
//            tenant: String, version: Option[ Int ]
//        ): Future[ Option[ DartTaxonomy ] ] = Future( tenantMap( tenant ).retrieveStaged() )
//
//        override def allTenants: Future[ Map[ String, TV ] ] = Future( tenantMap.mapValues( reg => {
//            TV( reg.publishedCount, reg.stagedCount )
//        } ) )
//    }
//}
//
//object DevMain
//  extends OntologyReaderDI
//    with WmDartSerializationDI
//    with WmDartConceptDI
//    with TaxonomyRoutesDI
//    with OntologyOutputRouteDI
//    with WmOntologyWriterDI
//    with DartTaxonomyDI
//    with UUIDTaxonomyIdDI
//    with UUIDTaxonomyIdSerializationDI
//    with StateAccessRoutesDI
//    with StateAccessApiDI
//    with RootApiDI
//    with DartClusterDI
//    with CuratedClusterDI
//    with RestClusteringServiceDI
//    with ClusterRoutesDI
//    with TestClusteringServiceDI
//    with AuthRouterDI
//    with WmDartClusterConceptBridgeDI
//    with OntologyPublicationRoutesDI
//    with OntologyPublicationApiDI
//    with DevOntologyPublicationServiceDI {
//
//    val config = ConfigFactory.load( "dart-ui-application.conf" )
//
//    implicit val system: ActorSystem[ Nothing ] = ActorSystem( Behaviors.empty, "my-system" )
//    // needed for the future flatMap/onComplete in the end
//    implicit val executionContext: ExecutionContextExecutor = system.executionContext
//
//    val clusteringService: ClusteringService = {
//        if ( config.getBoolean( "clustering.test.mode" ) ) TestClusteringService
//        else new RestClusteringService(
//            new RestClusteringClient(
//                config.getString( "clustering.host" ),
//                config.getInt( "clustering.port" ),
//                "dart/api/v1",
//                config.getString( "clustering.scheme" ),
//            )
//
//        )
//    }
//
//    val authRouterDeps = SecureDartController.deps(
//        serviceNameIn = "concepts-ui",
//        secretKeyIn = Try( config.getString( "dart.auth.secret" ) ).toOption,
//        bypassAuthIn = true,
//    )
//
//    override val authDependencies: SecureDartController.Dependencies = authRouterDeps
//    override val userDataStore: UserDataStore[ DartUser, StateAccessApi.ConceptsState ] = new
//        InMemoryUserDataStore[ DartUser, StateAccessApi.ConceptsState ]
//
//    def main( args: Array[ String ] ): Unit = {
//
//        val taxonomy1 = OntologyReader.ymlToOntology( Resource.getAsString( "wm-ontology.yml" ) ).get
//        val taxonomy2 = OntologyReader.ymlToOntology( Resource.getAsString( "wm2-ontology.yml" ) ).get
//
//        Await.result( OntologyPublicationService.stage( "tenant-1", taxonomy1 ), Duration.Inf )
//        Await.result( OntologyPublicationService.publishStaged( "tenant-1" ), Duration.Inf )
//        Await.result( OntologyPublicationService.stage( "tenant-2", taxonomy2 ), Duration.Inf )
//        Await.result( OntologyPublicationService.publishStaged( "tenant-2" ), Duration.Inf )
//
//        val taxonomyRoute = new TaxonomyRoutes( Map( "wm" -> taxonomy1, "wm2" -> taxonomy2 ) ).taxonomiesRoute
//        val clusterRoute = ClusterRoutes.clusterRoute
//        val staticRoute = new StaticRoutes(
//            publicDir = config.getString( "backend.public.dir" ),
//            publicResourceDir = "public",
//            defaultResource = "public/index.html",
//        ).staticRoute
//        val ontologyOutputRoute = OntologyOutputRoute.ontologyOutput
//        val stateAccessRoutes = StateAccessRoutes.routes
//        val ontologyPublicationRoutes = OntologyPublicationRoutes.routes
//
//        val bindingFuture = Http()
//          .newServerAt( "0.0.0.0", 8080 )
//          .bind(
//              stateAccessRoutes ~ ontologyPublicationRoutes ~ ontologyOutputRoute ~ clusterRoute ~ taxonomyRoute ~
//                staticRoute
//          )
//
//        println( s"Server online at http://0.0.0.0:8080/" )
//        if ( args.map( _.trim ).contains( "-i" ) ) {
//            println( "Press return to stop..." )
//            StdIn.readLine() // let it run until user presses return
//            bindingFuture
//              .flatMap( _.unbind() ) // trigger unbinding from the port
//              .onComplete( _ => system.terminate() ) // and shutdown when done
//        }
//
//    }
//
//}
