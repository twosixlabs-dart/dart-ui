package com.twosixtech.dart.scalajs.backend

import com.twosixtech.dart.scalajs.backend.HttpBody.{BinaryBody, JsFormData, NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.Xhr.XhrResponse
import org.scalajs.dom.ProgressEvent

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.typedarray.byteArray2Int8Array
import scala.util.Try

sealed trait HttpMethod

object HttpMethod {
    case object Post extends HttpMethod
    case object Get extends HttpMethod
    case object Put extends HttpMethod
    case object Delete extends HttpMethod
}

trait BackendClient {

    def submit( method : HttpMethod, request : HttpRequest, onProgress : Double => Unit = _ => {} ) : Future[ HttpResponse ]

    def post( request : HttpRequest ) : Future[ HttpResponse ] = submit( HttpMethod.Post, request )
    def get( request : HttpRequest ) : Future[ HttpResponse ] = submit( HttpMethod.Get, request )
    def put( request : HttpRequest ) : Future[ HttpResponse ] = submit( HttpMethod.Put, request )
    def delete( request : HttpRequest ) : Future[ HttpResponse ] = submit( HttpMethod.Delete, request )

}

object XhrBackendClient extends BackendClient {

    override def submit(
        method : HttpMethod,
        request : HttpRequest,
        onProgress : Double => Unit,
    ) : Future[ HttpResponse ] = {

        val methodString : String = method match {
            case HttpMethod.Post => "POST"
            case HttpMethod.Put => "PUT"
            case HttpMethod.Get => "GET"
            case HttpMethod.Delete => "DELETE"
        }

        val body = request.body match {
            case TextBody( text ) => Some( text : js.Any )
            case BinaryBody( data ) =>
                Some( byteArray2Int8Array( data ).buffer : js.Any )
            case JsFormData( data ) => Some( data : js.Any )
            case NoBody => None
        }

        val onProgressEvent = ( evt : ProgressEvent ) => {
            val total = evt.total
            val loaded = evt.loaded
            Try( loaded * 100 / total ) foreach { percentage =>
                onProgress( percentage )
            }
        }

        Xhr.xhrRequest(
            methodString,
            request.url,
            body,
            request.headers,
            onProgress = onProgressEvent,
            withCredentials = true,
        ) map { res =>
            val XhrResponse(status, response, headers) = res
            HttpResponse(
                headers,
                status,
                response match {
                    case Left( text ) => HttpBody.TextBody( text )
                    case Right( binary ) => HttpBody.BinaryBody( binary )
                },
            )
        }
    }

}
