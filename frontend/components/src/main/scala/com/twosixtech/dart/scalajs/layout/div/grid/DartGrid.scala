package com.twosixtech.dart.scalajs.layout.div.grid

import com.twosixtech.dart.scalajs.layout.types.{Alignment, Direction, Row, AlignStart}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.vdom.VdomNode

trait DartGrid[ State ] extends ReactComponent[ DartGrid.Props, State ]

object DartGrid {

    case class BreakPoints(
        xs : Option[ Int ] = None,
        sm : Option[ Int ] = None,
        md : Option[ Int ] = None,
        lg : Option[ Int ] = None,
        xl : Option[ Int ] = None,
    )

    object BreakPoints {
        def fullWidth : BreakPoints = BreakPoints( xs = Some( 12 ) )
    }

    case class ItemClasses(
        root : Option[ String ] = None,
    )

    case class GridItem(
        element : VdomNode,
        breakPoints : BreakPoints = BreakPoints(),
        key : Option[ String ] = None,
        classes : ItemClasses = ItemClasses(),
    )

    case class Classes(
        container : Option[ String ] = None,
        items : Option[ String ] = None,
    )

    case class Props(
        direction : Direction = Row,
        align : Alignment = AlignStart,
        wrap : Boolean = true,
        items : Vector[ GridItem ],
        classes : Classes = Classes(),
    )

}
