package com.twosixtech.dart.scalajs.layout.form.checkbox.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.MuiCheckBox
import com.twosixtech.dart.scalajs.layout.form.checkbox.CheckBox
import com.twosixtech.dart.scalajs.layout.types
import japgolly.scalajs.react.{ReactMouseEvent, ScalaComponent}
import japgolly.scalajs.react.component.Scala.Unmounted

import scala.scalajs.js

object CheckBoxMui extends CheckBox[ Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ CheckBox.Props ]
      .initialState()
      .noBackend
      .render_P( props => {
          val jsProps = ( new js.Object ).asInstanceOf[ MuiCheckBox.JsProps ]

          jsProps.checked = props.checked
          jsProps.disabled = props.disabled
          jsProps.classes = {
              val jsc = ( new js.Object ).asInstanceOf[ MuiCheckBox.JsClasses ]
              props.classes.root.foreach( jsc.root = _ )
              props.classes.checked.foreach( jsc.checked = _ )
              props.classes.disabled.foreach( jsc.disabled = _ )
              jsc
          }
          jsProps.color = props.checkedColor match {
              case types.Primary => "primary"
              case types.Secondary => "secondary"
              case _ => "default"
          }
          jsProps.size = props.size match {
              case types.Small => "small"
              case _ => "medium"
          }
          jsProps.onClick = { ( e : ReactMouseEvent ) =>
              props.onClickEvent( e ).runNow()
              props.onClick.runNow()
          }
          jsProps.onMouseDown = { ( e : ReactMouseEvent ) =>
              props.onMouseDownEvent( e ).runNow()
              props.onMouseDown.runNow()
          }
          jsProps.onMouseUp = { ( e : ReactMouseEvent ) =>
              props.onMouseUpEvent( e ).runNow()
              props.onMouseUp.runNow()
          }

          MuiCheckBox.Facade( jsProps )
      } )
      .build

    override def apply( props : CheckBox.Props ) : Unmounted[ CheckBox.Props, Unit, Unit ] =
        component( props )
}
