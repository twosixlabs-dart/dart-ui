package com.twosixtech.dart.scalajs.backend

import com.twosixtech.dart.scalajs.keycloak.KeycloakContextComponent.KeycloakContext
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.{Callback, CallbackTo}
import japgolly.scalajs.react.vdom.VdomElement

import scala.concurrent.Future
import scala.util.{Success, Try}
import scalajs.concurrent.JSExecutionContext.Implicits.queue


trait BackendContext {
    def client : BackendClient
    def authClient : BackendClient
}

trait BackendComponent[ BC <: BackendContext, St ] extends ReactComponent[ BC => VdomElement, St ]

object BackendComponent

