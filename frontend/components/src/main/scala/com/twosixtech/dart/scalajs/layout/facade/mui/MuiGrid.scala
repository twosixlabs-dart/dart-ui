package com.twosixtech.dart.scalajs.layout.facade.mui

import japgolly.scalajs.react.component.Js.Component
import japgolly.scalajs.react.{Children, CtorType, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "Grid" )
@js.native
object MuiGridRaw extends js.Object

object MuiGrid {

    @js.native
    trait JsProps extends js.Object {
        var xs : Int = js.native
        var sm : Int = js.native
        var md : Int = js.native
        var lg : Int = js.native
        var xl : Int = js.native
        var container : Boolean = js.native
        var item : Boolean = js.native
        var justify : String = js.native
        var spacing : String = js.native
        var wrap : String = js.native
        var alignContent : String = js.native
        var alignItems : String = js.native
        var component : String = js.native
        var direction : String = js.native
        var key : String = js.native
        var classes : JsClasses = js.native
    }

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    val Facade = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiGridRaw )

}
