package com.twosixtech.dart.scalajs.layout.button.filebutton.mui

import com.twosixtech.dart.scalajs.layout.button.filebutton.FileButton
import com.twosixtech.dart.scalajs.layout.button.filebutton.FileButton.{Outlined, Solid, Text}
import com.twosixtech.dart.scalajs.layout.types.{Large, Medium, Primary, Secondary, Small}
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.ReactEventFromInput
import japgolly.scalajs.react.raw.React.Node
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "dart-ui-components", "FileButton" )
@js.native
object FileButtonMuiRaw extends js.Object

object FileButtonMui
  extends AbstractReactFacade(
      FileButtonMuiRaw,
      FileButtonMuiTranslation.PropConverter,
  )

object FileButtonMuiTranslation {
    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var size : String = js.native
        var element : Node = js.native
        var style : String = js.native
        var color : String = js.native
        var onChange : js.Function1[ ReactEventFromInput, Unit ] = js.native
        var disabled : Boolean = js.native
        var classes : JsClasses = js.native
    }

    object PropConverter extends StoJ[ FileButton.Props, JsProps ] {
        override def scalaToJsBuilder(
            props : FileButton.Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = {
            import props._

            jsProps.element = element.rawNode
            jsProps.disabled = disabled
            jsProps.onChange = e => onChange( e ).runNow()
            jsProps.classes = {
                val jc = ( new js.Object ).asInstanceOf[ JsClasses ]
                classes.root.foreach( jc.root = _ )
                jc
            }
            jsProps.style = style match {
                case Solid => "solid"
                case Outlined => "outlined"
                case Text => "text"
            }
            jsProps.size = size match {
                case Small => "small"
                case Medium => "normal"
                case Large => "large"
            }
            jsProps.color = color match {
                case Primary => "primary"
                case Secondary => "secondary"
                case _ => null
            }
            (jsProps, EmptyVdom)
        }
    }
}
