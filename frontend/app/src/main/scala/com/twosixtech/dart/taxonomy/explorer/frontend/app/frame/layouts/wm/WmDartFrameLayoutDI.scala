package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.wm

import com.twosixtech.dart.scalajs.layout.css.theme.DartTheme
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.ErrorInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.layouts.ErrorInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.{DartMenuBarLayoutDI, DartMenuLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartAppWindowDI, DartAppWindowLayoutDeps, DartFrameDI, DartFrameLayoutDeps, DartMenuBarDI, DartMenuDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.all.onClick
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import java.util.UUID
import scala.scalajs.js

trait WmDartFrameLayoutDI
  extends DartFrameLayoutDeps {
    this : DartFrameDI
      with DartMenuDI
      with DartMenuBarDI
      with DartAppWindowDI
      with DartComponentDI
      with DartContextDeps
      with DartMenuLayoutDI
      with DartMenuBarLayoutDI
      with ErrorInterfaceDI
      with ErrorInterfaceLayoutDI
      with DartLoadingInterfaceDI
      with DartLoadingInterfaceLayoutDI
      with DartAppWindowLayoutDeps =>

    override type DartFrameLayoutState = Option[ UUID ]
    override lazy val dartFrameLayout : DartFrameLayout[ Unit, Option[ UUID ] ] = new DartFrameLayoutBasic

    class DartFrameLayoutBasic extends DartFrameLayout[ Unit, Option[ UUID ] ] {

        override val initialState : Option[ UUID ] = None

        override def render(
            scope : Scope,
            state : Option[ UUID ],
            props : DartFrame.LayoutProps,
        )(
            implicit renderProps : Unit,
            context : DartContext
        ) : VdomElement = {

            DartTheme(
                <.div(
                    ^.style := js.Dictionary( "display" -> "flex", "flexFlow" -> "column", "height" -> "100%" ),
                    <.div(
                        dartMenuBar( props.menuBarProps.toDartPropsRC() ),
                        dartMenu( props.menuProps.toDartPropsRC() ),
                    ),
                    <.div(
                        ^.style := js.Dictionary( "height" -> "48px" ),
                    ),
                    <.div(
                        ^.style := js.Dictionary( "flex" -> "1", "overflow" -> "auto", "padding" -> "20px", "paddingBottom" -> "0px" ),
                        dartAppWindow( props.windowProps.toDartPropsRC() ),
                    ),
                    errorInterface( props.errorInterfaceProps.toDartPropsRC( () ) ),
                    dartLoadingInterface( DartLoadingInterface.Props(
                        props.loadingInterfaceProps,
                        DartLoadingInterface.DarkOverlay,
                    ).toDartPropsRC( () ) ),
                )
            )
        }

    }

}
