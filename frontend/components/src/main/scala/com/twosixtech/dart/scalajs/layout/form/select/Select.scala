package com.twosixtech.dart.scalajs.layout.form.select

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait Select[ ValType, State ] extends ReactComponent[ Select.Props[ ValType ], State ] {
    def apply(
        value : ValType,
        items : Vector[ Select.Item[ ValType ] ],
        onChange : ValType => Callback,
        disabled : Boolean = false,
        style : Select.Style = Select.Standard,
        classes : Select.Classes = Select.Classes(),
    ) : Unmounted[ Select.Props[ ValType ], State, BackendType ] = {
        apply( Select.Props( value, items, onChange, disabled, style, classes ) )
    }
}

object Select {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class ItemClasses(
        root : Option[ String ] = None,
    )

    case class Item[ ValType ](
        element : VdomNode,
        value : ValType,
        key : Option[ String ] = None,
        classes : ItemClasses = ItemClasses(),
    )

    sealed trait Style
    case object Filled extends Style
    case object Outlined extends Style
    case object Standard extends Style

    case class Props[ ValType ](
        value : ValType,
        items : Vector[ Item[ ValType ] ],
        onChange : ValType => Callback,
        disabled : Boolean = false,
        style : Style = Standard,
        classes : Classes = Classes(),
    )

}
