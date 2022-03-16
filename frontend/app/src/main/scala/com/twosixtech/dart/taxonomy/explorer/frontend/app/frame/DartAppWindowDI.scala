package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame

import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement

trait DartAppWindowDI {
    this : DartAppWindowLayoutDeps
      with DartFrameDI
      with DartComponentDI
      with DartContextDeps =>

    val dartAppWindow : DartAppWindow = new DartAppWindow

    class DartAppWindow extends SimpleDartComponent[ DartAppWindow.Props, DartAppWindowRenderContext ] {
        override def render( props : DartAppWindow.Props )(
            implicit renderProps : DartAppWindowRenderContext,
            context : DartContext
        ) : VdomElement = {

            dartAppWindowLayout( DartAppWindow.LayoutProps( props.appChoice ).toDartProps )

        }
    }

    object DartAppWindow {
        case class State()

        case class Props(
            appChoice : DartFrame.AppChoice
        )

        case class LayoutProps(
            appChoice : DartFrame.AppChoice
        )
    }

}

trait DartAppWindowLayoutDeps { this : DartAppWindowDI with DartComponentDI =>

    type DartAppWindowRenderContext = Unit
    type DartAppWindowLayoutState
    val dartAppWindowLayout : DartAppWindowLayout

    trait DartAppWindowLayout
      extends DartLayoutComponent[ DartAppWindow.LayoutProps, DartAppWindowRenderContext, DartAppWindowLayoutState ]

}
