package com.twosixtech.dart.scalajs.layout.form.field

import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.layout.types.{AlignCenter, Row}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._

object Field {

    case class Classes(
        root : Option[ String ] = None,
        label : Option[ String ] = None,
        field : Option[ String ] = None,
    )

    case class Props(
        label : String,
        element : VdomNode,
        vertical : Boolean = false,
        classes : Classes = Classes(),
    )

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object FieldStyles extends StyleSheet.Inline {
        import dsl._

        val labelHoriz = style(
            marginRight( 10 px ),
        )

        val labelVert = style(
            marginBottom( 10 px ),
        )
    }

    FieldStyles.addToDocument()

    val component = ScalaComponent.builder[ Props ]
      .render_P( props => {
          val labelClass = {
              if ( props.vertical ) FieldStyles.labelVert.htmlClass
              else FieldStyles.labelHoriz.htmlClass
          } + props.classes.label.map( " " + _ ).getOrElse( "" )

          DartFlexBasic( DartFlex.Props(
              direction = if ( props.vertical ) types.Column else types.Row,
              align = if ( props.vertical ) types.AlignStart else types.AlignCenter,
              classes = DartFlex.Classes( props.classes.root ),
              items = Vector(
                  DartFlex.FlexItem(
                      TextMui( Text.Props( <.b( s"${props.label.trim.stripSuffix( ":" ).trim}:" ) ) ),
                      key = Some( "label" ),
                      classes = DartFlex.ItemClasses( Some( labelClass ) ),
                  ),
                  DartFlex.FlexItem(
                      props.element,
                      key = Some( "element" ),
                      classes = DartFlex.ItemClasses( props.classes.field ),
                  ),
              )
          ) )
      } )
      .build

    def apply( props : Props ) : Unmounted[ Props, Unit, Unit ] = component( props )
}

