package com.twosixtech.dart.scalajs.layout.form.textarea

import com.twosixtech.dart.scalajs.layout.facade.mui.{MuiTextField, MuiTextFieldRaw}
import com.twosixtech.dart.scalajs.layout.types.{Large, Medium, Primary, Secondary, Small}
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ReactKeyboardEvent}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom
import org.scalajs.dom.raw.KeyboardEvent
import org.scalajs.dom.window

object TextAreaMui
  extends AbstractReactFacade[ TextArea.Props, MuiTextField.JsProps ](
      MuiTextFieldRaw,
      TextAreaTranslation,
  )

object TextAreaTranslation extends StoJ[ TextArea.Props, MuiTextField.JsProps ] {
    override def scalaToJsBuilder(
        props : TextArea.Props,
        jsProps : MuiTextField.JsProps
    ) : (MuiTextField.JsProps, VdomNode) = {

        props.value.foreach( jsProps.value = _ )
        jsProps.autoFocus = props.autoFocus
        jsProps.multiline = true
        props.numLines.foreach( jsProps.rows = _ )
        props.maxNumLines.foreach( jsProps.rowsMax = _ )
        jsProps.color = props.color match {
            case Primary => "primary"
            case Secondary => "secondary"
        }
        jsProps.disabled = props.disabled
        jsProps.fullWidth = props.fullWidth
        props.onChange.foreach( ( fn : String => Callback ) => jsProps.onChange = {
            e : ReactEventFromInput => fn( e.target.value ).runNow()
        } )
        props.placeholder.foreach( jsProps.placeholder = _ )
        jsProps.size = props.size match {
            case Small => "small"
            case Medium => "medium"
            case Large => "large"
        }
        jsProps.variant = props.variant match {
            case TextArea.Standard => "standard"
            case TextArea.Filled => "filled"
            case TextArea.Outlined => "outlined"
        }
        props.onBlur.foreach( cb => jsProps.onBlur = _ => cb.runNow() )
        jsProps.onKeyDown = ( e : ReactKeyboardEvent ) => {
            props.onKeyDown.foreach( _( e ).runNow() )
            props.onEnter.foreach( cb => {
                if ( e.keyCode == 13 || e.which == 13 || e.key == "Enter" ) {
                    cb.runNow()
                }
            } )
            props.onEscape.foreach( cb => {
                if ( e.keyCode == 27 || e.which == 27 || e.key == "Escape" ) cb.runNow()
            } )
        }
        (jsProps, EmptyVdom)
    }
}

