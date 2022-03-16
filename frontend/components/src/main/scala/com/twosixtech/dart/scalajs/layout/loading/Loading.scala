package com.twosixtech.dart.scalajs.layout.loading

import com.twosixtech.dart.scalajs.layout.loading.Loading.Classes
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted

trait Loading[ State ] extends ReactComponent[ Loading.Props, State ] {
    def apply(
        complete : Option[ Float ] = None,
        overlay : Boolean = false,
        color : types.Color = types.Plain,
        size : types.BasicSize = types.Medium,
        classes : Classes = Classes(),
    ) : Unmounted[ Loading.Props, State, BackendType ] =
        apply( Loading.Props( complete, overlay, color, size, classes ) )
}

object Loading {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        complete : Option[ Float ] = None,
        overlay : Boolean = false,
        color : types.Color = types.Plain,
        size : types.BasicSize = types.Medium,
        classes : Classes = Classes(),
    )

}
