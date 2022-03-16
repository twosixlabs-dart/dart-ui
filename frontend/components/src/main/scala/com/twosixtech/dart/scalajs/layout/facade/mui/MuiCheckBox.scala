package com.twosixtech.dart.scalajs.layout.facade.mui

import japgolly.scalajs.react.{Children, JsComponent, ReactMouseEvent}
import japgolly.scalajs.react.raw.React.Node
import japgolly.scalajs.react.raw.SyntheticEvent
import org.scalajs.dom.html.Input

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "Checkbox" )
@js.native
object MuiCheckBoxRaw extends js.Object

object MuiCheckBox {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
        var checked : String = js.native
        var disabled : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var checked : Boolean = js.native
        var classes : JsClasses = js.native
        var color : String = js.native // 'default', 'primary', or 'secondary'
        var disabled : Boolean = js.native
        var disableRipple : Boolean = js.native
        var icon : Node = js.native // icon when unchecked
        var id : String = js.native // id of input element
        var indeterminate : Boolean = js.native
        var indeterminateIcon : Node = js.native
        var inputProps : js.Object = js.native
        var inputRef : js.Object = js.native
        var onChange : js.Function1[ SyntheticEvent[ Input ], Unit ] = js.native
        var required : Boolean = js.native
        var size : String = js.native // 'medium' or 'small'
        var value : String = js.native
        var onClick : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var onMouseDown : js.Function1[ ReactMouseEvent, Unit ] = js.native
        var onMouseUp : js.Function1[ ReactMouseEvent, Unit ] = js.native
    }

    val Facade = JsComponent[ JsProps, Children.None, js.Object ]( MuiCheckBoxRaw )

}
