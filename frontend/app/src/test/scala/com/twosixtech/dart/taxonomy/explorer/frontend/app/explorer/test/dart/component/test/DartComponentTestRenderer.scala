package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test

import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.{ContextHook, TestApp}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import diode.AnyAction.aType
import diode.react.{ModelProxy, ReactConnectProxy}
import diode.{ActionHandler, ActionResult, ModelUpdated}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, ScalaComponent}

trait DartComponentTestRenderer extends TestApp.AppStack {

    def defaultRenderer( implicit context : DartContext ) : VdomElement

    implicit class PropsHandler[ T ]( value : T ) {
        def toDartPropsRC[ NewRenderCtx ]( newRenderCtx : NewRenderCtx )( implicit dc : DartContext ) : DartProps[ T, NewRenderCtx ] = {
            DartProps[ T, NewRenderCtx ]( value, newRenderCtx, dc )
        }
    }

    val contextHookId : String = "DART-CONTEXT-TEST-HOOK"

    private val DartContextHookComponent = ContextHook.Component[ DartContext ]( Some( contextHookId ) )

    private case class InnerContext(
        dartContext : DartContext,
        renderer : DartContext => VdomElement,
    )

    private val innerComponent = ReactComponent.functional[ InnerContext ] {
        innerContext =>
            DartContextHookComponent(
                (innerContext.dartContext, innerContext.renderer( innerContext.dartContext ))
            )
    }

    def testComponent(
        renderer : DartContext => VdomElement,
    ) : ReactComponent[ Unit, Unit ] = new ReactComponent[ Unit, Unit ] {
        override type BackendType = Backend

        class Backend( scope : BackendScope[ Unit, Unit ] ) {

            def render( ) : VdomElement = {
                DartContextBuilder.ContextBuilder { (_, dartContext ) =>
                    DartContextHookComponent(
                        ( dartContext, renderer( dartContext ) )
                    )
                }
            }
        }

        private val component = ScalaComponent.builder[ Unit ]
          .initialState()
          .backend( new Backend( _ ) )
          .renderBackend
          .build

        override def apply( props : Unit ) : Unmounted[ Unit, Unit, Backend ] = component()
    }

    private def defaultRendererWrapper( context : DartContext ) : VdomElement = {
        implicit val implContext = context
        defaultRenderer
    }

    lazy val defaultTestComponent = testComponent( defaultRendererWrapper )

}
