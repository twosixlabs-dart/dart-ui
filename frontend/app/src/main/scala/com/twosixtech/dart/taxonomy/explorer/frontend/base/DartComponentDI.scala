package com.twosixtech.dart.taxonomy.explorer.frontend.base

import com.twosixtech.dart.scalajs.react.{AppComponent, AppProps, CoreComponentNoView, CoreComponentWithView, LayoutComponentNoView, LayoutComponentWithView, StatefulCoreComponentNoView, StatefulCoreComponentWithView}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement


trait DartComponentDI { this : DartStateDI with DartContextDeps =>

    case class DartProps[ CoreProps, RenderContext ](
        mainProps : CoreProps,
        renderProps : RenderContext,
        context : DartContext
    ) extends AppProps[ CoreProps, RenderContext, DartContext ] {
        def withRenderCtx[ NewRenderCtx ]( renderCtx : NewRenderCtx ) : DartProps[ CoreProps, NewRenderCtx ] = {
            DartProps[ CoreProps, NewRenderCtx ]( mainProps, renderCtx, context )
        }
    }

    object DartProps {
        implicit def toDartProps[ T, RenderContext ]( t : T )( implicit rc : RenderContext, dc : DartContext ) : DartProps[ T, RenderContext ] = {
            DartProps( t, rc, dc )
        }
    }

    trait AnyToProps[ RenderContext ] {
        implicit class AnyToProps[ T ]( value : T ) {
            def toDartProps( implicit rc : RenderContext, dc : DartContext ) : DartProps[ T, RenderContext ] = {
                DartProps.toDartProps( value )
            }

            def toDartPropsRC[ NewRenderCtx ]( newRenderCtx : NewRenderCtx )( implicit dc : DartContext ) : DartProps[ T, NewRenderCtx ] = {
                DartProps[ T, NewRenderCtx ]( value, newRenderCtx, dc )
            }
        }
    }

    type DartComponent[ Props, RenderContext, ContextView, State ] = AppComponent[ Props, RenderContext, DartContext, ContextView, State ]

    trait SimpleDartComponent[ Props, RenderContext ]
      extends CoreComponentNoView[ Props, RenderContext, DartContext ]
        with AnyToProps[ RenderContext ]

    trait ViewedDartComponent[ Props, RenderContext, StateView ]
      extends CoreComponentWithView[ Props, RenderContext, DartContext, StateView ]
        with AnyToProps[ RenderContext ] {

        def stateView( coreState : CoreState ) : StateView

        override final def contextView( context : DartContext ) : StateView = stateView( context.coreState )

        override def render( props : Props, stateView : StateView )
          ( implicit renderContext : RenderContext, stateContext : DartContext ) : VdomElement

    }

    trait StatefulDartComponent[ Props, RenderContext, State ]
      extends StatefulCoreComponentNoView[ Props, RenderContext, DartContext, State ]
        with AnyToProps[ RenderContext ] {

        override def render( scope : Scope, state : State, props : Props )
          ( implicit renderContext : RenderContext, stateContext : DartContext ) : VdomElement

    }

    trait StatefulViewedDartComponent[ Props, RenderContext, StateView, State ]
      extends StatefulCoreComponentWithView[ Props, RenderContext, DartContext, StateView, State ]
        with AnyToProps[ RenderContext ] {

        def stateView( coreState : CoreState ) : StateView

        override final def contextView( context : DartContext ) : StateView = stateView( context.coreState )

        override def render( scope : Scope, state : State, props : Props, stateView : StateView )
          ( implicit renderContext : RenderContext, stateContext : DartContext ) : VdomElement

    }

    trait DartLayoutComponent[ Props, RenderContext, State ]
      extends LayoutComponentNoView[ Props, RenderContext, DartContext, State ]
        with AnyToProps[ RenderContext ]

    trait ViewedDartLayoutComponent[ Props, RenderContext, StateView, State ]
      extends LayoutComponentWithView[ Props, RenderContext, DartContext, StateView, State ]
        with AnyToProps[ RenderContext ] {

        def stateView( layoutState : LayoutState ) : StateView

        override def contextView( context : DartContext ) : StateView = stateView( context.layoutState )

    }

}
