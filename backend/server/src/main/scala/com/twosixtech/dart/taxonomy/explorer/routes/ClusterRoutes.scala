package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{PathMatcher, Route}
import com.twosixtech.dart.taxonomy.explorer.api.ClusteringApiDI
import com.twosixtech.dart.taxonomy.explorer.clustering.ClusteringServiceDeps
import com.twosixtech.dart.taxonomy.explorer.models.Cluster
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps

import java.util.UUID
import scala.util.{Failure, Success, Try}
import upickle.default._

trait ClusterRoutesDI {
    this : ClusteringServiceDeps[ UUID ]
      with AuthRouterDI
      with ClusteringApiDI
      with DartSerializationDeps =>

    object ClusterRoutes {

        implicit class StringPathMatcher( pm : String ) {
            def pathMatch : PathMatcher[ Unit ] = {
                val trimmedPm = pm.trim
                if ( trimmedPm.startsWith( "/" ) ) {
                    val fixedPm = trimmedPm.stripPrefix( "/" )
                    Slash ~ fixedPm.pathMatch
                } else {
                    val splitPm = trimmedPm.split( "/" ).toSeq
                    val (firstSegment, otherSegments) = (splitPm.head, splitPm.tail)
                    otherSegments.foldLeft( firstSegment : PathMatcher[ Unit ] )( ( currentPm, nextSegment ) => currentPm / nextSegment.trim )
                }
            }
        }

        import ClusteringApi._

        private def postInitialClustering( tenantId : String ) : Route = post {
            onComplete( ClusteringService.initialClustering( tenantId ) ) {
                case Success( _ ) =>
                    complete( status = 200, Nil, "" )
                case Failure( e ) =>
                    complete( status = 500, Nil, e.getMessage )
            }
        }

        private def getClusterRoute( jobIdOpt : Option[ UUID ] ) : Route = get {
            onComplete( ClusteringService.clusterResults( jobIdOpt ) ) {
                case Success( res ) =>
                    complete( status = 200, Nil, res.marshalJson )
                case Failure( e ) =>
                    complete( status = 500, Nil, e.getMessage )
            }
        }

        private def getRescoreRoute( jobIdOpt : Option[ UUID ] ) : Route = get {
            onComplete( ClusteringService.rescoreResults( jobIdOpt ) ) {
                case Success( res ) =>
                    complete( status = 200, Nil, res.marshalJson )
                case Failure( e ) =>
                    complete( status = 500, Nil, e.getMessage )
            }
        }

        private def postReclusterRoute( jobIdOpt : Option[ UUID ] ) : Route = post {
            decodeRequest {
                entity( as[ String ] ) { str =>
                    import ClusteringApi._

                    val req = read[ DartReclusterRequest ]( str )
                    val taxonomy = DartSerialization.jsonToTaxonomy( req.ontologyJson )
                    onComplete( ClusteringService.recluster( req.phrases.toSet, taxonomy, jobIdOpt ) ) {
                        case Success( id ) =>
                            if ( id == null ) complete( 503, Nil, "Unable to submit job" )
                            else complete( 200, Nil, id.toString )
                        case Failure( e ) => complete( status = 500, Nil, e.getMessage )
                    }
                }
            }
        }

        private lazy val postRescoreRoute : Route = post {
            decodeRequest {
                entity( as[ String ] ) { str =>
                    import ClusteringApi._

                    val req = read[ RescoreRequest ]( str )
                    val taxonomy = DartSerialization.jsonToTaxonomy( req.ontologyJson )
                    onComplete( ClusteringService.rescore( req.clusterJobId, taxonomy ) ) {
                        case Success( id ) =>
                            if ( id == null ) complete( 503, Nil, "Unable to submit job" )
                            else complete( 200, Nil, id.toString )
                        case Failure( e ) =>
                            Try( Option( e.getMessage ) ).toOption.flatten match {
                                case None => complete( 500, Nil, "unknown failure" )
                                case Some( msg ) =>
                                    complete( status = 500, Nil, msg )
                            }
                    }
                }
            }
        }

        private lazy val initialClusteringRoute = rawPathPrefix( initialClusteringEndpoint.pathMatch ) {
            path( Segment ) { tenantId : String =>
                AuthRouter.authRoute { _ => postInitialClustering( tenantId ) }
            }
        }

        private lazy val rescoreSubmitRoute = rawPathPrefix( rescoreSubmitEndpoint.pathMatch ) {
            pathEndOrSingleSlash {
                AuthRouter.authRoute { _ => postRescoreRoute }
            }
        }

        private lazy val reclusterSubmitRoute = rawPathPrefix( reclusterSubmitEndpoint.pathMatch ) {
            pathEndOrSingleSlash {
                AuthRouter.authRoute { _ => postReclusterRoute( None ) }
            } ~
            path( JavaUUID ) { uuid : UUID =>
                AuthRouter.authRoute { _ => postReclusterRoute( Some( uuid ) ) }
            }
        }

        private lazy val rescoreResultsRoute = rawPathPrefix( rescoreResultsEndpoint.pathMatch ) {
            pathEndOrSingleSlash {
                AuthRouter.authRoute { _ => getRescoreRoute( None ) }
            } ~
            path( JavaUUID ) { uuid : UUID =>
                AuthRouter.authRoute { _ => getRescoreRoute( Some( uuid ) ) }
            }
        }

        private lazy val reclusterResultsRoute = rawPathPrefix( reclusterResultsEndpoint.pathMatch ) {
            pathEndOrSingleSlash {
                AuthRouter.authRoute { _ => getClusterRoute( None ) }
            } ~
            path( JavaUUID ) { uuid : UUID =>
                AuthRouter.authRoute { _ => getClusterRoute( Some( uuid ) ) }
            }
        }

        def route : Route = {
            initialClusteringRoute ~
              reclusterResultsRoute ~
              rescoreResultsRoute ~ reclusterSubmitRoute ~
              rescoreSubmitRoute
        }
    }

}
