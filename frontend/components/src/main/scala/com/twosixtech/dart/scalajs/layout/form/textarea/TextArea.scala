package com.twosixtech.dart.scalajs.layout.form.textarea

import com.twosixtech.dart.scalajs.layout.types.{BasicSize, Medium, Primary, ThemeColor}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.{Callback, ReactKeyboardEvent}

trait TextArea[ State ] extends ReactComponent[ TextArea.Props, State ]

object TextArea {
    sealed trait Variant
    case object Standard extends Variant
    case object Filled extends Variant
    case object Outlined extends Variant

    case class Props(
        value : Option[ String ] = None,
        onChange : Option[ String => Callback ] = None,
        onEnter : Option[ Callback ] = None,
        onEscape : Option[ Callback ] = None,
        onBlur : Option[ Callback ] = None,
        onKeyDown : Option[ ReactKeyboardEvent => Callback ] = None,
        numLines : Option[ Int ] = None,
        maxNumLines : Option[ Int ] = None,
        variant : Variant = Standard,
        placeholder : Option[ String ] = None,
        disabled : Boolean = false,
        autoFocus : Boolean = false,
        color : ThemeColor = Primary,
        size : BasicSize = Medium,
        fullWidth : Boolean = true,
    )
}
