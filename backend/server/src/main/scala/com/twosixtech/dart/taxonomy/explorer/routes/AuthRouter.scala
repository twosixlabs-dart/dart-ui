package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.twosixlabs.dart.auth.controllers.{DartAuthHttpExtractor, SecureDartController}
import com.twosixlabs.dart.auth.groups.ProgramManager
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixlabs.dart.exceptions.AuthenticationException

import scala.util.{Failure, Success, Try}

trait AuthRouterDI {

    val authDependencies : SecureDartController.Dependencies


    import authDependencies._

    object AuthRouter extends DartAuthHttpExtractor {
        val authBypassUser : DartUser = DartUser( "program-manager", Set( ProgramManager ) )

        def authRoute( authenticatedRoute : DartUser => Route  ) : Route = {
            if ( bypassAuth ) {
                authenticatedRoute( authBypassUser )
            } else optionalHeaderValue {
                case HttpHeader( key, value ) if key.trim.toLowerCase() == "authorization" =>
                    Some( value )
                case _ => None
            } {
                case None => complete( StatusCodes.Forbidden -> "Missing authorization header" )
                case Some( authHeaderValue ) =>
                    Try( userFromAuthHeader( "Authorization", authHeaderValue, secretKey, serviceName ) ) match {
                        case Success( dartUser ) => authenticatedRoute( dartUser )
                        case Failure( e : AuthenticationException ) =>
                            complete( StatusCodes.Forbidden -> e.getMessage )
                        case Failure( e ) => complete( StatusCodes.InternalServerError -> e.getMessage )
                    }
            }
        }
    }

}
