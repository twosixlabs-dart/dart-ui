package com.twosixtech.dart.scalajs.layout.button.filebutton

import com.twosixtech.dart.scalajs.layout.types.{BasicSize, Medium, Primary, ThemeColor}
import com.twosixtech.dart.scalajs.react.{ReactComponent, StoJ}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.{Callback, ReactEventFromInput}

import scala.scalajs.js

trait FileButton[ State ] extends ReactComponent[ FileButton.Props, State ]

object FileButton {
    case class Classes(
        root : Option[ String ] = None,
    )

    sealed trait Style
    case object Solid extends Style
    case object Outlined extends Style
    case object Text extends Style

    case class Props(
        element : VdomNode,
        onChange : ReactEventFromInput => Callback,
        style : Style = Solid,
        color : ThemeColor = Primary,
        disabled : Boolean = false,
        size : BasicSize = Medium,
        classes : Classes = Classes(),
    )
}
