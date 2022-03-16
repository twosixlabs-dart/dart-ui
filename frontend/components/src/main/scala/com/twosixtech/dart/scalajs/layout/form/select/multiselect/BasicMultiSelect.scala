package com.twosixtech.dart.scalajs.layout.form.select.multiselect
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.raw.SyntheticMouseEvent
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.Node

import scala.collection.immutable.ListMap

class BasicMultiSelect[ ValType ] extends MultiSelect[ ValType, BMSState[ ValType ] ] {
    override type BackendType = Backend

    import BasicMultiSelect.Styles._
    import BasicMultiSelect._
    import MultiSelect._

    class Backend( scope : BackendScope[ Props[ ValType ], BMSState[ ValType ] ] ) {

        def clickHandler( value : ValType )( e : SyntheticMouseEvent[ Node ] ) : Callback = {
            val clickType = {
                if ( e.metaKey || e.ctrlKey ) MultiClick
                else if ( e.shiftKey ) {
                    ExtendClick
                }
                else RegularClick
            }

            for {
                props <- scope.props
                state <- scope.state
                newValues <- CallbackTo(
                    getNewValues(
                        props.mode,
                        value,
                        state.lastClickedValue,
                        clickType,
                        props.entries.map( tup => tup._1 -> tup._2.selected ),
                    ),
                )
                _ <-
                  if ( newValues.exists( _._2 ) ) scope.modState( _.copy( lastClickedValue = Some( value ) ) )
                  else if ( state.lastClickedValue.nonEmpty ) scope.modState( _.copy( lastClickedValue = None ) )
                  else Callback()
                _ <- props.onChange( newValues )
            } yield ()
        }

        def render( props : Props[ ValType ], state : BMSState[ ValType ] ) : VdomNode = {
            import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

            val finalRootStyle =
                ClassNames.rootClass and
                props.classes.root and
                ( rootStyle overriddenBy props.classes.rootOverride )

            <.div(
                finalRootStyle,
                props.entries.zipWithIndex.map( { case ((value, entry), i) =>
                    val entryStyleWithoutSelection = {
                        ClassNames.entryClass and
                        props.classes.allEntries.root and
                        entry.classes.root and
                        ( entryStyle overriddenBy ( props.classes.allEntries.rootOverride and entry.classes.rootOverride ) )
                    }

                    val finalSelectedEntryStyle = {
                        ClassNames.selectedEntryClass and
                        props.classes.allEntries.selected and
                        entry.classes.selected and
                        ( selectedEntryStyle overriddenBy ( props.classes.allEntries.selectedOverride and entry.classes.selectedOverride ) )
                    }

                    val finalUnselectedEntryStyle = {
                        ClassNames.unselectedEntryClass and
                        props.classes.allEntries.unselected and
                        entry.classes.unselected and
                        ( unselectedEntryStyle overriddenBy ( props.classes.allEntries.unselectedOverride and entry.classes.unselectedOverride ) )
                    }

                    val finalEntryStyle = entryStyleWithoutSelection and {
                        if( entry.selected ) finalSelectedEntryStyle
                        else finalUnselectedEntryStyle
                    }

                    <.div(
                        ^.key := entry.key.getOrElse( s"multi-select-entry-$i" ),
                        finalEntryStyle,
                        ^.onMouseDown ==> clickHandler( value ),
                        entry.element,
                    )
                } ).toVdomArray
            )
        }
    }

    val component = ScalaComponent.builder[ MultiSelect.Props[ ValType ] ]
      .initialState( BMSState[ ValType ]( None ) )
      .backend( new Backend( _ ) )
      .renderBackend
      .build

    override def apply( props : Props[ ValType ] ) : Unmounted[ Props[ValType], BMSState[ ValType], Backend ] =
        component( props )
}

private[select] case class BMSState[ ValType ](
    lastClickedValue : Option[ ValType ]
)

object BasicMultiSelect extends MultiSelectObject[ BMSState ] {

    import scalacss.DevDefaults._
    object Styles extends StyleSheet.Inline {
        import dsl._

        val rootStyle = style()
        val entryStyle = style( cursor.default )
        val selectedEntryStyle = style(
            backgroundColor.lightblue,
            userSelect.none,
            borderTop( 1.px, dotted, gray ),
            borderBottom( 1.px, dotted, gray ),
        )
        val unselectedEntryStyle = style(
            paddingTop( 1.px ),
            paddingBottom( 1.px ),
        )
    }
    Styles.addToDocument()

    override def gen[ ValType ] : MultiSelect[ ValType, BMSState[ ValType ] ] = new BasicMultiSelect[ ValType ]

}
