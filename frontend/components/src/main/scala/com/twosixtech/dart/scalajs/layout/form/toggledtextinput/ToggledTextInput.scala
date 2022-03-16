package com.twosixtech.dart.scalajs.layout.form.toggledtextinput

import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaComponent}


object ToggledTextInput {

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    private object Styles extends StyleSheet.Inline {
        import dsl._
        val clickable = style( display.inlineBlock, &.hover( cursor.pointer ), minWidth( 50 px ), minHeight( 24 px ) )
        val inputWrapper = style()
    }
    Styles.addToDocument()

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        element : Either[ Text.Props, VdomNode ],
        input : TextInput.Props,
        onClick : Option[ Callback ] = None,
        onBlur : Option[ Callback ] = None,
        onEnter : Option[ Callback ] = None,
        setAsInput : Option[ Boolean ] = None,
        classes : Classes = Classes(),
    )

    case class State(
        input : Boolean,
    )

    class Backend( scope : BackendScope[ Props, State ] ) {

        def render( props : Props, state : State ) : VdomNode = {
            ( props.setAsInput match {
                case Some( bool ) => State( bool )
                case None => state
            } ) match {
                case State( false ) =>
                    <.span(
                        ^.className := Styles.clickable.htmlClass + props.classes.root.map( " " + _ ).getOrElse( "        " ),
                        ^.onClick --> ( scope.setState( State( true ) ) >> props.onClick.getOrElse( Callback() ) ),
                        props.element match {
                            case Left( textProps ) => TextMui( textProps )
                            case Right( ele ) => ele
                        }
                    )
                case State( true ) =>
                    <.span(
                        ^.onBlur --> ( scope.setState( State( false ) ) >> props.onBlur.getOrElse( Callback() ) ),
                        ^.className := Styles.inputWrapper.htmlClass + props.classes.root.map( " " + _ ).getOrElse( "" ),
                        TextInputMui( props.input.copy(
                            onEnter = Some(
                                props.input.onEnter.getOrElse( Callback() )
                                  .>>( scope.setState( State( false ) ) >> props.onEnter.getOrElse( Callback() ) )
                            ),
                            onEscape = Some(
                                props.input.onEscape.getOrElse( Callback() )
                                  .>>( scope.setState( State( false ) ) >> props.onBlur.getOrElse( Callback() ) )
                            )
                        ) ),
                    )
            }

        }

    }

    val component = ScalaComponent.builder[ Props ]
      .initialState( State( false ) )
      .backend( new Backend( _ ) )
      .renderBackend
      .build

    def apply( props : Props ) : Unmounted[ Props, State, Backend ] = component( props )

    def apply(
        element : Either[ Text.Props, VdomNode ],
        input : TextInput.Props,
        onClick : Option[ Callback ] = None,
        onBlur : Option[ Callback ] = None,
        onEnter : Option[ Callback ] = None,
        setAsInput : Option[ Boolean ] = None,
        classes : Classes = Classes(),
    ) : Unmounted[ Props, State, Backend ] = apply( Props( element, input, onClick, onBlur, onEnter, setAsInput, classes ) )

}
