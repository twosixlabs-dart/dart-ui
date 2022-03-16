package com.twosixtech.dart.scalajs.layout.form.label

import com.twosixtech.dart.scalajs.layout.form.label.InputLabel.Classes
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}

trait InputLabel[ State ] extends ReactComponent[ InputLabel.Props, State ] {
    def apply(
        label : VdomNode,
        inputElement : VdomElement,
        labelPlacement : types.RelativePlacement = types.AboveMiddle,
        classes : Classes = Classes(),
    ) : Unmounted[ InputLabel.Props, State, BackendType ] =
        apply( InputLabel.Props( label, inputElement, labelPlacement, classes ) )
}

object InputLabel {
    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        label : VdomNode,
        inputElement : VdomElement,
        labelPlacement : types.RelativePlacement = types.AboveMiddle,
        classes : Classes = Classes(),
    )
}
