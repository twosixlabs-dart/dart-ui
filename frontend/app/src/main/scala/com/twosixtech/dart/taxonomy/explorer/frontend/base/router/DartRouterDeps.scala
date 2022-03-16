package com.twosixtech.dart.taxonomy.explorer.frontend.base.router

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.router.{ResolutionWithProps, RouterWithPropsConfig}
import japgolly.scalajs.react.raw.React.Component
import japgolly.scalajs.react.vdom.html_<^.VdomElement

trait DartRouterDeps {

    trait DartRouter {
        trait DartRoute

        case class Context(
            page : DartRoute,
            router : DartRoute => Callback,
        )

        def ContextBuilder( renderer : Context => VdomElement ) : Unmounted[ Context => VdomElement, _, _ ]
    }

    val DartRouter : DartRouter

}
