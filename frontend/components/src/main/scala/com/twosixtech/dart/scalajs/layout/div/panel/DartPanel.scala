package com.twosixtech.dart.scalajs.layout.div.panel

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait DartPanel[ State ] extends ReactComponent[ DartPanel.Props, State ] {
    def apply(
        element : VdomNode,
        classes : DartPanel.Classes = DartPanel.Classes(),
        fullHeight : Boolean = true,
    ) : Unmounted[ DartPanel.Props, State, BackendType ] =
        apply( DartPanel.Props( element, classes, fullHeight ) )
}

object DartPanel {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        element : VdomNode,
        classes : Classes = Classes(),
        fullHeight : Boolean = true,
    )

}
