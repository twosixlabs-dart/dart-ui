package com.twosixtech.dart.scalajs.layout.button.iconbutton

import com.twosixtech.dart.scalajs.layout.types.{BasicSize, Color, Medium, Primary, Size}
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

trait IconButton[ State ] extends ReactComponent[ IconButton.Props, State ]

object IconButton {

    case class Classes(
        root : Option[ String ] = None,
    )

    sealed trait Style
    case object Solid extends Style
    case object Outlined extends Style
    case object Text extends Style

    case class Props(
        icon : VdomNode,
        onClick : Callback,
        ariaLabel : Option[ String ] = None,
        style : Style = Solid,
        color : Color = Primary,
        disabled : Boolean = false,
        size : BasicSize = Medium,
        classes : Classes = Classes(),
    )

}
