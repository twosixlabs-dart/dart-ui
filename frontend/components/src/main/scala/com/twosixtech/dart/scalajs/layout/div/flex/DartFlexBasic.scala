package com.twosixtech.dart.scalajs.layout.div.flex
import com.twosixtech.dart.scalajs.layout.div.flex.DartFlex.{Basis, Grow, NoFlex, Shrink}
import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.layout.types.{AlignCenter, AlignEnd, AlignStart, AlignStretch, Column, Row}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

object DartFlexBasic extends DartFlex [ Unit ] {
  override type BackendType = Unit

  import scalacss.DevDefaults._
  import scalacss.ScalaCssReact._
  object Styles extends StyleSheet.Inline {
    import dsl._
    val defaultContainer = style()
    val defaultItem = style( minHeight( 0 px ), minWidth( 0 px ) )
  }
  Styles.addToDocument()

  val component = ScalaComponent.builder[ DartFlex.Props ]
    .noBackend
    .render_P( props => {
      <.div(
        Styles.defaultContainer and props.classes.container,
        ^.display := "flex",
        ^.alignItems := ( props.align match {
          case AlignStart => "flex-start"
          case AlignCenter => "center"
          case AlignEnd => "flex-end"
          case AlignStretch => "stretch"
        } ),
        ^.justifyContent := ( props.justify match {
          case types.JustifyStart => "flex-start"
          case types.JustifyEnd => "flex-end"
          case types.JustifyCenter => "center"
          case types.JustifySpacedEvenly => "space-evenly"
          case types.JustifySpacedAround => "space-around"
          case types.JustifySpacedBetween => "space-between"
        } ),
        ^.flexDirection := ( props.direction match {
          case Row => "row"
          case Column => "column"
        } ),
        props.items.zipWithIndex.map( tup => {
          val (item, index) = tup
          val (basis, shrink, grow) = item.flex match {
            case NoFlex => (null, null, null)
            case Basis( b ) => (b, null, null)
            case Shrink( s ) => (null, s.toString, null)
            case Grow( g ) => (null, null, g.toString)
          }

          <.div(
            Styles.defaultItem and props.classes.items and item.classes.root,
            ^.key := ( item.key.getOrElse( index.toString ) ),
            if ( basis != null) ^.flexBasis := basis else EmptyVdom,
            if ( shrink != null ) ^.flexShrink := shrink else EmptyVdom,
            if ( grow != null ) ^.flexGrow := grow else EmptyVdom,
            if ( basis == null && shrink == null && grow == null ) ^.flex := "none" else EmptyVdom,
            ^.alignSelf := ( item.align match {
              case None => null
              case Some( AlignStart ) => "flex-start"
              case Some( AlignCenter ) => "center"
              case Some( AlignEnd ) => "flex-end"
              case Some( AlignStretch ) => "stretch"
            } ),
            item.element,
          )
        } ).toVdomArray
      )
    } )
    .build


  override def apply(
    props : DartFlex.Props ) : Unmounted[ DartFlex.Props, Unit, Unit ] = component( props )
}
