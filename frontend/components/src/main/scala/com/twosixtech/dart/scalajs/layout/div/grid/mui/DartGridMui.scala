package com.twosixtech.dart.scalajs.layout.div.grid.mui

import com.twosixtech.dart.scalajs.layout.div.grid.DartGrid
import com.twosixtech.dart.scalajs.layout.facade.mui.MuiGrid
import com.twosixtech.dart.scalajs.layout.types.{AlignCenter, Column, AlignEnd, Row, AlignStart}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object DartGridMui extends DartGrid[ Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ DartGrid.Props ]
      .noBackend
      .render_P( props => {
          val items = props.items.zipWithIndex.map( tup => {
              val (item, index) = tup
              val itemProps = {
                  val ip = ( new js.Object ).asInstanceOf[ MuiGrid.JsProps ]
                  ip.container = false
                  ip.item = true
                  ip.classes = {
                      val cl = ( new js.Object ).asInstanceOf[ MuiGrid.JsClasses ]
                      cl.root = ""
                      props.classes.items.foreach( ic => cl.root = cl.root + ic + " " )
                      item.classes.root.foreach( ic => cl.root = cl.root + ic )
                      cl
                  }
                  item.breakPoints.xs.foreach( ip.xs = _ )
                  item.breakPoints.xs.foreach( ip.sm = _ )
                  item.breakPoints.xs.foreach( ip.md = _ )
                  item.breakPoints.xs.foreach( ip.lg = _ )
                  item.breakPoints.xs.foreach( ip.xl = _ )
                  ip.key = item.key.getOrElse( s"grid-item-$index" )
                  ip
              }

              MuiGrid.Facade( itemProps )( item.element )
          } ).toVdomArray

          val containerProps = {
              val cp = ( new js.Object ).asInstanceOf[ MuiGrid.JsProps ]
              cp.container = true
              cp.item = false
              cp.direction = props.direction match {
                  case Row => "row"
                  case Column => "column"
              }
              cp.alignItems = props.align match {
                  case AlignCenter => "center"
                  case AlignStart => "flex-start"
                  case AlignEnd => "flex-end"
              }
              cp.classes = {
                  val cl = ( new js.Object ).asInstanceOf[ MuiGrid.JsClasses ]
                  props.classes.container.foreach( cl.root = _ )
                  cl
              }
              cp.component = "div"
              cp.wrap = if ( props.wrap ) "wrap" else "nowrap"
              cp
          }

          MuiGrid.Facade( containerProps )( items )
      } )
      .build

    override def apply(
        props : DartGrid.Props
    ) : Unmounted[ DartGrid.Props, Unit, Unit ] = {
        component( props )
    }

}


