package com.twosixtech.dart.scalajs.layout.div.dndlist

import com.twosixtech.dart.scalajs.layout.div.dndlist.utils.{IdUtils, ListUtils}
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.raw.React.Element
import japgolly.scalajs.react.vdom.html_<^._
import react.beautifuldnd.{DragDropContext, DropResult, ResponderProvided}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExportTopLevel

object DndWrappedListContext {

    type ListId = String

    case class ListIndex( listId : ListId, index : Int )

    case class Props(
        onRearrange : Map[ ListId, Seq[ DndWrappedList.Key ] ] => Callback,
        children : Context => VdomElement
    )

    trait JsProps extends js.Object {
        val onRearrange : js.Function1[ js.Dictionary[ js.Array[ String ] ], Unit ]
        val children : js.Function1[ Context, Element ]
    }

    case class State(
        lists : Map[ ListId, Seq[ Seq[ DndWrappedList.Key ] ] ]
    )

    case class Context(
        updateLists : (ListId, Seq[ Seq[ DndWrappedList.Key ] ]) => Callback
    )

    class Backend( $: BackendScope[ Props, State ] ) {

        def updateLists( listId : ListId, lists : Seq[ Seq[ DndWrappedList.Key ] ] ) : Callback = {
            $.modState( (s : State) => {
                val newState = State( s.lists + (listId -> lists) )
                newState
            } )
        }

        def render( props : Props, state : State ) : VdomElement = {
            def onDragEnd( dropResult : DropResult, responderProvided : ResponderProvided ) : Callback = {
                if ( dropResult.destination.toOption.isEmpty ) Callback()
                else if ( dropResult.source == dropResult.destination.toOption.get ) Callback()
                else {
                    val sourceTuple = IdUtils.getLocationTupleFromDraggableLocation( state.lists, dropResult.source )
                    val destinationTuple = IdUtils.getLocationTupleFromDraggableLocation( state.lists, dropResult.destination.toOption.get )
                    val changedList : Map[ListId, Seq[ Seq[ DndWrappedList.Key ] ] ] =  ListUtils.rearrangeLists( state.lists, sourceTuple, destinationTuple )
                    if ( changedList.isEmpty || changedList.forall {
                        case (listId, list) => state.lists( listId ).flatten == list.flatten
                    } ) {
                        Callback( )
                    }
                    else $.modState( ( s : State ) => State( s.lists ++ changedList ) ) >> props.onRearrange( changedList.mapValues( _.flatten ).toMap )
                }
            }

            DragDropContext( onDragEnd = onDragEnd ) {
                props.children( Context( updateLists ) )
            }
        }

    }

    private val build = ScalaComponent.builder[ Props ]
      .initialState( State( Map[ ListId, Seq[ Seq[ DndWrappedList.Key ] ] ]() ) )
      .renderBackend[ Backend ]
      .build

    def apply( props : Props ) : Unmounted[Props, State, Backend ] = build( props )

    private def convertProps( jsProps : JsProps ) : Props = {
        def onRearrange( lists : Map[ ListId, Seq[ DndWrappedList.Key ] ] ) : Callback = Callback {
            jsProps.onRearrange( {
                val arrayMap : Map[ListId, js.Array[DndWrappedList.Key ] ] = lists.mapValues( _.toJSArray ).toMap
                arrayMap.toJSDictionary
            } )
        }

        def children( ctx : Context ) : VdomElement = {
            VdomElement( jsProps.children( ctx ) )
        }


        Props( onRearrange, children )
    }

    @JSExportTopLevel( "DndWrappedListContext" )
    val exportJsComponent =
        build
          .cmapCtorProps[JsProps]( convertProps ) // Change props from JS to Scala
          .toJsComponent // Create a new, real JS component
          .raw // Leave the nice Scala wrappers behind and obtain the underlying JS value
}
