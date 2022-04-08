package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame

import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.ErrorInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartFrameDI {
    this : DartFrameLayoutDeps
      with DartComponentDI
      with DartStateDI
      with DartContextDeps
      with DartMenuBarDI
      with DartMenuDI
      with ErrorInterfaceDI
      with DartAppWindowDI
      with DartLoadingInterfaceDI
      with DartLoadingDI =>

    lazy val dartFrame : DartFrame = new DartFrame

    // DartFrame is top level dart component, so there should be no render context
    class DartFrame extends ViewedDartComponent[ DartFrame.Props, Unit, DartMenuBar.State ] {

        override def stateView( coreState : CoreState ) : DartMenuBar.State = coreState.frameState.menuBarState

        override def render( props : DartFrame.Props, stateView : DartMenuBar.State )(
            implicit renderContext : SnapshotType,
            stateContext : DartContext
        ) : VdomElement = {

            dartFrameLayout( DartFrame.LayoutProps(
                menuBarProps = (),
                menuProps = DartMenu.Props(
                    stateView.menuOpened,
                    props.appChoice
                ),
                renderMenu = stateView.menuOpened,
                windowProps = DartAppWindow.Props( props.appChoice ),
                errorInterfaceProps = ErrorInterface.Props(),
                loadingInterfaceProps = stateContext.coreState.loadingState,
            ).toDartProps )
        }
    }

    object DartFrame {
        trait AppChoice
        case object ConceptExplorer extends AppChoice
        case object Corpex extends AppChoice
        case class CorpexDocument( id : String ) extends AppChoice
        case object Forklift extends AppChoice
        case object Tenants extends AppChoice
        case object Test extends AppChoice

        case class Props(
            appChoice : AppChoice
        )

        case class State(
            menuBarState : DartMenuBar.State = DartMenuBar.State(),
            menuState : DartMenu.State = DartMenu.State(),
            windowState : DartAppWindow.State = DartAppWindow.State(),
        )

        case class LayoutProps(
            menuBarProps : Unit,
            menuProps : DartMenu.Props,
            renderMenu : Boolean,
            windowProps : DartAppWindow.Props,
            errorInterfaceProps : ErrorInterface.Props,
            loadingInterfaceProps : DartLoading.State,
        )
    }

}

trait DartFrameLayoutDeps { this : DartComponentDI with DartFrameDI =>

    type DartFrameLayoutState
    val dartFrameLayout : DartFrameLayout[ Unit, DartFrameLayoutState ]

    trait DartFrameLayout[ RenderContext, State ]
      extends DartLayoutComponent[ DartFrame.LayoutProps, RenderContext, State ]

}

