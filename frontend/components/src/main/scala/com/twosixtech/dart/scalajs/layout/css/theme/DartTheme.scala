package com.twosixtech.dart.scalajs.layout.css.theme

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.vdom.VdomNode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "DartTheme" )
object DartThemeRaw extends js.Object

object DartTheme extends AbstractReactFacade( DartThemeRaw, DartThemeTranslation )

object DartThemeTranslation extends StoJ[ VdomNode, js.Object ] {
    override def scalaToJsBuilder( props : VdomNode, jsProps : js.Object ) : (js.Object, VdomNode) = {
        (new js.Object, props)
    }
}
