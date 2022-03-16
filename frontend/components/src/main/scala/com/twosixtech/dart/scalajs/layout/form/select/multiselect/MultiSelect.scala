package com.twosixtech.dart.scalajs.layout.form.select.multiselect

import com.twosixtech.dart.scalajs.layout.form.select.multiselect.MultiSelect.{ClickType, ExtendClick, MultiClick, MultiDefault, MultiPersist, RegularClick, SelectEntry, SelectMode, SingleSelect}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

import scala.collection.SortedMap
import scala.collection.immutable.ListMap
import scala.language.higherKinds

trait MultiSelect[ ValType, State ] extends ReactComponent[ MultiSelect.Props[ ValType ], State ] {

    def getStateSlice(
        state : ListMap[ ValType, Boolean ],
        startValue : Option[ ValType ],
        endValue : ValType,
    ) : ListMap[ ValType, Boolean ] = {
        val stateAsList = state.toList.map( _._1 )
        val si : Int = stateAsList.indexOf( startValue.getOrElse( -1 ) )
        val ei : Int = stateAsList.indexOf( endValue )
        val (startIndex, endIndex) =
            if ( ei == si ) (ei, si)
            else if ( ei > si ) (si + 1, ei)
            else (ei, si - 1)
        state.slice( startIndex, endIndex + 1 )
    }

    def getNewValues(
        selectMode : SelectMode,
        newClickedValue : ValType,
        lastClickedValue : Option[ ValType ],
        clickType: ClickType,
        prevState : ListMap[ ValType, Boolean ]
    ) : ListMap[ ValType, Boolean ] = {
        selectMode match {
            case SingleSelect =>
                prevState.map( v => v._1 -> ( if ( v._1 == newClickedValue ) true else false ) )
            case MultiPersist =>
                clickType match {
                    case ExtendClick =>
                        prevState ++
                        ( getStateSlice( prevState, lastClickedValue, newClickedValue ).mapValues( _ => true ) )
                    case _ =>
                        prevState + (newClickedValue -> !prevState( newClickedValue ) )
                }
            case MultiDefault =>
                clickType match {
                    case ExtendClick =>
                        prevState ++
                        ( getStateSlice( prevState, lastClickedValue, newClickedValue ).mapValues( _ => true ) )
                    case MultiClick =>
                        prevState + (newClickedValue -> !prevState( newClickedValue ) )
                    case RegularClick =>
                        if ( prevState( newClickedValue ) && (prevState - newClickedValue).forall( !_._2 ) )
                            prevState.map( v => v._1 -> false )
                        else prevState.map( v => v._1 -> ( if ( v._1 == newClickedValue ) true else false ) )
                }
        }
    }

    def apply(
        entries : ListMap[ ValType, SelectEntry ],
        onChange : Map[ ValType, Boolean ] => Callback,
        mode : MultiSelect.SelectMode = MultiSelect.MultiDefault,
        classes : MultiSelect.Classes = MultiSelect.Classes(),
    ) : Unmounted[ MultiSelect.Props[ ValType ], State, BackendType ] =
        apply( MultiSelect.Props( entries, onChange, mode, classes ) )
}

trait MultiSelectObject[ State[ _ ] ] {
    def gen[ ValType ] : MultiSelect[ ValType, State[ ValType ] ]
    lazy val StringMultiSelect : MultiSelect[ String, State[ String ] ] = gen[ String ]
    lazy val IntMultiSelect : MultiSelect[ Int, State[ Int ] ] = gen[ Int ]
}

object MultiSelect {

    trait SelectMode

    case object SingleSelect extends SelectMode // Can only select one at a time
    case object MultiDefault extends SelectMode // Can select multiple by holding shift or cmd
    case object MultiPersist extends SelectMode // Can select multiple by clicking one after the other (or shift)

    trait ClickType
    case object RegularClick extends ClickType
    case object ExtendClick extends ClickType
    case object MultiClick extends ClickType

    case class EntryClasses(
        root : Option[ String ] = None,
        rootOverride : Option[ String ] = None,
        selected : Option[ String ] = None,
        selectedOverride : Option[ String ] = None,
        unselected : Option[ String ] = None,
        unselectedOverride : Option[ String ] = None,
    )

    case class SelectEntry(
        element : VdomNode,
        key : Option[ String ] = None,
        selected : Boolean,
        classes : EntryClasses = EntryClasses(),
    )

    case class Classes(
        root : Option[ String ] = None,
        rootOverride : Option[ String ] = None,
        allEntries : EntryClasses = EntryClasses(),
    )

    case class Props[ ValType ](
        entries : ListMap[ ValType, SelectEntry ],
        onChange : Map[ ValType, Boolean ] => Callback,
        mode : SelectMode = MultiDefault,
        classes : Classes = Classes(),
    )

    object ClassNames {
        val rootClass : String = "multi-select-root"
        val entryClass : String = "multi-select-entry"
        val selectedEntryClass : String = "multi-select-entry-selected"
        val unselectedEntryClass : String = "multi-select-entry-unselected"
    }

}
