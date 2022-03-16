package com.twosixtech.dart.scalajs.layout.button.regular.mui

import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.Button.{Large, Normal, Outlined, Primary, Secondary, Small, Solid, Text}
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.raw.SyntheticMouseEvent
import japgolly.scalajs.react.vdom.VdomNode
import org.scalajs.dom.Node
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "dart-ui-components", "Button" )
@js.native
object ButtonMuiRaw extends js.Object

object ButtonMui
  extends AbstractReactFacade[ Button.Props, ButtonMuiTranslation.JsProps ](
      ButtonMuiRaw,
      ButtonMuiTranslation.PropsConverter,
  ) with Button[ Unit ]

object ButtonMuiTrans

object ButtonMuiTranslation {
    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var size : String = js.native
        var variant : String = js.native
        var color : String = js.native
        var onClick : js.Function1[ SyntheticMouseEvent[ HTMLElement ], Unit ] = js.native
        var onMouseDown : js.Function1[ SyntheticMouseEvent[ HTMLElement ], Unit ] = js.native
        var disabled : Boolean = js.native
        var classes : JsClasses = js.native
    }

    object PropsConverter extends StoJ[ Button.Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Button.Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = {
            jsProps.size = props.size match {
                case Large => "medium"
                case Normal => "medium"
                case Small => "small"
            }
            jsProps.variant = props.style match {
                case Solid => "contained"
                case Outlined => "outlined"
                case Text => "text"
            }
            jsProps.color = props.color match {
                case Primary => "primary"
                case Secondary => "secondary"
                case _ => null
            }
            jsProps.onClick = ( e : SyntheticMouseEvent[ HTMLElement ] ) => {
                ( props.onClickEvent( e ) >> props.onClick ).runNow()
            }
            jsProps.onMouseDown = ( e : SyntheticMouseEvent[ HTMLElement ] ) => {
                ( props.onMouseDownEvent( e ) >> props.onMouseDown ).runNow()
            }
            jsProps.disabled = props.disabled
            jsProps.classes = {
                val jc = ( new js.Object ).asInstanceOf[ JsClasses ]
                props.classes.root.foreach( jc.root = _ )
                jc
            }
            (jsProps, props.element)
        }
    }
}
