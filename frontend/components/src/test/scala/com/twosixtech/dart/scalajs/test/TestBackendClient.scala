package com.twosixtech.dart.scalajs.test

import com.twosixtech.dart.scalajs.backend.{BackendClient, HttpMethod, HttpRequest, HttpResponse}
import org.scalajs.dom.window

import scala.concurrent.{Future, Promise}
import scala.scalajs.js

class RequestMocker( defaultResponse : Option[ HttpResponse ] = None ) {
    import RequestMocker._

    var response : ResponseType = defaultResponse match {
        case None => NoResponse()
        case Some( res ) => StaticResponse( res )
    }

    def setStatic( res : HttpResponse ) : Unit = {
        response = StaticResponse( res )
    }

    def setStaticAndHandle( res : HttpResponse )( handle : (HttpMethod, HttpRequest) => Unit ) : Unit = {
        response = StaticResponse( res, handle )
    }

    def setHandler( handle : (HttpMethod, HttpRequest) => HttpResponse ) : Unit = {
        response = HandleRequest( handle )
    }

    def removeResponse() : Unit = { response = NoResponse() }

    def removeResponseButHandle( handle : (HttpMethod, HttpRequest) => Unit ) : Unit = {
        response = NoResponse( handle )
    }
}

object RequestMocker {
    sealed trait ResponseType
    case class NoResponse( onRequest : (HttpMethod, HttpRequest) => Unit = ( _, _ ) => () ) extends ResponseType
    case class StaticResponse( res : HttpResponse, onRequest : (HttpMethod, HttpRequest) => Unit = ( _, _ ) => () )
      extends ResponseType
    case class HandleRequest( handler : (HttpMethod, HttpRequest) => HttpResponse ) extends ResponseType
}

import scalajs.concurrent.JSExecutionContext.Implicits.queue
class TestBackendClient( requestMocker : RequestMocker ) extends BackendClient {
    override def submit(
        method : HttpMethod,
        request : HttpRequest,
        onProgress : Double => Unit = _ => {},
    ) : Future[ HttpResponse ] = {
        requestMocker.response match {
            case RequestMocker.NoResponse( onRequest ) =>
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
            case RequestMocker.StaticResponse( res, handler ) => Future.successful {
                handler( method, request )
                res
            }
            case RequestMocker.HandleRequest( handler ) =>
                Future.successful( handler( method, request ) )
        }
    }
}
