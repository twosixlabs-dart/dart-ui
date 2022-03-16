package com.twosixtech.dart.scalajs.layout.form.select.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.{MuiMenuItem, MuiSelect}
import com.twosixtech.dart.scalajs.layout.form.select.Select
import japgolly.scalajs.react.{ReactEventFromInput, ScalaComponent}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.raw.React.Node
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html

import scala.scalajs.js

class SelectMui[ ValType ] extends Select[ ValType, Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ Select.Props[ ValType ] ]
      .noBackend
      .render_P( props => {
          val jsProps = ( new js.Object ).asInstanceOf[ MuiSelect.JsProps ]
          jsProps.value = props.value
          jsProps.disabled = props.disabled
          jsProps.onChange = { ( e : SyntheticEvent[ html.Select ] ) =>
              props.onChange( e.target.value.asInstanceOf[ ValType ] ).runNow()
          }
          jsProps.variant = props.style match {
              case Select.Filled => "filled"
              case Select.Outlined => "outlined"
              case Select.Standard => "standard"
          }
          jsProps.classes = {
              val jc = ( new js.Object ).asInstanceOf[ MuiSelect.JsClasses ]
              props.classes.root.foreach( jc.root = _ )
              jc
          }

          MuiSelect( jsProps )(
              ( props.items.map( item => {
                  val jsMenuProps = ( new js.Object ).asInstanceOf[ MuiMenuItem.JsProps ]
                  jsMenuProps.value = item.value
                  item.key.foreach( jsMenuProps.key = _ )
                  jsMenuProps.classes = {
                      val jmc = ( new js.Object ).asInstanceOf[ MuiMenuItem.JsClasses ]
                      item.classes.root.foreach( jmc.root = _ )
                      jmc
                  }

                  MuiMenuItem( jsMenuProps )( item.element ) : VdomNode
              } ) ) : _*,
          )
      } )
      .build

    def apply( props : Select.Props[ ValType ] ) : Unmounted[ Select.Props[ ValType ], Unit, Unit ] = {
        component( props )
    }

}

object SelectMui {

    lazy val StringSelectMui = new SelectMui[ String ]
    lazy val IntSelectMui = new SelectMui[ Int ]

}
