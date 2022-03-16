package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame

import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartMenuBarDI {
    this : DartMenuBarLayoutDeps
      with DartComponentDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with DartFrameDI =>

    lazy val dartMenuBar : DartMenuBar = new DartMenuBar

    class DartMenuBar extends ViewedDartComponent[ Unit, DartMenuBarRenderContext, DartMenuBar.State ] {
        import DartMenuBar._

        override def stateView( coreState : CoreState ) : DartMenuBar.State = coreState.frameState.menuBarState

        override def render( props : Unit, stateView : DartMenuBar.State )(
            implicit renderContext : DartMenuBarRenderContext,
            stateContext : DartContext ) : VdomElement = {

            lazy val toggleMenu : Callback = Callback.empty >> {
                if ( stateView.menuOpened ) stateContext.dispatch( CloseDartMenu )
                else stateContext.dispatch( OpenDartMenu )
            }

            lazy val openMenu : Callback = stateContext.dispatch( OpenDartMenu )
            lazy val closeMenu : Callback = stateContext.dispatch( CloseDartMenu )

            dartMenuBarLayout( LayoutProps(
                menuName = "Menu",
                menuIsOpen = stateView.menuOpened,
                toggleMenu = toggleMenu,
                openMenu = openMenu,
                closeMenu = closeMenu,
            ).toDartProps )
        }
    }

    object DartMenuBar {
        case class State(
            menuOpened : Boolean = false,
        )

        case class LayoutProps(
            menuName : String,
            menuIsOpen : Boolean,
            toggleMenu : Callback,
            openMenu : Callback,
            closeMenu : Callback,
        )

        trait DartMenuBarAction extends CoreAction
        case object OpenDartMenu extends DartMenuBarAction
        case object CloseDartMenu extends DartMenuBarAction

        val dartMenuHandler : CoreHandler[ State ] = DartCircuitContext.coreHandler[ State ]( _.zoomTo( _.frameState.menuBarState ) ) {
            state : State => {
                case OpenDartMenu => State( true )
                case CloseDartMenu => State( false )
            }
        }
    }

}

trait DartMenuBarLayoutDeps { this : DartMenuBarDI with DartComponentDI =>

    type DartMenuBarRenderContext
    type DartMenuBarLayoutState
    val dartMenuBarLayout : DartMenuBarLayout[ DartMenuBarRenderContext, DartMenuBarLayoutState ]

    trait DartMenuBarLayout[ RenderContext, State ]
      extends DartLayoutComponent[ DartMenuBar.LayoutProps, RenderContext, State ]
}
