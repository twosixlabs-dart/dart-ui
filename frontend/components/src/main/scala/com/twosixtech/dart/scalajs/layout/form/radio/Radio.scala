package com.twosixtech.dart.scalajs.layout.form.radio

import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait Radio[ State ] extends ReactComponent[ Radio.Props, State ] {
    def apply(
        value : String,
        items : Vector[ Radio.Item ],
        onChange : String => Callback,
        color : types.Color = types.Primary,
        horizontal : Boolean = false,
        disabled : Boolean = false,
        classes : Radio.Classes = Radio.Classes(),
    ) : Unmounted[ Radio.Props, State, BackendType ] = apply( Radio.Props( value, items, onChange, color, horizontal, disabled, classes ) )
}

object Radio {

    case class ItemClasses(
        root : Option[ String ] = None,
        label : Option[ String ] = None,
        button : Option[ String ] = None,
    )

    case class Classes(
        root : Option[ String ] = None,
        items : ItemClasses = ItemClasses(),
    )

    case class Item(
        value : String,
        label : VdomNode,
        disabled : Boolean = false,
        color : Option[ types.Color ] = None,
        classes : ItemClasses = ItemClasses(),
    )

    case class Props(
        value : String,
        items : Vector[ Item ],
        onChange : String => Callback,
        color : types.Color = types.Primary,
        horizontal : Boolean = false,
        disabled : Boolean = false,
        classes : Classes = Classes(),
    )

}
