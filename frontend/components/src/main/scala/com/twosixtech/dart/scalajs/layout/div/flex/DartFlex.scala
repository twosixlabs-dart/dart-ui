package com.twosixtech.dart.scalajs.layout.div.flex

import com.twosixtech.dart.scalajs.layout.div.flex.DartFlex.ItemClasses
import com.twosixtech.dart.scalajs.layout.types.{AlignStart, Alignment, Direction, Justification, JustifyStart, Row}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait DartFlex[ State ] extends ReactComponent[ DartFlex.Props, State ] {
    import DartFlex._

    def apply(
        direction : Direction = Row,
        align : Alignment = AlignStart,
        justify : Justification = JustifyStart,
        items : Vector[ FlexItem ],
        classes : Classes = Classes(),
    ) : Unmounted[ Props, State, BackendType ] = apply( Props( direction, align, justify, items, classes ) )
}

object DartFlex {

    sealed trait FlexSetting
    case object NoFlex extends FlexSetting
    case class Basis( basis : String ) extends FlexSetting
    case class Shrink( factor : Int ) extends FlexSetting
    case class Grow( factor : Int ) extends FlexSetting

    case class ItemClasses(
        root : Option[ String ] = None,
    )

    case class FlexItem(
        element : VdomNode,
        flex : FlexSetting = NoFlex,
        align : Option[ Alignment ] = None,
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
        justify : Justification = JustifyStart,
        items : Vector[ FlexItem ],
        classes : Classes = Classes(),
    )

}

