package com.twosixtech.dart.scalajs.layout.form.checkbox

import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.{Callback, ReactMouseEvent}

trait CheckBox[ State ] extends ReactComponent[ CheckBox.Props, State ] {

    def apply(
        checked : Boolean,
        onClick : Callback = Callback(),
        onClickEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseDown : Callback = Callback(),
        onMouseDownEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseUp : Callback = Callback(),
        onMouseUpEvent : ReactMouseEvent => Callback = _ => Callback(),
        disabled : Boolean = false,
        size : types.BasicSize = types.Medium,
        checkedColor : types.Color = types.Plain,
        uncheckedColor : types.Color = types.Primary,
        classes : CheckBox.Classes = CheckBox.Classes(),
    ) : Unmounted[ CheckBox.Props, State, BackendType ] =
        apply( CheckBox.Props(
            checked,
            onClick,
            onClickEvent,
            onMouseDown,
            onMouseDownEvent,
            onMouseUp,
            onMouseUpEvent,
            disabled,
            size,
            checkedColor,
            uncheckedColor,
            classes,
        ) )

}

object CheckBox {

    case class Classes(
        root : Option[ String ] = None,
        unchecked : Option[ String ] = None,
        checked : Option[ String ] = None,
        disabled : Option[ String ] = None,
    )

    case class Props(
        checked : Boolean,
        onClick : Callback = Callback(),
        onClickEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseDown : Callback = Callback(),
        onMouseDownEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseUp : Callback = Callback(),
        onMouseUpEvent : ReactMouseEvent => Callback = _ => Callback(),
        disabled : Boolean = false,
        size : types.BasicSize = types.Medium,
        checkedColor : types.Color = types.Plain,
        uncheckedColor : types.Color = types.Primary,
        classes : Classes = Classes(),
    )

}
