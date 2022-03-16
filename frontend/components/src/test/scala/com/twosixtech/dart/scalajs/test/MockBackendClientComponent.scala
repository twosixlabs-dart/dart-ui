package com.twosixtech.dart.scalajs.test

import com.twosixtech.dart.scalajs.backend.{BackendClient, HttpMethod, HttpRequest, HttpResponse}
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.scalajs.test.BackendMocks.ResponseType
import com.twosixtech.dart.scalajs.test.MockBackendClientComponent._
import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}

import java.util.UUID
import scala.concurrent.{Future, Promise}
import scala.scalajs.js

class MockBackendClientComponent extends ReactComponent[ Props, State ] {
    override type BackendType = Backend

    class Backend( scope : BackendScope[ Props, State ] ) {

        val id : String = UUID.randomUUID().toString

        def render( props : Props, state : State ): VdomElement = {
            props.render(
                new MockBackendContext(
                    id,
                    state.currentHandler,
                    ( newResType : ResponseType ) => {
                        scope.modState( _.copy( currentHandler = newResType ) )
                    },
                )
            )
        }

    }

    val component = ScalaComponent.builder[ Props ]
      .initialState( State() )
      .backend( new Backend( _ ) )
      .renderBackend
      .build

    override def apply(
        props : Props
    ) : Unmounted[ Props, State, Backend ] = component( props )

    def apply( render : MockBackendContext => VdomElement ) : Unmounted[ Props, State, Backend ] = apply( Props( render ) )
}

object MockBackendClientComponent {
    case class Props(
        render : MockBackendContext => VdomElement,
    )

    case class State(
        currentHandler : ResponseType = BackendMocks.NoResponse()
    )

    class MockBackendContext( val id : String, val responseType : BackendMocks.ResponseType, val setResponse : BackendMocks.ResponseType => Callback ) {

        val client : MockBackendClient = new MockBackendClient( responseType )

        def setStatic( res : HttpResponse ) : Callback = setResponse( BackendMocks.StaticResponse( res ) )

        def setStaticAndHandle( res : HttpResponse )( handle : (HttpMethod, HttpRequest) => Unit ) : Callback =
            setResponse( BackendMocks.StaticResponse( res, handle ) )

        def setHandler( handle : (HttpMethod, HttpRequest) => HttpResponse ) : Callback =
            setResponse( BackendMocks.HandleRequest( handle ) )

        def removeResponse() : Callback =
            setResponse( BackendMocks.NoResponse() )

        def removeResponseButHandle( handle : (HttpMethod, HttpRequest) => Unit ) : Callback =
            setResponse( BackendMocks.NoResponse( handle ) )
    }

    class MockBackendClient( mock : BackendMocks.ResponseType ) extends BackendClient {
        override def submit(
            method : HttpMethod,
            request : HttpRequest,
            onProgress : Double => Unit = _ => {},
        ) : Future[ HttpResponse ] = {
            import scalajs.concurrent.JSExecutionContext.Implicits.queue

            mock match {
                case BackendMocks.NoResponse( onRequest ) =>
                    onRequest( method, request )

                    def delay(milliseconds: Int): Future[ Unit ] = {
                        val p = Promise[ Unit ]()
                        js.timers.setTimeout(milliseconds) {
                            p.success(())
                        }
                        p.future
                    }

                    def defer() : Future[ HttpResponse ] = delay( 1000 )
                      .flatMap( _ => defer() )

                    defer()
                case BackendMocks.StaticResponse( res, handler ) => Future.successful {
                    handler( method, request )
                    res
                }
                case BackendMocks.HandleRequest( handler ) =>
                    Future.successful( handler( method, request ) )
            }
        }
    }
}
