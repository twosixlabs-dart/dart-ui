package com.twosixtech.dart.scalajs.backend

import org.scalajs.dom.FormData

import scala.language.implicitConversions
import scala.scalajs.js.typedarray._

sealed trait HttpBody

object HttpBody {
    final case class TextBody( text : String ) extends HttpBody
    final case class BinaryBody( data : Array[ Byte ] ) extends HttpBody
    final case class JsFormData( data : FormData ) extends HttpBody
    case object NoBody extends HttpBody

    implicit def httpBodyFromString( string : String ) : TextBody = TextBody( string )
    implicit def httpBodyFromBinary( data : Array[ Byte ] ) : BinaryBody = BinaryBody( data )
    implicit def httpBodyFromArrayBuffer( data : ArrayBuffer ) : BinaryBody =
        BinaryBody( int8Array2ByteArray( new Int8Array( data ) ) )
}


case class HttpRequest(
    url : String = "",
    headers : Map[ String, String ] = Map.empty,
    body : HttpBody = HttpBody.NoBody,
) {
    def withBody( newBody : HttpBody ) : HttpRequest = copy( body = newBody )
    def withoutBody : HttpRequest = copy( body = HttpBody.NoBody )
    def withData( data : Array[ Byte ] ) : HttpRequest = withBody( HttpBody.BinaryBody( data ) )
    def withTextBody( text : String ) : HttpRequest = withBody( HttpBody.TextBody( text ) )

    def addHeader( key : String, value : String ) : HttpRequest = copy( headers = headers + (key -> value) )
    def addHeaders( newHeaders : Iterable[ (String, String)] ) : HttpRequest = copy( headers = headers ++ newHeaders )
    def withHeaders( newHeaders : Iterable[ (String, String) ] ) : HttpRequest = copy( headers = newHeaders.toMap )

    def withUrl( newUrl : String ) : HttpRequest = copy( url = newUrl )
    def withUrl( scheme : String, host : String, port : Int = 80, pathAndParameters : String ) : HttpRequest = {
        copy( url = s"$scheme://$host:$port$pathAndParameters" )
    }
}


case class HttpResponse(
    headers : Map[ String, String ],
    status : Int,
    body : HttpBody,
)
