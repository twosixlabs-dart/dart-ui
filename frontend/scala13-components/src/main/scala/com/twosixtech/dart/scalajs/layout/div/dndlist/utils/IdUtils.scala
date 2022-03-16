package com.twosixtech.dart.scalajs.layout.div.dndlist.utils

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedListContext.ListId
import react.beautifuldnd.{DraggableLocation, DroppableId}

import scala.util.matching.Regex

object IdUtils {

    def generateDroppableId( listId : ListId, index : Int ) : DroppableId = s"$listId:$index"
    def generateDroppableIdBefore( listId : ListId, index : Int ) : DroppableId = s"$listId:before:$index"
    def generateDroppableIdLast( listId : ListId ) : DroppableId = s"$listId:last"

    val DroppableIdRegular : Regex = """([^:]+):(\d+)$""".r
    val DroppableIdBefore : Regex = """([^:]+):before:(\d+)$""".r
    val DroppableIdLast : Regex = """([^:]+):last$""".r

    def getLocationTupleFromDraggableLocation( source : Map[ ListId, Seq[ Seq[ Any ] ] ], location : DraggableLocation ) : (ListId, Int, Int) = {
        location.droppableId match {
            case DroppableIdRegular( listId, listIndexStr ) => (listId, listIndexStr.toInt, location.index)
            case DroppableIdBefore( listId, listIndexStr ) =>
                val listIndex = listIndexStr.toInt
                (listId, listIndex, 0)
            case DroppableIdLast( listId ) => (listId, source( listId ).length - 1, source( listId ).last.length)
        }
    }
}
