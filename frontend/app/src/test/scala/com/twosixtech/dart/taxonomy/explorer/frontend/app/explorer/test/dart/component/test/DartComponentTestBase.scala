package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test

import com.twosixtech.dart.scalajs.react.ReactTestUtilities
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom
import utest.TestSuite

import scala.concurrent.Future

trait DartComponentTestBase extends TestSuite {
    this : DartComponentTestRenderer with DartContextDeps =>

    class TestRunner( renderer : DartContext => VdomElement ) {
        private lazy val component = testComponent( renderer )

        def runTest[ Result ]( testFn : dom.Element => Result ) : Result = {
            ReactTestUtilities.testOn( component() )( testFn )
        }

        def asyncRunTest[ Result ]( testFn : dom.Element => Future[ Result ] ) : Future[ Result ] = {
            ReactTestUtilities.asyncTestOn( component() )( testFn )
        }
    }

    object TestRunner {
        private def defaultRendererWrapper( context : DartContext ) : VdomElement = {
            implicit val implContext = context
            defaultRenderer
        }

        def apply(
            renderer : DartContext => VdomElement = defaultRendererWrapper,
        ) : TestRunner =
            new TestRunner( renderer )
    }

    lazy val defaultRunner : TestRunner = TestRunner()

}
