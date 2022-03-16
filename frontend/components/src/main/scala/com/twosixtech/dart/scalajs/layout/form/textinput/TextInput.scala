package com.twosixtech.dart.scalajs.layout.form.textinput

import com.twosixtech.dart.scalajs.layout.types.{BasicSize, Medium, Primary, ThemeColor}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ReactKeyboardEvent}

trait TextInput[ State ] extends ReactComponent[ TextInput.Props, State ]

object TextInput {
    sealed trait Variant
    case object Standard extends Variant
    case object Filled extends Variant
    case object Outlined extends Variant

    case class Classes(
        root : Option[ String ] = None,
        input : Option[ String ] = None,
    )

    case class Props(
        value : Option[ String ] = None,
        onChange : Option[ String => Callback ] = None,
        onEnter : Option[ Callback ] = None,
        onEscape : Option[ Callback ] = None,
        onKeyDown : Option[ ReactKeyboardEvent => Callback ] = None,
        variant : Variant = Standard,
        placeholder : Option[ String ] = None,
        disabled : Boolean = false,
        autoFocus : Boolean = false,
        color : ThemeColor = Primary,
        size : BasicSize = Medium,
        onBlur : Option[ ReactEventFromInput => Callback ] = None,
        fullWidth : Boolean = false,
        classes : Classes = Classes(),
    )
}
