package com.twosixtech.dart.scalajs.layout.button.iconbutton.mui

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.types.{Large, Medium, Plain, Primary, Secondary, Small}
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.ReactMouseEvent
import japgolly.scalajs.react.raw.React.Node
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core/IconButton", JSImport.Default )
@js.native
object MuiIconButtonRaw extends js.Object

object IconButtonMui
  extends AbstractReactFacade[ IconButton.Props, IconButtonMuiTranslation.JsProps ](
      MuiIconButtonRaw,
      IconButtonMuiTranslation.PropsConverter,
  ) with IconButton[ Unit ]

object IconButtonMuiTranslation {
    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var size : String = js.native // small || medium
        var color : String = js.native
        var onClick : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var onMouseDown : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var disabled : Boolean = js.native
        var classes : JsClasses = js.native
    }

    object PropsConverter extends StoJ[ IconButton.Props, JsProps ] {
        override def scalaToJsBuilder(
            props : IconButton.Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = {
            jsProps.classes = {
                val c = ( new js.Object ).asInstanceOf[ JsClasses ]
                props.classes.root.foreach( c.root = _ )
                c
            }
            jsProps.color = props.color match {
                case Primary => "primary"
                case Secondary => "secondary"
                case _ => "default"
            }
            jsProps.disabled = props.disabled
            jsProps.onClick = e => {
                props.onClick.runNow()
            }
            jsProps.size = props.size match {
                case Small => "small"
                case Medium => "medium"
                case Large => "medium"
            }

            (jsProps, props.icon)
        }
    }
}

