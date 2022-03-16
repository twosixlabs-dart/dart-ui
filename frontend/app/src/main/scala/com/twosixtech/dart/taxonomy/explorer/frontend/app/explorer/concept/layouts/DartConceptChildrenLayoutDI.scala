package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts

import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.form.field.Field
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.{DartConceptChildrenDI, DartConceptChildrenLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait DartConceptChildrenLayoutDI
  extends DartConceptChildrenLayoutDeps {
    this : DartContextDeps
      with DartComponentDI
      with DartConceptChildrenDI
      with DartConceptDeps =>

    override type DartConceptChildrenLayoutState = DartConceptChildrenLayoutBasic.State
    override type DartConceptChildrenRenderContext = Unit
    override lazy val dartConceptChildrenLayout : DartConceptChildrenLayout = new DartConceptChildrenLayoutBasic

    class DartConceptChildrenLayoutBasic extends DartConceptChildrenLayout {
        import DartConceptChildrenLayoutBasic._

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object Styles extends StyleSheet.Inline {
            import dsl._
            val cursorPointer = style( cursor.pointer )
        }
        Styles.addToDocument()
        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override protected def componentDidUpdate(
            modState : ( DartConceptChildrenLayoutBasic.State => DartConceptChildrenLayoutBasic.State ) => Callback,
            props : DartConceptChildren.LayoutProps,
            prevProps : DartConceptChildren.LayoutProps,
            state : State,
            prevState : State,
            prevContext : DartContext )(
            implicit
            context : DartContext ) : Callback = {
            if ( props != prevProps ) modState( _ => State( "" ) )
            else Callback()
        }

        override def render(
            scope : Scope, state : State,
            props : DartConceptChildren.LayoutProps )(
            implicit renderProps : SnapshotType,
            context : DartContext ) : VdomElement = {

            import props._

            val stateNameIsEmpty = state.childName.trim.isEmpty

            Field( Field.Props(
                label = "Children",
                vertical = true,
                element = <.div(
                    <.span(
                        concepts.toSeq.sorted.map( childName => {
                            <.div(
                                ^.key := childName,
                                HoverButton(
                                    left = true,
                                    remove = true,
                                    onClick = Some( removeConcept( childName ) ),
                                    element = TextMui( Text.Props(
                                        element = childName,
                                        onClick = Some( props.navigateToChild( childName ) ),
                                        classes = Text.Classes( Styles.cursorPointer.cName ),
                                    ) ),
                                ),
                            )
                        } ).toVdomArray,
                        HoverButton(
                            onClick = if ( !stateNameIsEmpty ) Some( addConcept( state.childName ) ) else None,
                            element = TextInputMui( TextInput.Props(
                                value = Some( state.childName ),
                                onChange = Some( newValue => scope.setState( State( newValue ) ) ),
                                onEnter = if ( !stateNameIsEmpty ) Some( addConcept( state.childName ) ) else None,
                                size = types.Small,
                                variant = TextInput.Outlined,
                            ) ),
                        )
                    )
                )
            ) )
        }

        override val initialState : State = State( "" )
    }

    object DartConceptChildrenLayoutBasic {
        case class State(
            childName : String,
        )
    }

}
