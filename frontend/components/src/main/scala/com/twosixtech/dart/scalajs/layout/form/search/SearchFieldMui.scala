package com.twosixtech.dart.scalajs.layout.form.search

import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.panel.{DartPanel, DartPanelMui}
import com.twosixtech.dart.scalajs.layout.form.textinput.TextInputMui
import com.twosixtech.dart.scalajs.layout.icon.IconMui
import com.twosixtech.dart.scalajs.layout.icon.Icons.SearchIconMui
import com.twosixtech.dart.scalajs.layout.list.centered.CenteredList.CenteredListStyle.style
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

object SearchFieldMui extends SearchField[ Option[ String ] ] {
    override type BackendType = Backend

    type State = Option[ String ]

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object Styles extends StyleSheet.Inline {
        import dsl._

        val root = style( position.relative, display.inlineBlock )
        val menu = style(
            position.absolute,
            top( 100 %% ),
            height.auto,
            paddingTop( 5 px ),
            paddingBottom( 5 px ),
            zIndex( 1000 ),
            maxHeight( 500 px ),
            overflowY.auto,
        )
        val menuItem = style(
            width( 100 %% ),
            paddingTop( 3 px ),
            paddingBottom( 3 px ),
            paddingLeft( 10 px ),
            paddingRight( 10 px ),
            &.hover(
                cursor.pointer,
                backgroundColor.rgba( 0, 0, 0, 0.04 ),
            ),
        )
        val inputClass = style(
            paddingRight( 40 px ),
        )
        val iconClass = style(
            position.absolute,
            right( 5 px ),
            top( 50 %% ),
            scalacss.internal.Attr.real("transform") := "translate( 0%, -50%)",
        )

    }
    Styles.addToDocument()

    class Backend( scope : BackendScope[ SearchField.Props, State ] ) {
        def render( props : SearchField.Props, state : State ) : VdomNode = {

            <.div(
                ^.className := Styles.root.htmlClass + props.classes.root.map( " " + _ ).getOrElse( "" ),
                TextInputMui(
                    props.textInput.copy(
                        classes = props.textInput.classes.copy(
                            input = Some(
                                props.textInput.classes.input.map( _ + " " ).getOrElse( "" ) + Styles.inputClass.htmlClass
                            )
                        )
                    )
                ),
                SearchIconMui(
                    size = Some( types.Large ),
                    color = types.Primary,
                    classes = IconMui.Classes( root = Some( Styles.iconClass.htmlClass ) ),
                ),
                if (props.results.isEmpty ) ""
                else <.div( DartPanelMui( DartPanel.Props(
                    classes = DartPanel.Classes( Some(
                        Styles.menu.htmlClass + props.classes.menu.map( " " + _ ).getOrElse( "" )
                    ) ),
                    element = DartFlexBasic( DartFlex.Props(
                        classes = DartFlex.Classes(
                            items = Some( Styles.menuItem.htmlClass + props.classes.results.root.map( " " + _ ).getOrElse( "" ) ),
                        ),
                        direction = types.Column,
                        align = types.AlignStart,
                        items = props.results.get.map( result => {
                            DartFlex.FlexItem(
                                key = result.key,
                                classes = DartFlex.ItemClasses( result.classes.root ),
                                element = <.div(
                                    ^.onMouseDown --> ( props.onSelect >> result.onSelect ),
                                    result.value match {
                                        case Left( node ) => node
                                        case Right( text ) =>
                                            TextMui( Text.Props( text ) )
                                    }
                                ),
                            )
                        } ),
                    ) ),
                ) ) ),
            )
        }
    }

    val component = ScalaComponent.builder[ SearchField.Props ]
      .initialState[ State ]( None )
      .backend( new Backend( _ ) )
      .renderBackend
      .build

    override def apply(
        props : SearchField.Props ) : Unmounted[ SearchField.Props, State, Backend ] = {
        component( props )
    }
}
