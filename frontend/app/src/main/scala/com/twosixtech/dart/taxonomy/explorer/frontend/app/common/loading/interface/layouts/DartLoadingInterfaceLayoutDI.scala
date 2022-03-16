package com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts

import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.loading.mui.LoadingMui.LoadingCircularMui
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.{DartLoadingInterfaceDI, DartLoadingInterfaceLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait DartLoadingInterfaceLayoutDI
  extends DartLoadingInterfaceLayoutDeps {
    this : DartLoadingInterfaceDI
      with DartComponentDI
      with DartContextDeps =>

    override type DartLoadingInterfaceRenderContext = Unit
    override type DartLoadingInterfaceLayoutState = Unit

    override val dartLoadingInterfaceLayout : DartLoadingInterfaceLayout =
        new GenericDartLoadingInterfaceLayout

    class GenericDartLoadingInterfaceLayout extends DartLoadingInterfaceLayout {
        override def render(
            scope : Scope, state : SnapshotType,
            props : DartLoadingInterface.LayoutProps,
        )(
            implicit renderProps : SnapshotType,
            context : DartContext,
        ) : VdomElement = {
            if ( !props.isLoading ) <.div()
            else {
                <.div(
                    ^.position := "absolute",
                    ^.top := "0px",
                    ^.bottom := "0px",
                    ^.left := "0px",
                    ^.right := "0px",
                    ^.zIndex := "999",
                    props.display match {
                        case DartLoadingInterface.DarkOverlay =>
                            ^.backgroundColor := "rgba(0,0,0,0.65)"
                        case DartLoadingInterface.LightOverlay =>
                            EmptyVdom
                    },
                    <.div(
                        ^.position := "absolute",
                        ^.margin := "0",
                        ^.top := "50%",
                        ^.left := "50%",
                        ^.transform := "translate(-50%, -50%)",
                        props.display match {
                            case DartLoadingInterface.DarkOverlay =>
                                ^.color := "rgb(255,255,255)"
                            case DartLoadingInterface.LightOverlay =>
                                EmptyVdom
                        },
                        LoadingCircularMui (
                            complete = props.progress,
                            color = props.display match {
                                case DartLoadingInterface.DarkOverlay =>
                                    types.Plain
                                case DartLoadingInterface.LightOverlay =>
                                    types.Primary
                            },
                            size = types.Large,
                        ),
                    )
                )
            }
        }

        override val initialState : Unit = ()
    }
}
