package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{PathMatchers, Route}
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.taxonomy.explorer.api.StateAccessApiDI
import com.twosixtech.dart.taxonomy.explorer.userdata.VersionedUserDataStore
import upickle.default._

import scala.util.{Failure, Success, Try}

trait StateAccessRoutesDI {
    this : StateAccessApiDI
      with AuthRouterDI =>

    val userDataStore : VersionedUserDataStore[ DartUser, StateAccessApi.ConceptsState ]

    object StateAccessRoutes {

        def postRoute( user : DartUser ) : Route = post {
            pathPrefix( PathMatchers.Segment ) { key =>
                pathEndOrSingleSlash {
                    decodeRequest {
                        entity( as[ String ] ) { stateString =>
                            import StateAccessApi.DeserializableConceptsStateJson
                            Try( stateString.unmarshalConceptsState ) match {
                                case Success( conceptsState ) =>
                                    onComplete( userDataStore.save( user, key, conceptsState ) ) {
                                        case Success( i : Int ) => complete( StatusCodes.OK, Nil, s"$i" )
                                        case Failure( e ) => complete( StatusCodes.InternalServerError, Nil, e.getMessage )
                                    }
                                case Failure( e ) =>
                                    complete( StatusCodes.BadRequest, Nil, s"Invalid request body: ${e.getMessage}" )
                            }
                        }
                    }
                }
            }
        }

        def getRoute( user : DartUser ) : Route = get {
            pathEndOrSingleSlash {
                onComplete( userDataStore.getKeys( user ) ) {
                    case Success( keys : Map[ String, Int ] ) => complete( 200, Nil, write( keys ) )
                    case Failure( e ) => complete( 500, Nil, e.getMessage )
                }
            } ~
            pathPrefix( PathMatchers.Segment ) { key =>
                pathEndOrSingleSlash {
                    onComplete( userDataStore.getLatestVersion( user, key ) ) {
                        case Success( None ) => complete( 404, Nil, s"No versions found for key ${key}" )
                        case Failure( e ) => complete( 500, Nil, e.getMessage )
                        case Success( Some( res ) ) =>
                            import StateAccessApi.SerializableConceptsState
                            complete( 200, Nil, res.marshalJson )
                    }
                }
            } ~
            pathPrefix( PathMatchers.Segment ) { key =>
              pathPrefix( PathMatchers.IntNumber ) { version =>
                if ( version == 0 ) complete( 400, Nil, "Version must be positive or negative integer (non-zero)" )
                else  pathEndOrSingleSlash {
                      onComplete( userDataStore.getVersion( user, key, version ) ) {
                          case Success( None ) => complete( 404, Nil, s"Version ${version} not found for key ${key}" )
                          case Failure( e ) => complete( 500, Nil, e.getMessage )
                          case Success( Some( res ) ) =>
                              import StateAccessApi.SerializableConceptsState
                              complete( 200, Nil, res.marshalJson )
                      }
                  }
              }
            }
        }

        def route: Route =
            pathPrefix( "concepts" / "explorer" / StateAccessApi.ENDPOINT.stripPrefix( "/" ) ) {
                AuthRouter.authRoute { user: DartUser =>
                    getRoute( user ) ~ postRoute( user )
                }
            }
    }
}
