package com.twosixtech.dart.scalajs.layout.div.dndlist.utils

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.{Dimensions, Key}
import japgolly.scalajs.react.CallbackTo
import japgolly.scalajs.react.Ref.Simple
import org.scalajs.dom.html.Element

object MeasureUtils {
    def getDimensions( key : Key, ref : Simple[ Element ] ) : CallbackTo[ Dimensions ] = {
        ref.get.map( ele => Dimensions( ele.offsetHeight.round.toInt, ele.offsetWidth.round.toInt ) ).getOrElse( Dimensions( 100, 100 ) )
    }
}
