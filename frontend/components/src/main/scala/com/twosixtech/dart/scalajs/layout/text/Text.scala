package com.twosixtech.dart.scalajs.layout.text

import com.twosixtech.dart.scalajs.layout.text.Text.Classes
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.layout.types.{Color, Medium, Size}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.{Callback, ReactMouseEvent}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait Text[ State ] extends ReactComponent[ Text.Props, State ] {
    def apply(
        element : VdomNode,
        size : Size = Medium,
        color : Option[ Color ] = None,
        onClick : Option[ Callback ] = None,
        onClickEvent : Option[ ReactMouseEvent => Callback ] = None,
        onMouseDown : Option[ Callback ] = None,
        onMouseDownEvent : Option[ ReactMouseEvent => Callback ] = None,
        onMouseUp : Option[ Callback ] = None,
        onMouseUpEvent : Option[ ReactMouseEvent => Callback ] = None,
        classes : Classes = Classes(),
    ) : Unmounted[ Text.Props, State, BackendType ] =
        apply( Text.Props(
            element,
            size,
            color,
            onClick,
            onClickEvent,
            onMouseDown,
            onMouseDownEvent,
            onMouseUp,
            onMouseUpEvent,
            classes,
        ) )
}

object Text {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        element : VdomNode,
        size : Size = Medium,
        color : Option[ Color ] = None,
        onClick : Option[ Callback ] = None,
        onClickEvent : Option[ ReactMouseEvent => Callback ] = None,
        onMouseDown : Option[ Callback ] = None,
        onMouseDownEvent : Option[ ReactMouseEvent => Callback ] = None,
        onMouseUp : Option[ Callback ] = None,
        onMouseUpEvent : Option[ ReactMouseEvent => Callback ] = None,
        classes : Classes = Classes(),
    )

}
