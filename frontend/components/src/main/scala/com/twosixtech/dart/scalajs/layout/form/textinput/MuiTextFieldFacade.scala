package com.twosixtech.dart.scalajs.layout.form.textinput

import com.twosixtech.dart.scalajs.layout.facade.mui.{MuiTextField, MuiTextFieldRaw}
import com.twosixtech.dart.scalajs.layout.types.{Large, Medium, Primary, Secondary, Small}
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ReactKeyboardEvent}

import scala.scalajs.js

object TextInputMui
  extends AbstractReactFacade[ TextInput.Props, MuiTextField.JsProps ](
      MuiTextFieldRaw,
      TextInputTranslation,
  )

object TextInputTranslation extends StoJ[ TextInput.Props, MuiTextField.JsProps ] {
    override def scalaToJsBuilder(
        props : TextInput.Props,
        jsProps : MuiTextField.JsProps ) : (MuiTextField.JsProps,
      VdomNode) = {
        props.value.foreach( jsProps.value = _ )
        props.classes.root.foreach( r => {
            val jc = ( new js.Object ).asInstanceOf[ MuiTextField.JsClasses ]
            jc.root = r
            jc
        } )
        props.classes.input.foreach( i => {
            val jip = ( new js.Object ).asInstanceOf[ MuiTextField.JsInputProps ]
            val jic =  ( new js.Object ).asInstanceOf[ MuiTextField.JsInputClasses ]
            jic.input = i
            jip.classes = jic
            jsProps.InputProps = jip
        } )
        jsProps.autoFocus = props.autoFocus
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
            case Large => "medium"
        }
        jsProps.variant = props.variant match {
            case TextInput.Standard => "standard"
            case TextInput.Filled => "filled"
            case TextInput.Outlined => "outlined"
        }
        props.onBlur.foreach( fn => jsProps.onBlur = ( e : ReactEventFromInput ) => fn( e ).runNow() )
        jsProps.onKeyDown = ( e : ReactKeyboardEvent ) => {
            props.onKeyDown.foreach( _( e ).runNow() )
            props.onEnter.foreach( cb => {
                if ( e.keyCode == 13 || e.which == 13 || e.key == "Enter" ) cb.runNow()
            } )
            props.onEscape.foreach( cb => {
                if ( e.keyCode == 27 || e.which == 27 || e.key == "Escape" ) cb.runNow()
            } )
        }
        (jsProps, EmptyVdom)
    }
}

