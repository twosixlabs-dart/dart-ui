package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.form.field.Field
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.form.toggledtextinput.ToggledTextInput
import com.twosixtech.dart.scalajs.layout.icon.Icons.EditIconMui
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.AppProps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.{DartConceptParentDI, DartConceptParentLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.{Callback, ReactEventFromInput}
import org.scalajs.dom.window

trait DartConceptParentLayoutDI
  extends DartConceptParentLayoutDeps {
    this : DartContextDeps
      with DartComponentDI
      with DartConceptParentDI
      with DartConceptDeps =>

    override type DartConceptParentLayoutState = DartConceptParentLayoutBasic.State
    override type DartConceptParentRenderContext = Unit
    override lazy val dartConceptParentLayout : DartConceptParentLayout = new DartConceptParentLayoutBasic

    class DartConceptParentLayoutBasic extends DartConceptParentLayout {
        import DartConceptParentLayoutBasic._

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object Styles extends StyleSheet.Inline {
            import dsl._
            val cursorPointer = style( cursor.pointer )
        }
        Styles.addToDocument()
        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override protected def componentDidUpdate(
            modState : ( DartConceptParentLayoutBasic.State => DartConceptParentLayoutBasic.State ) => Callback,
            props : DartConceptParent.LayoutProps,
            prevProps : DartConceptParent.LayoutProps,
            state : State,
            prevState : State,
            prevContext : DartContext
        )(
            implicit context : DartContext
        ) : Callback = {
            if ( props.name != prevProps.name ) modState( _ => State( None ) )
            else Callback()
        }

        override def render(
            scope : Scope, state : State,
            props : DartConceptParent.LayoutProps )(
            implicit renderProps : SnapshotType,
            context : DartContext ) : VdomElement = {

            <.div(
                Field( Field.Props( "Parent", <.span(
                    state.parentName match {
                        case None =>
                            DartFlexBasic( DartFlex.Props(
                                align = types.AlignCenter,
                                items = Vector(
                                    DartFlex.FlexItem(
                                        TextMui( new Text.Props(
                                            onClick = Some( props.navigateToParent ),
                                            classes = Text.Classes( Styles.cursorPointer.cName ),
                                            element = state.parentName.getOrElse( props.name.getOrElse( "Root" ) ) : String,
                                        ) )
                                    ),
                                    DartFlex.FlexItem(
                                        IconButtonMui( IconButton.Props(
                                            icon = EditIconMui(
                                                size = Some( types.Small ),
                                                color = types.Primary,
                                            ),
                                            onClick = scope.modState( _.copy( parentName = Some( "" ) ) ),
                                        ) )
                                    )
                                ),
                            ))
                        case Some( inputValue ) =>
                            TextInputMui( TextInput.Props(
                                Some( inputValue ),
                                onChange = Some( newValue => {
                                    scope.modState( _.copy( parentName = Some( newValue ) ) )
                                } ),
                                size = types.Small,
                                variant = TextInput.Outlined,
                                autoFocus = true,
                                placeholder = props.name,
                                onBlur = Some(
                                    _ => scope.modState( _.copy( parentName = None ) )
//                                    _ => Callback(
//                                        window.setTimeout( () => scope.setState( State( None ) ).runNow(), 1000 ),
//                                    ),
                                ),
                            ) )
                    },
                ) ) ),
                state.parentName.map( parentName => {
                    val searchedPaths = props
                      .searchParents( parentName )
                      .toList
                      .map( ( resultTup ) => {
                          val (pathString, chooseCallback) = resultTup
                          <.span(
                              ^.display := "inline-block",
                              ^.key := pathString,
                              ButtonMui( Button.Props(
                                  element = pathString,
                                  onMouseDown = chooseCallback,
                                  style = Button.Text,
                                  size = Button.Small,
                              ) )
                          )
                      } )

                    <.div(
                        ^.display := "flex",
                        ^.flexDirection := "column",
                        if ( props.name.isEmpty ) searchedPaths.toVdomArray
                        else {
                            ( <.span(
                                ^.display := "inline-block",
                                ^.key := "root",
                                ButtonMui( Button.Props(
                                    element = "Root",
                                    onMouseDown = props.removeParent,
                                    style = Button.Text,
                                    size = Button.Small,
                                ) )
                            ) +: searchedPaths ).toVdomArray
                        }
                    )
                } ).getOrElse( "" ),
            )

        }

        override val initialState : State = State( None )
    }

    object DartConceptParentLayoutBasic {
        case class State(
            parentName : Option[ String ],
        )
    }

}
