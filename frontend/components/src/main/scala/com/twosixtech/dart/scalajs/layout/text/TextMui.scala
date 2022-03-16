package com.twosixtech.dart.scalajs.layout.text
import com.twosixtech.dart.scalajs.layout.types.{ExtraLarge, ExtraSmall, Giant, Large, Medium, Plain, Primary, Secondary, Small, Tiny}
import com.twosixtech.dart.scalajs.layout.facade.mui.MuiTypography
import japgolly.scalajs.react.{ReactMouseEvent, ScalaComponent}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object TextMui extends Text[ Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ Text.Props ]
      .noBackend
      .render_P( ( props : Text.Props ) => {
          val tp = ( new js.Object ).asInstanceOf[ MuiTypography.JsProps ]
          tp.classes = {
              val tc = ( new js.Object ).asInstanceOf[ MuiTypography.JsClasses ]
              props.classes.root.foreach( tc.root = _ )
              tc
          }
          props.color.foreach( c => tp.color = ( c match {
              case Primary => "primary"
              case Secondary => "secondary"
              case Plain => "inherit"
          } ) )
          tp.onClick = ( e : ReactMouseEvent ) => {
              props.onClick.foreach( _.runNow() )
              props.onClickEvent.foreach( _( e ).runNow() )
          }
          tp.onMouseDown = ( e : ReactMouseEvent ) => {
              props.onMouseDown.foreach( _.runNow() )
              props.onMouseDownEvent.foreach( _( e ).runNow() )
          }
          tp.onMouseUp = ( e : ReactMouseEvent ) => {
              props.onMouseUp.foreach( _.runNow() )
              props.onMouseUpEvent.foreach( _( e ).runNow() )
          }
          tp.variant = props.size match {
              case Tiny => "caption"
              case ExtraSmall => "caption"
              case Small => "body2"
              case Medium => "body1"
              case Large => "h6"
              case ExtraLarge => "h3"
              case Giant => "h1"
          }
          tp.component = "span"

          MuiTypography.Facade( tp )( props.element )
      } )
      .build

    override def apply(
        props : Text.Props
    ) : Unmounted[ Text.Props, Unit, Unit ] = component( props )
}
