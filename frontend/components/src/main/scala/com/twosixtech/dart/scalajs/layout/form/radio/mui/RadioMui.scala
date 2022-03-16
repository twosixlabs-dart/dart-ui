package com.twosixtech.dart.scalajs.layout.form.radio.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.{MuiFormControlLabel, MuiRadio, MuiRadioGroup}
import com.twosixtech.dart.scalajs.layout.form.radio.Radio
import com.twosixtech.dart.scalajs.layout.types
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}

import scala.scalajs.js

object RadioMui extends Radio[ Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ Radio.Props ]
      .noBackend
      .render_P( props => {
          val radioGroupProps = ( new js.Object ).asInstanceOf[ MuiRadioGroup.JsProps ]
          props.classes.root.foreach( radioGroupProps.className = _ )
          radioGroupProps.value = props.value
          radioGroupProps.onChange = e => props.onChange( e.target.value ).runNow()

          import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

          MuiRadioGroup( radioGroupProps)(
              props.items.map( item => {
                  val formControlLabelProps = ( new js.Object ).asInstanceOf[ MuiFormControlLabel.JsProps ]
                  formControlLabelProps.value = item.value
                  formControlLabelProps.label = item.label.rawNode
                  formControlLabelProps.disabled = props.disabled && item.disabled
                  formControlLabelProps.classes = {
                      val fclc = ( new js.Object ).asInstanceOf[ MuiFormControlLabel.JsClasses ]
                      fclc.label = props.classes.items.label and item.classes.label
                      fclc.root = props.classes.items.root and item.classes.root
                      fclc
                  }
                  val radioProps = ( new js.Object ).asInstanceOf[ MuiRadio.JsProps ]
                  radioProps.color = item.color.getOrElse( props.color ) match {
                      case types.Primary => "primary"
                      case types.Secondary => "secondary"
                      case _ => "default"
                  }
                  radioProps.classes = {
                      val fclc = ( new js.Object ).asInstanceOf[ MuiRadio.JsClasses ]
                      fclc.root = props.classes.items.button and item.classes.button
                      fclc
                  }
                  formControlLabelProps.control = MuiRadio( radioProps ).raw

                  MuiFormControlLabel( formControlLabelProps ) : VdomNode
              } ) : _*
          )
      } )
      .build

    override def apply( props : Radio.Props ) : Unmounted[ Radio.Props, Unit, Unit ] = {
        component( props )
    }
}
