package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts

import com.twosixtech.dart.scalajs.layout.form.field.Field
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.form.toggledtextinput.ToggledTextInput
import com.twosixtech.dart.scalajs.layout.text.Text
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.AppProps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.{DartConceptNameDI, DartConceptNameLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.raw.SyntheticKeyboardEvent
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.vdom.{Attr, VdomElement}
import japgolly.scalajs.react.{Callback, ReactEventFromInput}

trait DartConceptNameLayoutDI
  extends DartConceptNameLayoutDeps {
    this : DartContextDeps
      with DartComponentDI
      with DartConceptNameDI =>

    override type DartConceptNameLayoutState = DartConceptNameLayoutBasic.State
    override type DartConceptNameRenderContext = Unit
    override lazy val dartConceptNameLayout : DartConceptNameLayout = new DartConceptNameLayoutBasic

    class DartConceptNameLayoutBasic extends DartConceptNameLayout {
        import DartConceptNameLayoutBasic._

        override protected def componentDidMount(
            cdm : Lifecycle.ComponentDidMount[AppProps[DartConceptName.LayoutProps, SnapshotType, DartContext], State, Backend] ) : Callback = {
            cdm.setState( State( None ) )
        }

        override protected def componentDidUpdate(
            modState : ( DartConceptNameLayoutBasic.State => DartConceptNameLayoutBasic.State ) => Callback,
            props : DartConceptName.LayoutProps,
            prevProps : DartConceptName.LayoutProps,
            state : State,
            prevState : State,
            prevContext : DartContext )(
            implicit
            context : DartContext ) : Callback = {
            if ( props != prevProps ) modState( _ => State( None ) )
            else Callback()
        }

        override def render(
            scope : Scope, state : State,
            props : DartConceptName.LayoutProps )(
            implicit renderProps : SnapshotType,
            context : DartContext ) : VdomElement = {

            val searchedNames : String =
                state.name.map( v => props.searchNames( v ).mkString( ", " ) ).getOrElse( "" )

            <.div(
                Field( Field.Props( "Name", <.span(
                    ToggledTextInput(
                        element = Left( Text.Props( state.name.getOrElse( props.name ) : String ) ),
                        input = TextInput.Props(
                            Some( state.name.getOrElse( props.name ) ),
                            onChange = Some( newValue => scope.setState( State( Some( newValue ) ) ) ),
                            size = types.Small,
                            variant = TextInput.Outlined,
                            autoFocus = true,
                        ),
                        onBlur = Some( scope.setState( State( None ) ) ),
                        onEnter = Some {
                            if ( state.name.isDefined
                                 && state.name.exists( _.nonEmpty )
                                 && !state.name.contains( props.name ) ) {
                                props.updateName( state.name.get ) >> scope.setState( State( None ) )
                            } else scope.setState( State( None ) )
                        },
                    ),
                ) ) ),
                searchedNames,
            )

        }

        override val initialState : State = State( None )
    }

    object DartConceptNameLayoutBasic {
        case class State(
            name : Option[ String ],
        )
    }

}
