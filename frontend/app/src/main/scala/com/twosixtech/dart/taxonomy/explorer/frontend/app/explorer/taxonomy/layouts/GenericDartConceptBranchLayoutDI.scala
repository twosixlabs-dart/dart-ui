package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.layouts

import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.Button.{Plain, Primary, Small}
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.withdimensions.{WithDimensions, WithDimensionsTranslation}
import com.twosixtech.dart.scalajs.layout.events.KeyHandler
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.icon.Icons.RightIconMui
import com.twosixtech.dart.scalajs.layout.list.centered.{CenteredList, CenteredListProps, ListItem}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.layout.types.{AlignCenter, AlignStart, Column, Row}
import com.twosixtech.dart.scalajs.react.SimpleFacade
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.{DartConceptBranchDI, DartConceptBranchLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.vdom.{Attr, VdomElement}
import japgolly.scalajs.react.{Callback, CallbackTo, ReactEventFromInput}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

trait GenericDartConceptBranchLayoutDI
  extends DartConceptBranchLayoutDeps {
    this : DartConceptBranchDI
      with DartComponentDI
      with DartContextDeps
      with DartComponentDI
      with DartConceptBranchDI
      with DartConceptDeps =>

    override type DartConceptBranchRenderContext = Unit
    override type DartConceptBranchLayoutState = String
    override lazy val dartConceptBranchLayout : DartConceptBranchLayout = new GenericDartConceptBranchLayout

    class GenericDartConceptBranchLayout extends DartConceptBranchLayout {
        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object BranchStyles extends StyleSheet.Inline {
            import dsl._

            val fullHeight = style( height( 100 %% ) )
            val fullWidth = style( width( 100 %% ) )

            val selectedConcept = style(
                fontSize( 120 %% ),
                minWidth( 0 px ),
                marginLeft( 5 px ),
            )

            val selectedConceptHoverWrapper = style( paddingRight( 25 px ) )

            val hoverButton = style( padding( 0 px ), marginLeft( 10 px ), marginRight( 2 px ) )

            val unselectedConcept = style(
                minWidth( 0 px ),
            )

            val conditionalHidden = style(
                fontSize.inherit,
            )

            val hoverCondition = style(
                unsafeChild( "." + this.conditionalHidden.htmlClass )(
                    display.none,
                ),
                &.hover(
                    unsafeChild( "." + this.conditionalHidden.htmlClass )(
                        display.inherit,
                    ),
                )
            )

            val relativeFrame = style(
                position.relative,
            )

            val stripeContainer = style(
                pointerEvents.none,
                position.absolute,
                top( 0 px ),
                bottom( 0 px ),
                left( 0 px ),
                right( 0 px ),
                display.flex,
            )

            val stripe = style(
                margin.auto,
                height( 25 px ),
                width( 100 %% ),
                backgroundColor.lightgray,
            )

            val rightArrow = style(
                paddingRight( 20 px ),
                zIndex( 5 ),
            )

            val centeredList = style(
                paddingTop( 10 px ),
                paddingBottom( 10 px ),
            )

            val paddingLeftStyle = style( paddingLeft( 5 px ) )
        }

        BranchStyles.addToDocument()

//        override protected def componentDidMount(
//            modState : ( String => String ) => Callback,
//            props : DartConceptBranch.LayoutProps )
//          ( implicit context : DartContext ) : Callback = Callback( BranchStyles.addToDocument() )

//        override protected def shouldComponentUpdate(
//            props : DartConceptBranch.LayoutProps, nextProps : DartConceptBranch.LayoutProps,
//            state : String, nextState : String,
//            nextContext : DartContext )(
//            implicit
//            context : DartContext ) : CallbackTo[ Boolean ] = {
//            CallbackTo(
//                props.concepts != nextProps.concepts ||
//                props.choice != nextProps.choice ||
//                state != nextState
//            )
//        }

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render( scope : Scope, state: String, props : DartConceptBranch.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            val addConceptCallback : Callback =
                props.addConcept( state ) >> scope.setState( "" )

            val concepts = props.concepts.toVector.sortBy( _.name )

            val centeredItem =
                if ( concepts.exists( _.isSelected ) ) Some( concepts.map( _.isSelected ).indexOf( true ) )
                else None

            val branchList = {
                WithDimensions( WithDimensionsTranslation.Props(
                    setHeight = _ => Callback(),
                    setWidth = _ => Callback(),
                    render = rp => {
                        CenteredList( CenteredListProps(
                            centeredItem = centeredItem,
                            height = rp.outerHeight,
                            items = concepts.map( conceptData => {
                                val selected = conceptData.isSelected
                                ListItem(
                                    Some( conceptData.name ),
                                    <.div(
                                        ^.style := ( js.Dictionary( "marginBottom" -> "5px" ) ),
                                        BranchStyles.hoverCondition + BranchStyles.relativeFrame,
                                        if ( selected ) <.div( BranchStyles.stripeContainer, <.div( BranchStyles.stripe ) )
                                        else "",
                                        <.span(
                                            DartFlexBasic ( new DartFlex.Props(
                                                direction = Row,
                                                align = AlignCenter,
                                                items = Vector(
                                                    DartFlex.FlexItem(
                                                        element =
                                                          HoverButton(
                                                              remove = true,
                                                              left = true,
                                                              onClick = Some( conceptData.remove ),
                                                              classes = HoverButton.Classes(
                                                                  root = BranchStyles.selectedConceptHoverWrapper.cName,
                                                                  firstItem = BranchStyles.hoverButton.cName,
                                                              ),
                                                              element = ButtonMui( Button.Props(
                                                                  TextMui( Text.Props(
                                                                      if ( selected ) <.b( conceptData.name )
                                                                      else conceptData.name
                                                                  ) ),
                                                                  conceptData.select,
                                                                  style = Button.Text,
                                                                  size = Button.Small,
                                                                  color =
                                                                    if ( selected ) Primary
                                                                    else Plain,
                                                                  classes = Button.Classes(
                                                                      if ( selected ) BranchStyles.selectedConcept.cName
                                                                      else BranchStyles.unselectedConcept.cName
                                                                  )
                                                              ) ),
                                                          )
                                                    ),
                                                    DartFlex.FlexItem(
                                                        flex = DartFlex.Grow( 1 ),
                                                        element = <.span(),
                                                        key = Some( "spacer" )
                                                    ),
                                                    DartFlex.FlexItem(
                                                        element = {
                                                            if ( selected )
                                                                RightIconMui( color = types.Primary, size = Some( types.Large ) )
                                                            else ""
                                                        },
                                                        key = Some( "right-arrow" ),
                                                        classes = DartFlex.ItemClasses(
                                                            root = BranchStyles.rightArrow.cName
                                                        )
                                                    ),
                                                ),
                                            ) ),
                                        ),
                                        ^.key := conceptData.name
                                    ) // TODO: need to add branch render context
                                )
                            } )
                        ) )
                    }
                ) )
            }

            val newNodeInput = {
                HoverButton(
                    onClick = Some( addConceptCallback ),
                    element = TextInputMui( TextInput.Props(
                        value = Some( state ),
                        onChange = Some( ( newValue : String ) => scope.setState( newValue ) ),
                        onEnter = Some( addConceptCallback ),
                        size = types.Small,
                        variant = TextInput.Outlined
                    ) )
                )
            }

            DartFlexBasic( DartFlex.Props(
                direction = Column,
                align = AlignStart,
                items = Vector(
                    DartFlex.FlexItem(
                        element = branchList,
                        flex = DartFlex.Grow( 1 ),
                        classes = DartFlex.ItemClasses(
                            BranchStyles.centeredList.cName,
                        ),
                        key = Some( "concept-branch-list" ),
                    ),
                    DartFlex.FlexItem(
                        element = newNodeInput,
                        key = Some( "concept-branch-add" )
                    ),
                ),
                classes = DartFlex.Classes(
                    container = (BranchStyles.fullHeight + BranchStyles.fullWidth).cName,
                    items = BranchStyles.fullWidth.cName,
                )
            ) )

        }

        override val initialState : String = ""
    }
}
