package com.twosixtech.dart.scalajs.layout.div.panel

import com.twosixtech.dart.scalajs.layout.div.header.DartHeaderMui.Styles.style
import com.twosixtech.dart.scalajs.layout.facade.mui.MuiPaper
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted

import scala.scalajs.js

object DartPanelMui extends DartPanel[ Unit ] {
    override type BackendType = Unit

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object Styles extends StyleSheet.Inline {
        import dsl._

        val fullHeight = style( height( 100 %% ) )
    }
    Styles.addToDocument()

    val component = ScalaComponent.builder[ DartPanel.Props ]
      .noBackend
      .render_P( props => {
          val paperProps = ( new js.Object ).asInstanceOf[ MuiPaper.JsProps ]
          paperProps.square = false
          paperProps.component = "div"
          paperProps.classes = {
              val pc = ( new js.Object ).asInstanceOf[ MuiPaper.JsClasses ]
              pc.root = Styles.fullHeight.htmlClass
              props.classes.root.foreach( pc.root += " " + _ )
              pc
          }

          MuiPaper.Facade( paperProps )( props.element )
      } )
      .build

    override def apply(
        props : DartPanel.Props
    ) : Unmounted[ DartPanel.Props, Unit, Unit ] = component( props )
}
