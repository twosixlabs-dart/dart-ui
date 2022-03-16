package com.twosixtech.dart.scalajs.layout.facade.mui

import com.twosixtech.dart.scalajs.react.AbstractReactFacade
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "Paper" )
@js.native
object MuiPaperRaw extends js.Object

object MuiPaper {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var classes : JsClasses = js.native
        var component : String = js.native
        var elevation : Int = js.native
        var square : Boolean = js.native
        var variant : String = js.native
    }

    val Facade = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiPaperRaw )

}

