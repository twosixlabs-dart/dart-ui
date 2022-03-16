package com.twosixtech.dart.scalajs.layout.div.header

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.vdom.VdomNode

trait DartHeader[ State ] extends ReactComponent[ DartHeader.Props, State ]

object DartHeader {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        content : Either[ String, VdomNode ],
        small : Boolean = false,
        classes : Classes = Classes(),
    )

}
