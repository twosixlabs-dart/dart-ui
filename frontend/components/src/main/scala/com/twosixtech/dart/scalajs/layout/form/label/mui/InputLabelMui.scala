package com.twosixtech.dart.scalajs.layout.form.label.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.MuiFormControlLabel
import com.twosixtech.dart.scalajs.layout.form.label.InputLabel
import com.twosixtech.dart.scalajs.layout.types
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted

import scala.scalajs.js

object InputLabelMui extends InputLabel[ Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ InputLabel.Props ]
      .initialState()
      .noBackend
      .render_P( props => {
          val jsProps = ( new js.Object ).asInstanceOf[ MuiFormControlLabel.JsProps ]
          jsProps.label = props.label.rawNode
          jsProps.control = props.inputElement.rawElement
          jsProps.labelPlacement = props.labelPlacement match {
              case _ : types.Above => "top"
              case _ : types.Below => "bottom"
              case _ : types.Left => "start"
              case _ : types.Right => "end"
              case _ => "start"
          }
          jsProps.classes = {
              val jc = ( new js.Object ).asInstanceOf[ MuiFormControlLabel.JsClasses ]
              props.classes.root.foreach( jc.root = _ )
              jc
          }

          MuiFormControlLabel( jsProps )
      } )
      .build

    override def apply( props : InputLabel.Props ) : Unmounted[ InputLabel.Props, Unit, Unit ] =
        component( props )

}
