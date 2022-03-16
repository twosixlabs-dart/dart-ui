package com.twosixtech.dart.scalajs.layout.div.dndlist.utils

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.Dimensions
import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedListContext.ListId
import IdUtils.DroppableIdRegular
import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.Dimensions
import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.Dimensions
import react.beautifuldnd.DraggableLocation

object ListUtils {

    def breakUpLists[ T ]( source : Seq[ T ], dimensions : Map[ T, Dimensions ], maxWidth : Int ) : Seq[ Seq[ T ] ] = {
        val wDims = dimensions.view.mapValues( _.w )
        val result = source.map( t => (t, wDims.getOrElse( t, 100 )) ).foldLeft( Seq( (Seq[ T ](), 0) ) ) { ( agg : Seq[ (Seq[ T ], Int) ], ele : (T, Int) ) =>
            if ( agg.isEmpty ) Seq( (Seq( ele._1 ), ele._2) )
            else {
                if ( agg.head._2 + ele._2 > maxWidth ) (Seq( ele._1 ), ele._2) +: agg
                else (agg.head._1 :+ ele._1, agg.head._2 + ele._2) +: agg.tail
            }
        }.map( _._1 ).reverse
        result.filter( _.nonEmpty )
    }

    def rearrangeList[ T ]( source : Seq[ T ], sourceIndex : Int, destinationIndex : Int ) : Seq[ T ] = {
        val (beginning, elePlusEnding) = source.splitAt( sourceIndex )
        val ending = elePlusEnding.drop( 1 )
        val ele = elePlusEnding.head
        if ( destinationIndex >= source.length ) ( beginning ++ ending ) :+ ele
        else {
            val (newBeginning, newEnding) = ( beginning ++ ending ).splitAt( destinationIndex )
            val result = newBeginning ++ ( ele +: newEnding )
            result
        }
    }

    def rearrangeLists[ T ]( lists : Map[ ListId, Seq[ Seq[ T ] ] ], source : ( ListId, Int, Int ), destination : (ListId, Int, Int) ) : Map[ ListId, Seq[ Seq[ T ] ] ] = {
        if ( source == destination )Map[ ListId, Seq[ Seq[ T ] ] ]()
        else {
            val (sourceId, sourceListIndex, sourceElementIndex) = source
            val (destinationId, destinationListIndex, destinationElementIndex) = destination

            val res = if ( sourceId == destinationId && sourceListIndex == destinationListIndex ) {
                val destEleInd = if ( destinationElementIndex < lists( sourceId )( sourceListIndex ).length ) destinationElementIndex else lists( sourceId )( sourceListIndex ).length - 1
                val res = lists + ( sourceId -> lists( sourceId ).updated( sourceListIndex, rearrangeList[ T ]( lists( sourceId )( sourceListIndex ), sourceElementIndex, destEleInd ) ) )
                res
            } else if ( sourceId == destinationId ) {
                val ele : T = lists( sourceId )( sourceListIndex )( sourceElementIndex )
                lists + ( sourceId -> {
                    val newMap = ( lists( sourceId ).zipWithIndex collect {
                        case (seq : Seq[ T ], i) if i == sourceListIndex =>
                            seq.take( sourceElementIndex ) ++ seq.drop( sourceElementIndex + 1 )
                        case (seq : Seq[ T ], i) if i == destinationListIndex =>
                            if ( destinationElementIndex < seq.length ) seq.zipWithIndex.flatMap {
                                case (t, j) if j == destinationElementIndex => Seq( ele, t )
                                case (t, _) => Seq( t )
                            } else seq :+ ele
                        case (seq : Seq[ T ], _) => seq
                    } )
                    newMap
                } )
            } else {
                val ele : T = lists( sourceId )( sourceListIndex )( sourceElementIndex )
                lists + ( sourceId -> ( lists( sourceId ).zipWithIndex collect {
                    case (seq : Seq[ T ], i) if i == sourceListIndex => seq.take( sourceElementIndex ) ++ seq.drop( sourceElementIndex + 1 )
                    case (seq : Seq[ T ], _) => seq
                } ) ) + ( destinationId -> ( lists( destinationId ).zipWithIndex collect {
                    case (seq : Seq[ T ], i) if i == destinationListIndex =>
                        if ( destinationElementIndex < seq.length ) seq.zipWithIndex flatMap {
                            case (t, j) if j == destinationElementIndex => Seq( ele, t )
                            case (t, _) => Seq( t )
                        } else seq :+ ele
                    case (seq : Seq[ T ], _) => seq
                } ) )
            }
            res
        }
    }
}
