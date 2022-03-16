package com.twosixtech.dart.scalajs.layout.button

import com.twosixtech.dart.scalajs.react.SimpleReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

object Clickable extends SimpleReactComponent[ ClickableProps ] {

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object ClickableStyles extends StyleSheet.Inline {
        import dsl._

        val clickable : StyleA = style(
            &.hover( cursor.pointer ),
        )
    }
    ClickableStyles.addToDocument()

    override protected def render( props : ClickableProps ) : VdomElement = {
        <.div(
            ^.className := ( ClickableStyles.clickable.htmlClass + props.classes.root.map( " " + _ ).getOrElse( "" ) ),
            ^.onClick --> props.onClick,
            props.element,
        )
    }

    implicit class ClickableElement( vdomNode: VdomNode ) {
        def clickable(
            onClick : Callback,
            classes : ClickableClasses = ClickableClasses(),
        ) : Unmounted[ ClickableProps, Unit, Unit ] = {
            Clickable( ClickableProps( vdomNode, onClick, classes ) )
        }
    }

}

case class ClickableClasses(
    root : Option[ String ] = None,
)

case class ClickableProps(
    element : VdomNode,
    onClick : Callback,
    classes : ClickableClasses = ClickableClasses(),
)
