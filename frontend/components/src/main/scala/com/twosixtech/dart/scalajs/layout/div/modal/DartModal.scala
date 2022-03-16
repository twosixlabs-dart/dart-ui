package com.twosixtech.dart.scalajs.layout.div.modal

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait DartModal[ State ] extends ReactComponent[ DartModal.Props, State ]  {
    import com.twosixtech.dart.scalajs.layout.div.modal.DartModal.{Classes, Medium, Size, Overlay, TransparentBlocking}

    def apply(
        element : VdomNode,
        open : Boolean,
        overlay : Overlay = TransparentBlocking,
        size : Size = Medium,
        moveable : Boolean = false,
        resizable : Boolean = false,
        classes : Classes = Classes(),
    ) : Unmounted[ DartModal.Props, State, BackendType ] =
        apply( DartModal.Props( element, open, overlay, size, moveable, resizable, classes ) )
}

object DartModal {

    sealed trait Location
    case object AutoLocate extends Location

    sealed trait Size
    case object Small extends Size
    case object Medium extends Size
    case object Large extends Size

    case class Classes(
        root : Option[ String ] = None,
    )

    sealed trait Overlay
    case object NoOverlay extends Overlay
    case object TransparentBlocking extends Overlay
    case object GreyBlocking extends Overlay
    case object GreyNonBlocking extends Overlay

    case class Props(
        element : VdomNode,
        open : Boolean,
        overlay : Overlay = TransparentBlocking,
        size : Size = Medium,
        moveable : Boolean = false,
        resizable : Boolean = false,
        classes : Classes = Classes(),
    )

}
