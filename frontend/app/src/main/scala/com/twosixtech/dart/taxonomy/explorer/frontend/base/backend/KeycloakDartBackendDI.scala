package com.twosixtech.dart.taxonomy.explorer.frontend.base.backend

import com.twosixlabs.dart.auth.groups.DartGroup
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.scalajs.backend.{BackendClient, BackendComponent, HttpMethod, HttpRequest, HttpResponse, XhrBackendClient}
import com.twosixtech.dart.scalajs.keycloak.{Keycloak, KeycloakContextComponent, KeycloakLoginOptions}
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.loading.mui.LoadingMui
import com.twosixtech.dart.scalajs.layout.loading.mui.LoadingMui.LoadingCircularMui
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.util.{Failure, Success, Try}
import scalajs.concurrent.JSExecutionContext.Implicits.queue


@js.native
trait DartIdTokenRaw extends js.Object {
    val group : js.Array[ String ] = js.native
    val preferred_username : String = js.native
    val aud : js.Array[ String ] = js.native
    val exp : Int = js.native
}

trait KeycloakXhrDartBackendDI
  extends DartBackendDeps {
    this : DartConfigDeps
      with ErrorHandlerDI =>

    class KeycloakBackend extends DartBackend {
        override def client : BackendClient = XhrBackendClient

        // Returns user and time of expiration in ms
        def tokenReader( rawIdToken : js.Object ) : (DartUser, Int) = {
            val typedRawIdToken = rawIdToken.asInstanceOf[ DartIdTokenRaw ]
            val usr = DartUser(
                userName = typedRawIdToken.preferred_username,
                groups = typedRawIdToken.group.toSet.map( ( v : String ) => DartGroup.fromString( v.trim.stripPrefix( "/" ) ) ),
            )
            (usr, typedRawIdToken.exp)
        }

        case class KeycloakContext(
            override val client : BackendClient,
            override val user : Option[ DartUser ],
            exp : Option[ Int ],
            token : Option[ String ],
            updateToken : CallbackTo[ Future[ (String, DartUser, Int) ] ],
        ) extends Context {
            implicit class EnrichedXhrRequest( req : HttpRequest ) {
                def withToken( tk : String ) : HttpRequest = {
                    val newReq = req.addHeader(
                        "Authorization",
                        s"Bearer $tk",
                    )
                    newReq
                }
            }

            override val authClient : BackendClient = new BackendClient {
                override def submit(
                    method : HttpMethod,
                    request : HttpRequest,
                    onProgress : Double => Unit = ( _ : Double ) => {},
                ) : Future[ HttpResponse ] = {
                    for {
                        tk <- updateToken.runNow().map( _._1 )
                        res <- client.submit( method, request.withToken( tk ) )
                    } yield res
                }
            }
        }

        case class State(
            token : Option[ String ] = None,
            user : Option[ DartUser ] = None,
            exp : Option[ Int ] = None,
            initialized : Boolean = false,
        )

        override type Cx = KeycloakContext
        override type St = State

        override def emptyState : State = State()

        override def genContext(
            authClient : BackendClient,
            user : Option[ DartUser ],
        ) : KeycloakContext = new KeycloakContext(
            client = client,
            user = user,
            exp = None,
            token = None,
            updateToken = CallbackTo( Future.failed[ (String, DartUser, Int) ]( new IllegalAccessException( "Auth is diabled" ) ) )
        ) {
            override val authClient : BackendClient = client
        }

        override def enabledContextComponent : BackendComponent[ KeycloakContext, State ] = new BackendComponent[ KeycloakContext, State ] {
            override type BackendType = Backend

            class Backend( scope : BackendScope[ KeycloakContext => VdomElement, State ] ) {

                private val keycloak = new Keycloak( dartConfig.keycloakParams )

                private def initKeycloak() : Unit = {
                    keycloak.init( dartConfig.keycloakInit )
                      .toFuture
                      .onComplete {
                          case Success( true ) =>
                              val newToken = Option( keycloak.token )
                              val tokenDataOpt = Option( keycloak.idTokenParsed ).map( tokenReader )
                              val newUser = tokenDataOpt.map( _._1 )
                              val newExp = tokenDataOpt.map( _._2 )
                              if ( newToken.isEmpty ) {
                                  window.console.log( "Initialized keycloak but no token" )
                                  Try( keycloak.login( KeycloakLoginOptions() ) )
                                    .getOrElse( window.setTimeout( () => initKeycloak(), 5000 ) )
                              } else scope.modState( _.copy(
                                  token = newToken,
                                  user = newUser,
                                  exp = newExp,
                                  initialized = true,
                              ) ).runNow()
                          case Success( false ) =>
                              window.console.log( "Failed to initialize keycloak" )
                              Try( keycloak.login( KeycloakLoginOptions() ) )
                                .getOrElse( window.setTimeout( () => initKeycloak(), 5000 ) )
                          case Failure( e ) =>
                              window.console.log( "Failed to initialize keycloak with exception:\n\n" )
                              e.printStackTrace()
                              window.setTimeout( () => initKeycloak(), 5000 )
                      }
                }

                initKeycloak()

                keycloak.onTokenExpired = () => {
                    keycloak.updateToken( 30 )
                      .toFuture
                      .onComplete {
                          case Success( true ) =>
                              val tokenDataOpt = Option( keycloak.idTokenParsed ).map( tokenReader )
                              val newUser = tokenDataOpt.map( _._1 )
                              val newExp = tokenDataOpt.map( _._2 )
                              scope.modState( _.copy(
                                  token = Option( keycloak.token ),
                                  user = newUser,
                                  exp = newExp,
                                  initialized = true,
                              ) ).runNow()
                          case Success( false ) =>
                              scope.modState( _.copy(
                                  token = None,
                                  user = None,
                                  exp = None,
                              ) ).runNow()
                              Try( keycloak.login( KeycloakLoginOptions() ) )
                                .getOrElse( window.setTimeout( keycloak.onTokenExpired, 5000 ) )
                          case Failure( e ) =>
                              window.console.log( "Failed to update token" )
                              e.printStackTrace()
                              scope.modState( _.copy(
                                  token = None,
                                  user = None,
                              ) ).runNow()
                      }
                }

                def render( renderer : KeycloakContext => VdomElement, state : State ) : VdomNode = {
                    val updateTokenCallback : CallbackTo[ Future[ (String, DartUser, Int) ] ] = CallbackTo {
                        keycloak.updateToken( 30 )
                          .toFuture
                          .flatMap( refreshed => {
                              val (newUser, newExp) = tokenReader( keycloak.idTokenParsed )
                              if ( refreshed ) {
                                  Future( scope.modState( _.copy(
                                      token = Option( keycloak.token ),
                                      user = Option( newUser ),
                                      exp = Option( newExp ),
                                      initialized = true,
                                  ) ).runNow() ).map( _ => (keycloak.token, newUser, newExp) )
                              } else Future.successful((keycloak.token, newUser, newExp))

                          } )
                    }

                    if ( state.initialized )
                        renderer( KeycloakContext(
                            client = client,
                            user = state.user,
                            exp = state.exp,
                            token = state.token,
                            updateToken = updateTokenCallback,
                        ))
                    else <.div(
                        ^.position := "absolute",
                        ^.top := "50%",
                        ^.left := "50%",
                        ^.transform := "translate(-50%, -50%)",
                        LoadingCircularMui(
                            size = types.Large,
                            color = types.Primary,
                        )
                    )
                }
            }

            val component = ScalaComponent.builder[ KeycloakContext => VdomElement ]
              .initialState( State() )
              .backend( new Backend( _ ) )
              .renderBackend
              .build

            override def apply( renderer : KeycloakContext => VdomElement ) : Unmounted[ KeycloakContext => VdomElement, State, Backend ] =
                component( renderer )
        }
    }

    val DartBackend : KeycloakBackend = new KeycloakBackend
}
