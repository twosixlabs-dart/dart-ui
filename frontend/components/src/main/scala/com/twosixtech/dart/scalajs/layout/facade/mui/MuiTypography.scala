package com.twosixtech.dart.scalajs.layout.facade.mui

import japgolly.scalajs.react.{Children, JsComponent, ReactMouseEvent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "Typography" )
@js.native
object MuiTypographyRaw extends js.Object

object MuiTypography {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var align : String = js.native
        var classes : JsClasses = js.native
        var color : String = js.native
        var display : String = js.native
        var gutterBottom : Boolean = js.native
        var noWrap : Boolean = js.native
        var variant : String = js.native
        var onClick : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var onMouseDown : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var onMouseUp : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var component : String = js.native
    }

    val Facade = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiTypographyRaw )
}
