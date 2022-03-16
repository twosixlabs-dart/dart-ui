package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.server.{PathMatchers, Route}
import com.twosixtech.dart.taxonomy.explorer.api.{RootApiDeps, UserDataApiDI}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.taxonomy.explorer.userdata.UserDataStore

import scala.util.{Failure, Success, Try}

trait UserDataRoutesDI
  extends UserDataApiDI {
    this : RootApiDeps
      with AuthRouterDI =>

  class UserDataRoutes[ Data ](
    userDataService : UserDataStore[ DartUser, Data ],
    serializeData : Data => String,
    deserializeData : String => Data,
  ) {
    private lazy val retrieveData = get {
      pathPrefix( PathMatchers.Segment ) { key =>
        pathEndOrSingleSlash {
          AuthRouter.authRoute { user =>
            onComplete( userDataService.get( user, key ) ) {
              case Success( None ) =>
                complete( StatusCodes.NotFound, Nil, s"No data under key $key" )
              case Success( Some( data ) ) =>
                complete( StatusCodes.OK, Nil, serializeData( data ) )
              case Failure( exception ) =>
                complete( StatusCodes.InternalServerError, Nil, exception.getMessage )
            }
          }
        }
      }
    }

    private lazy val submitData = post {
      pathPrefix( PathMatchers.Segment ) { key =>
        pathEndOrSingleSlash {
          AuthRouter.authRoute { user =>
            decodeRequest {
              entity( as[ String ] ) { dataString =>
                Try( deserializeData( dataString ) ) match {
                  case Success( data ) =>
                    onComplete( userDataService.save( user, key, data ) ) {
                      case Success( () ) =>
                        complete( StatusCodes.Created )
                      case Failure( exception ) =>
                        complete( StatusCodes.InternalServerError, Nil, exception.getMessage )
                    }
                  case Failure( exception ) =>
                    complete(
                      StatusCodes.BadRequest,
                      Nil,
                      s"Unable to deserialized data: ${exception.getMessage}",
                    )
                }
              }
            }
          }
        }
      }
    }

    def route : Route =
      pathPrefix( "concepts" / "explorer" / "user" / "data" ) {
        retrieveData ~ submitData
      }
  }
}
