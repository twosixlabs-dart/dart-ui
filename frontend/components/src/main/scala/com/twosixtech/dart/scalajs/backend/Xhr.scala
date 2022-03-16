package com.twosixtech.dart.scalajs.backend

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.{ErrorEvent, Event, ProgressEvent, XMLHttpRequest}
import org.scalajs.dom.window

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.typedarray._
import scala.util.{Failure, Success, Try}

class XhrFailure( msg : String ) extends Exception( msg )
class XhrUnknownFailure extends Exception( "Xhr request failed: cause unknown" )
class XhrLocalErrorEvent( val evt : ErrorEvent ) extends XhrFailure( s"Xhr request failed: ${evt.message}" )
class XhrNetworkErrorEvent( val evt : ProgressEvent ) extends XhrFailure( s"Xhr request failed: nextwork error" )
class XhrTimeoutEvent( timeout : Double, e : Event ) extends XhrFailure( s"Xhr request timed out: ($timeout ms) ${e.toString}" )
class XhrAbortEvent( value : Any ) extends XhrFailure( s"Xhr request aborted: $value" )

object Xhr {

    case class XhrResponse(
        status : Int,
        response : Either[ String, Array[ Byte ] ],
        headers : Map[ String, String ],
    )

    def parseResponse( xhr : XMLHttpRequest ) : Either[ String, Array[ Byte ] ] = {
        xhr.responseType match {
            case "arraybuffer" | "blob" =>
                Right( int8Array2ByteArray( new Int8Array( xhr.response.asInstanceOf[ ArrayBuffer ] ) ) )
            case _ =>
                Left( xhr.responseText )
        }
    }

    private def parseHeaders( headers : String ) : Map[ String, String ] = {
        headers
          .split( '\n' )
          .map( _.split( ':' ) )
          .filter( _.length != 2 )
          .map( list => (list.head, list( 1 )) )
          .toMap
    }

    def xhrRequest(
        method : String,
        url : String,
        body : Option[ js.Any ] = None,
        headers : Map[ String, String ] = Map.empty,
        timeout : Option[ Long ] = None,
        withCredentials : Boolean = false,
        onSucceed : XMLHttpRequest => Unit = _ => (),
        onError : XMLHttpRequest => Unit = _ => (),
        onTimeout : XMLHttpRequest => Unit = _ => (),
        onAbort : XMLHttpRequest => Unit = _ => (),
        onStart : XMLHttpRequest => Unit = _ => (),
        onProgress : ProgressEvent => Unit = _ => (),
    ) : Future[ XhrResponse ] = {

        val promise = Promise[ XhrResponse ]()

        val xhr = new dom.XMLHttpRequest()

        timeout.foreach( to => xhr.timeout = to )
        xhr.withCredentials = withCredentials

        xhr.open( method, url )

        headers.foreach { case (key, value) => xhr.setRequestHeader( key, value ) }

        xhr.onload = { event =>
            onSucceed( xhr )
            if ( !promise.isCompleted ) promise.complete(
                Success(
                    XhrResponse(
                        Option( xhr.status ).get,
                        parseResponse( xhr ),
                        parseHeaders( Option( xhr.getAllResponseHeaders() ).get ),
                    )
                )
            )
        }

        xhr.onerror = { ( evt : Event ) =>
            onError( xhr )
            val exception =
                if ( js.special.instanceof( evt, js.Dynamic.global.ErrorEvent ) )
                    new XhrLocalErrorEvent( evt.asInstanceOf[ ErrorEvent ] )
                else if ( js.special.instanceof( evt, js.Dynamic.global.ProgressEvent ) )
                    new XhrNetworkErrorEvent( evt.asInstanceOf[ ProgressEvent ] )
                else new XhrUnknownFailure
            if ( !promise.isCompleted ) promise.complete( Failure( exception ) )
        }

        xhr.ontimeout = { event =>
            onTimeout( xhr )
            if ( !promise.isCompleted )
                promise.complete( Failure( new XhrTimeoutEvent( xhr.timeout, event ) ) )
        }

        xhr.onabort = { any =>
            onAbort( xhr )
            if ( !promise.isCompleted )
                promise.complete( Failure( new XhrAbortEvent( any ) ) )
        }

        xhr.onloadstart = { _ => onStart( xhr ) }

        xhr.onprogress = { progressEvent => onProgress( progressEvent ) }

        body match {
            case None => xhr.send()
            case Some( value ) => xhr.send( value )
        }

        promise.future

    }

}
