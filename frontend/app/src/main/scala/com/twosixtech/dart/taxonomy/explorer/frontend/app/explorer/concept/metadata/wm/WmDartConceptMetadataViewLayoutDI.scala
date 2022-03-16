package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.wm

import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.form.field.Field
import com.twosixtech.dart.scalajs.layout.form.select.Select
import com.twosixtech.dart.scalajs.layout.form.select.mui.SelectMui
import com.twosixtech.dart.scalajs.layout.form.textarea.{TextArea, TextAreaMui}
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.WmDartConceptDI
import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Event, Negative, Positive, Property}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait WmDartConceptMetadataViewLayoutDI
  extends WmDartConceptMetadataViewLayoutDeps {
    this : DartComponentDI
      with WmDartConceptMetadataViewDI
      with DartContextDeps
      with DartConceptExplorerDI
      with DartCircuitDeps
      with WmDartConceptDI =>

    override type DartConceptMetadataViewRenderContext = Unit
    override type WmDartConceptMetadataViewLayoutState = WmDartConceptMetadataViewLayoutBasic.State
    override val wmDartConceptMetadataViewLayout : WmDartConceptMetadataViewLayout =
        new WmDartConceptMetadataViewLayoutBasic

    class WmDartConceptMetadataViewLayoutBasic extends WmDartConceptMetadataViewLayout {

        import WmDartConceptMetadataViewLayoutBasic._

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._

        object Styles extends StyleSheet.Inline {

            import dsl._

            val spacedField = style( marginBottom( 5 px ) )
            val nodeType = style( marginTop( 30 px ), marginBottom( 30 px ) )
            val discriptionsInputWrapper = style( display.block, width( 100 %% ) )
            val fullWidth = style( width( 100 %% ), overflow.hidden )
            val fillWidth = style( flexGrow( 1 ) )
        }

        Styles.addToDocument()

        override protected def componentDidUpdate(
            modState : ( State => State ) => Callback,
            props : WmDartConceptMetadataView.LayoutProps,
            prevProps : WmDartConceptMetadataView.LayoutProps,
            state : State,
            prevState : State,
            prevContext : DartContext )(
            implicit
            context : DartContext
        ) : Callback = {
            val descUpdate =
                if ( props.descriptions != prevProps.descriptions )
                    ( v : State ) => v.copy( descriptions = props.descriptions.map( v => (false, v) ).toArray )
                else ( v : State ) => v

            val exUpdate =
                if ( props.examples != prevProps.examples )
                    ( v : State ) => v.copy( example = "" )
                else ( v : State ) => v

            val patUpdate =
               if ( props.patterns != prevProps.patterns )
                    ( v : State ) => v.copy( pattern = "" )
               else ( v : State ) => v

            if ( props != prevProps)
                modState( v => patUpdate( exUpdate( descUpdate( v ) ) ) )
            else Callback()
        }

        override protected def componentDidMount(
            modState: ( WmDartConceptMetadataViewLayoutBasic.State => WmDartConceptMetadataViewLayoutBasic.State ) => Callback,
            props: WmDartConceptMetadataView.LayoutProps
        )(
            implicit context: DartContext
        ): Callback = {
            modState( _.copy( descriptions = props.descriptions.map( v => (false, v) ).toArray ) )
        }

        override val initialState : WmDartConceptMetadataViewLayoutBasic.State =
            WmDartConceptMetadataViewLayoutBasic.State( "", "", Array.empty )

        val StringSelect = new SelectMui[ String ]

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render(
            scope : Scope, state : State,
            props : WmDartConceptMetadataView.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import props._

            DartFlexBasic( DartFlex.Props(
                direction = types.Column,
                align = types.AlignStretch,
                classes = DartFlex.Classes(
                    items = Styles.spacedField.cName,
                ),
                items = Vector(
                    DartFlex.FlexItem(
                        TextMui( Text.Props(
                            <.b(
                                if ( isDefined ) "Leaf Node (children not supported)"
                                else "Branch Node",
                            ),
                            size = types.Small,
                        )
                        ),
                        classes = DartFlex.ItemClasses(
                            Styles.nodeType.cName
                        ),
                    ),
                    DartFlex.FlexItem(
                        Field( Field.Props(
                            "Examples:",
                            vertical = true,
                            element = VdomArray(
                                examples.toSeq.sorted.map( ( v : String ) => <.div(
                                    ^.key := v,
                                    HoverButton(
                                        left = true,
                                        remove = true,
                                        onClick = Some( updateExamples( examples.filter( _ != v ) ) ),
                                        element = TextMui( Text.Props( v ) ),
                                    ),
                                ) ).toVdomArray,
                                <.div(
                                    ^.key := "input-section",
                                    HoverButton(
                                        onClick = Some( updateExamples( examples + state.example ) ),
                                        element = TextInputMui( TextInput.Props(
                                            value = Some( state.example ),
                                            onChange = Some( newValue => scope.modState( s => s.copy( example = newValue ) ) ),
                                            onEnter = Some( {
                                                if ( state.example.nonEmpty ) {
                                                    updateExamples( examples + state.example )
                                                      .>>( scope.modState( _.copy( example = "" ) ) )
                                                }
                                                else Callback()
                                            } ),
                                            variant = TextInput.Outlined,
                                            size = types.Small,
                                        ) )
                                    ),
                                ),
                            )
                        ) ),
                    ),
                    DartFlex.FlexItem(
                        Field( Field.Props(
                            "Patterns:",
                            vertical = true,
                            element = VdomArray(
                                patterns.toSeq.sorted.map( v => <.div(
                                    ^.key := v,
                                    HoverButton(
                                        left = true,
                                        remove = true,
                                        onClick = Some( updatePatterns( patterns.filter( _ != v ) ) ),
                                        element = TextMui( Text.Props( v ) ),
                                    ),
                                ) ).toVdomArray,
                                <.div(
                                    ^.key := "patterns-input-div",
                                    HoverButton(
                                        onClick = Some( updatePatterns( patterns + state.pattern ) ),
                                        element = TextInputMui( TextInput.Props(
                                            value = Some( state.pattern ),
                                            onChange = Some( newValue => scope.modState( s => s.copy( pattern = newValue ) ) ),
                                            onEnter = Some(
                                                if ( state.pattern.nonEmpty ) {
                                                    updatePatterns( patterns + state.pattern )
                                                      .>>( scope.modState( _.copy( example = "" ) ) )
                                                }
                                                else Callback()
                                            ),
                                            variant = TextInput.Outlined,
                                            size = types.Small,
                                        ) ),
                                    ),
                                ),
                            )
                        )
                        ),
                    ),
                    DartFlex.FlexItem(
                        classes = DartFlex.ItemClasses( Styles.fullWidth.cName ),
                        element = Field( Field.Props(
                            "Descriptions",
                            vertical = true,
                            classes = Field.Classes( field = Styles.discriptionsInputWrapper.cName ),
                            element = ( state.descriptions.zipWithIndex.map( tup => {
                                val ((edit, desc), index) = tup
                                <.div(
                                    ^.className := Styles.fullWidth.classString ,
                                    ^.key := index,
                                    if ( edit ) {
                                            val cancelCb = {
                                                if ( index < descriptions.length )
                                                    scope.modState( _.copy( descriptions = state.descriptions.updated( index, (false, props.descriptions( index )) ) ) )
                                                else
                                                    scope.modState( _.copy( descriptions = state.descriptions.zipWithIndex.filter( _._2 != index ).map( _._1 ) ) )
                                            }
                                            TextAreaMui(
                                                TextArea.Props(
                                                    Some( desc ),
                                                    autoFocus = true,
                                                    onChange = Some(
                                                        newValue =>
                                                            scope.modState( _.copy( descriptions = state.descriptions.updated( index, (true, newValue) ) ) )
                                                        ),
                                                    onEnter = Some(
                                                        updateDescriptions( state.descriptions.map( _._2 ).filter( _.trim.nonEmpty ) )
                                                    ),
                                                    onEscape = Some( cancelCb ),
                                                    onBlur = Some( cancelCb ),
                                                ),
                                            )
                                    } else {
                                            HoverButton(
                                                left = true,
                                                remove = true,
                                                elementFlex = DartFlex.Grow( 1 ),
                                                onClick = Some( updateDescriptions( descriptions.zipWithIndex.filter( _._2 != index ).map( _._1 ) ) ),
                                                classes = HoverButton.Classes( Styles.fullWidth.cName, elementWrapper = Styles.fillWidth.cName ),
                                                element = TextMui(
                                                    element = desc,
                                                    onClick = Some( scope.modState( _.copy( descriptions = state.descriptions.updated( index, (true, state.descriptions( index )._2) ) ) ) ),
                                                    classes = Text.Classes( Styles.fullWidth.cName ),
                                                )
                                            )
                                    }
                                )
                            } ) ++ Seq(
                                <.div( ButtonMui(
                                    element = "+",
                                    size = Button.Small,
                                    style = Button.Text,
                                    onClick = scope.modState( _.copy( descriptions = state.descriptions :+ ( (true, "") ) ) ),
                                    disabled = state.descriptions.length > props.descriptions.length ||
                                      (examples.isEmpty && patterns.isEmpty),
                                ) )
                            ) ).toVdomArray
                        ) )
                    ),
                    DartFlex.FlexItem(
                        Field( Field.Props(
                            "Polarity",
                            element =
                                StringSelect(
                                    disabled = !isDefined,
                                    value = polarity match {
                                        case Positive => "positive"
                                        case Negative => "negative"
                                    },
                                    onChange = newVal => updatePolarity(
                                        if ( newVal == "positive" ) Positive
                                        else Negative
                                    ),
                                    items = Vector(
                                        Select.Item(
                                            "Positive",
                                            "positive",
                                            Some( "positive" )
                                        ),
                                        Select.Item(
                                            "Negative",
                                            "negative",
                                            Some( "negative" )
                                        ),
                                    ),
                                ),
                        ) ),
                    ),
                    DartFlex.FlexItem(
                        Field( Field.Props(
                            "Semantic Type",
                            element =
                                StringSelect(
                                    disabled = !isDefined,
                                    value = semanticType match {
                                        case Entity => "entity"
                                        case Event => "event"
                                        case Property => "property"
                                    },
                                    onChange = newVal => updateSemanticType(
                                        newVal match {
                                            case "entity" => Entity
                                            case "event" => Event
                                            case "property" => Property
                                        }
                                    ),
                                    items = Vector(
                                        Select.Item(
                                            "Entity",
                                            "entity",
                                        ),
                                        Select.Item(
                                            "Event",
                                            "event",
                                        ),
                                        Select.Item(
                                            "Property",
                                            "property",
                                        ),
                                    ),
                                ),
                        ) ),
                    ),
                )
            ) )
        }
    }

    object WmDartConceptMetadataViewLayoutBasic {

        case class State(
            example : String,
            pattern : String,
            descriptions : Array[ (Boolean, String) ],
        )

    }

}
