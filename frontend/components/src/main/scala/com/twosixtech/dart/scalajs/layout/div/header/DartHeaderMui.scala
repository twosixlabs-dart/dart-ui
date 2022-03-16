package com.twosixtech.dart.scalajs.layout.div.header
import com.twosixtech.dart.scalajs.layout.div.grid.DartGrid
import com.twosixtech.dart.scalajs.layout.div.grid.mui.DartGridMui
import com.twosixtech.dart.scalajs.layout.facade.mui.MuiPaper
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types.{AlignCenter, Column}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

import scala.scalajs.js

object DartHeaderMui extends DartHeader[ Unit ] {
    override type BackendType = Unit

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object Styles extends StyleSheet.Inline {
        import dsl._

        val paper : StyleA = style( height( 48 px ) )
        val smallPaper : StyleA = style( padding( 3 px ) )
        val container : StyleA = style( height( 100 %% ) )
    }
    Styles.addToDocument()



    val component = ScalaComponent.builder[ DartHeader.Props ]
      .noBackend
      .render_P( props => {
          val outerProps = ( new js.Object ).asInstanceOf[ MuiPaper.JsProps ]
          outerProps.square = true
          outerProps.classes = {
              val c = ( new js.Object ).asInstanceOf[ MuiPaper.JsClasses ]
              c.root = if ( props.small ) Styles.smallPaper.htmlClass else Styles.paper.htmlClass
              props.classes.root.foreach( c.root += " " + _ )
              c
          }

        MuiPaper.Facade( outerProps ) {
            DartGridMui( DartGrid.Props(
                Column,
                AlignCenter,
                classes = DartGrid.Classes( Some( Styles.container.htmlClass ) ),
                items = Vector( DartGrid.GridItem(
                    DartGridMui( DartGrid.Props(
                        Column,
                        AlignCenter,
                        classes = DartGrid.Classes( Some( Styles.container.htmlClass ) ),
                        items = Vector( DartGrid.GridItem(
                            props.content match {
                                case Left( title ) => TextMui( Text.Props( VdomNode.cast( title ) ) )
                                case Right( ele ) => ele
                            },
                            key = Some( "header-inside" ),
                        ) ),
                    ) ),
                    key = Some( "header-outside" ),
                ) )
            ) )
        }
      } )
      .build

    override def apply( props : DartHeader.Props ) : Unmounted[ DartHeader.Props, Unit, Unit ] = component( props )
}
