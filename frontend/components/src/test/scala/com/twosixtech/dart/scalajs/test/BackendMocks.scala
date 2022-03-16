package com.twosixtech.dart.scalajs.test

import com.twosixtech.dart.scalajs.backend.{HttpMethod, HttpRequest, HttpResponse}

object BackendMocks {
    sealed trait ResponseType
    case class NoResponse( onRequest : (HttpMethod, HttpRequest) => Unit = ( _, _ ) => () ) extends ResponseType
    case class StaticResponse( res : HttpResponse, onRequest : (HttpMethod, HttpRequest) => Unit = ( _, _ ) => () )
      extends ResponseType
    case class HandleRequest( handler : (HttpMethod, HttpRequest) => HttpResponse ) extends ResponseType
}
