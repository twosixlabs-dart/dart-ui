package com.twosixtech.dart.scalajs.layout.button.regular

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Js
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.{Callback, ReactMouseEvent}
import japgolly.scalajs.react.vdom.VdomNode

import scala.scalajs.js

trait Button[ State ] extends ReactComponent[ Button.Props, State ] {
    def apply(
        element : VdomNode,
        onClick : Callback,
        onClickEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseDown : Callback = Callback(),
        onMouseDownEvent : ReactMouseEvent => Callback = _ => Callback(),
        style : Button.Style = Button.Solid,
        color : Button.Color = Button.Primary,
        disabled : Boolean = false,
        size : Button.Size = Button.Normal,
        id : Option[ String ] = None,
        classes : Button.Classes = Button.Classes(),
    ) : Unmounted[ Button.Props, State, BackendType ] =
        apply( new Button.Props(
            element,
            onClick,
            onClickEvent,
            onMouseDown,
            onMouseDownEvent,
            style,
            color,
            disabled,
            size,
            id,
            classes
        ) )
}

object Button {
    sealed trait Size
    case object Large extends Size
    case object Normal extends Size
    case object Small extends Size

    case class Classes(
        root : Option[ String ] = None,
    )

    sealed trait Style
    case object Solid extends Style
    case object Outlined extends Style
    case object Text extends Style

    sealed trait Color
    case object Primary extends Color
    case object Secondary extends Color
    case object Plain extends Color

    case class Props(
        element : VdomNode,
        onClick : Callback = Callback(),
        onClickEvent : ReactMouseEvent => Callback = _ => Callback(),
        onMouseDown : Callback = Callback(),
        onMouseDownEvent : ReactMouseEvent => Callback = _ => Callback(),
        style : Style = Solid,
        color : Color = Primary,
        disabled : Boolean = false,
        size : Size = Normal,
        id : Option[ String ] = None,
        classes : Classes = Classes(),
    )
}
