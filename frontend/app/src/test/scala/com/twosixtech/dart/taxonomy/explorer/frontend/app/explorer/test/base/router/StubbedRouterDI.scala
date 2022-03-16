package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.router

import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.{DartRouterDI, DartRouterDeps}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^._

trait StubbedRouterDI extends DartRouterDI {

    class TestDartRouter extends DartAppRouter {

        case object TestPage extends DartRoute

        private val stubbedComponent = ReactComponent.functional[ Context => VdomElement ] {
            renderer =>
                val context = Context(
                    TestPage,
                    _ => Callback(),
                )
                renderer( context )
        }

        override def ContextBuilder(
            renderer : Context => VdomElement,
        ) : Unmounted[ Context => VdomElement, _, _ ] = stubbedComponent( renderer )

    }

    override lazy val DartRouter : TestDartRouter = new TestDartRouter
}
