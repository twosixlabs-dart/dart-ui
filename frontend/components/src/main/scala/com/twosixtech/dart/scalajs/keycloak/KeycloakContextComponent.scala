package com.twosixtech.dart.scalajs.keycloak

import com.twosixtech.dart.scalajs.backend.{BackendClient, HttpMethod, HttpRequest, HttpResponse}
import com.twosixtech.dart.scalajs.keycloak.KeycloakContextComponent.KeycloakContext
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted, builder}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.|
import scala.util.{Failure, Success, Try}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

class KeycloakBackendContextComponent[ IdToken ](
    params : KeycloakParams | String,
    init : KeycloakInit,
    readToken : js.Object => IdToken,
) extends ReactComponent[ KeycloakContextComponent.Props[ IdToken ], KeycloakContextComponent.State[ IdToken ] ] {
    override type BackendType = Backend

    class Backend( scope : BackendScope[ KeycloakContextComponent.Props[ IdToken ], KeycloakContextComponent.State[ IdToken ] ] ) {

        private val keycloak = new Keycloak( params )
        keycloak.init( init )
          .toFuture
          .onComplete {
              case Success( true ) =>
                  ( for {
                      props <- scope.props
                      _ <- props.onInit
                      _ <- scope.modState( _.copy(
                          token = Option( keycloak.token ),
                          idToken = Option( keycloak.idTokenParsed ).map( readToken ),
                          initialized = true,
                      ) )
                  } yield () ).runNow()
              case Success( false ) =>
                  println( "Failed to init without exception" )
                  ( for {
                      props <- scope.props
                      _ <- props.onInit
                      _ <- Callback( keycloak.login( KeycloakLoginOptions() ) )
                  } yield () ).runNow()
              case Failure( e ) =>
                  println( "Failed to init with exception:\n\n" )
                  e.printStackTrace()
          }

        keycloak.updateToken( 30 )
          .toFuture
          .onComplete( _ => update.runNow() )

        def update : Callback = Callback {
            keycloak.onTokenExpired = () => {
                val tokenRefreshFuture = keycloak.updateToken( 30 )
                  .toFuture
                  .transform {
                      case Success( _ ) =>
                          Success( (Option( keycloak.token ), Option( keycloak.idTokenParsed ).map( readToken ) ) )
                      case Failure( _ ) => Failure( new KeycloakContextComponent.KeycloakTokenRefreshFailure )
                  }

                tokenRefreshFuture.onComplete {
                    case Success( (tokenOpt, idTokenOpt) ) =>
                        ( for {
                            _ <- scope.modState( _.copy(
                                token = tokenOpt,
                                idToken = idTokenOpt,
                                initialized = true,
                            ) )
                            props <- scope.props
                            _ <- props.handleTokenRefresh( Try( ( tokenOpt.get, idTokenOpt.get ) ) )
                        } yield () ).runNow()
                    case Failure( exception ) =>
                        ( for {
                            _ <- scope.modState( _.copy(
                                token = None,
                                idToken = None,
                                initialized = true,
                            ) )
                            props <- scope.props
                            _ <- props.handleTokenRefresh( Failure( exception ) )
                        } yield () ).runNow()
                }
            }
        }

        def render( props : KeycloakContextComponent.Props[ IdToken ], state : KeycloakContextComponent.State[ IdToken ] ) : VdomNode = {
            val updateTokenCallback : CallbackTo[ Future[ (String, IdToken) ] ] = CallbackTo {
                keycloak.updateToken( -1 )
                  .toFuture
                  .map( _ => {
                      update.runNow()
                      (keycloak.token, readToken( keycloak.idTokenParsed ))
                  } )
            }

            if ( state.initialized )
                props.render( KeycloakContextComponent.KeycloakContext[ IdToken ](
                    token = state.token,
                    idToken = state.idToken,
                    props.client,
                    updateTokenCallback,
                ) )
            else EmptyVdom
        }
    }

    val component = ScalaComponent.builder[ KeycloakContextComponent.Props[ IdToken ] ]
      .initialState( KeycloakContextComponent.State[ IdToken ]() )
      .backend( new Backend( _ ) )
      .renderBackend
      .build

    override def apply(
        props : KeycloakContextComponent.Props[ IdToken ]
    ) : Unmounted[ KeycloakContextComponent.Props[ IdToken], KeycloakContextComponent.State[ IdToken ], Backend ] =
        component( props )

    def apply(
        render : KeycloakContext[ IdToken ] => VdomElement,
        client : BackendClient,
        onInit : Callback = Callback(),
        handleTokenRefresh : Try[ (String, IdToken) ] => Callback = {
            ( _ : Try[ (String, IdToken) ] ) => Callback()
        },
        minValidity : Int = 30,
    ) : Unmounted[ KeycloakContextComponent.Props[ IdToken], KeycloakContextComponent.State[ IdToken ], Backend ] =
        component( KeycloakContextComponent.Props(
            render,
            client,
            onInit,
            handleTokenRefresh,
            minValidity,
        ) )
}

import scalajs.concurrent.JSExecutionContext.Implicits.queue

object KeycloakContextComponent {
    class KeycloakTokenRefreshFailure extends Exception( "Keycloak failed to refresh token" )

    case class KeycloakContext[ IdToken ](
        token : Option[ String ] = None,
        idToken : Option[ IdToken ] = None,
        backendClient : BackendClient,
        updateToken : CallbackTo[ Future[ (String, IdToken) ] ],
    ) {
        object Implicits {
            implicit class EnrichedXhrRequest( req : HttpRequest ) {
                def withToken( tk : String ) : HttpRequest = {
                    val newReq = req.addHeader(
                        "Authorization",
                        s"Bearer $tk",
                    )
                    newReq
                }
            }

            implicit class HandledXhrResponse( res : Future[ HttpResponse ] ) {
                def authHandled : Future[ HttpResponse ] = res transform {
                    case origRes@Success( HttpResponse( _, status, _ ) ) if ( status == 401 ) =>
                        updateToken.runNow()
                        origRes
                    case origRes => origRes
                }
            }
        }

        import Implicits._

        val authClient : BackendClient =
            ( method : HttpMethod, request : HttpRequest, onProgress : Double => Unit ) => for {
                tk <- token match {
                    case None => {
                        updateToken.runNow().map( _._1 )
                    }
                    case Some( t ) =>
                        Future.successful( t )
                }
                res <- backendClient.submit( method, request.withToken( tk ), onProgress )
            } yield res
    }

    case class Props[ IdToken ](
        render : KeycloakContext[ IdToken ] => VdomElement,
        client : BackendClient,
        onInit : Callback = Callback(),
        handleTokenRefresh : Try[ (String, IdToken) ] => Callback = {
            ( _ : Try[ (String, IdToken) ] ) => Callback()
        },
        minValidity : Int = 30,
    )

    case class State[ IdToken ](
        token : Option[ String ] = None,
        idToken : Option[ IdToken ] = None,
        initialized : Boolean = false,
    )
}
