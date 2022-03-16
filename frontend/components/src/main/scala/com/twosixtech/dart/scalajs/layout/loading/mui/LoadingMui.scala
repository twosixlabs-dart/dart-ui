package com.twosixtech.dart.scalajs.layout.loading.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.{MuiCircularProgressRaw, MuiLinearProgressRaw, MuiProgress}
import com.twosixtech.dart.scalajs.layout.loading.Loading
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js

class LoadingMui(
    muiProgressComponent : js.Object,
) extends AbstractReactFacade[ Loading.Props, MuiProgress.JsProps ](
    muiProgressComponent,
    LoadingMui.LoadingMuiTranslation,
) with Loading[ Unit ]

object LoadingMui {

    object LoadingCircularMui extends LoadingMui( MuiCircularProgressRaw )
//    object LoadingCircularLabeledMui extends LoadingMui( MuiCircularProgressWithLabelRaw )
    object LoadingLinearMui extends LoadingMui( MuiLinearProgressRaw )
//    object LoadingLinearLabeledMui extends LoadingMui( MuiLinearProgressWithLabelRaw )

    object LoadingMuiTranslation extends StoJ[ Loading.Props, MuiProgress.JsProps ] {
        override def scalaToJsBuilder(
            props : Loading.Props,
            jsProps : MuiProgress.JsProps
        ) : (MuiProgress.JsProps, VdomNode) = {
            props.complete match {
                case None =>
                    jsProps.variant = "indeterminate"
                case Some( value ) =>
                    jsProps.variant = "determinate"
                    jsProps.value = value
            }
            props.color match {
                case types.Primary => jsProps.color = "primary"
                case types.Secondary => jsProps.color = "secondary"
                case types.Plain => jsProps.color = "inherit"
                case _ =>
            }
            props.size match {
                case types.Small =>
                    jsProps.size = 20F
                    jsProps.thickness = 2.8F
                case types.Medium =>
                    jsProps.size = 40F
                    jsProps.thickness = 3.6F
                case types.Large =>
                    jsProps.size = 60F
                    jsProps.thickness = 4.4F
            }
            (jsProps, EmptyVdom)
        }
    }
}


