package com.twosixtech.dart.scalajs.layout.facade.mui

import com.twosixtech.dart.scalajs.layout.facade.mui.MuiSelect.JsProps
import japgolly.scalajs.react.component.Js.{RawMounted, UnmountedWithRawType}
import japgolly.scalajs.react.{Children, JsComponent, ReactEventTypes}
import japgolly.scalajs.react.raw.React.{Element, Node}
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import org.scalajs.dom.html

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "Select" )
@js.native
object MuiSelectRaw extends js.Object

object MuiSelect {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var classes : JsClasses = js.native
        var defaultValue : Any = js.native
        var displayEmpty : Boolean = js.native
        var disabled : Boolean = js.native
        var iconComponent : Element = js.native
        var id : String = js.native
        var label : Node = js.native
        var multiple : Boolean = js.native
        var native : Boolean = js.native
        var onChange : js.Function1[ SyntheticEvent[ html.Select ], Unit ] = js.native
        var onClose : js.Function1[ SyntheticEvent[ html.Select ], Unit ] = js.native
        var onOpen : js.Function1[ SyntheticEvent[ html.Select ], Unit ] = js.native
        var open : Boolean = js.native
        var renderValue : js.Function1[ Any, Node ] = js.native
        var value : Any = js.native
        var variant : String = js.native
    }

    val component = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiSelectRaw )

    def apply( props : JsProps )( children : VdomNode* ) : UnmountedWithRawType[ JsProps, js.Object, RawMounted[JsProps, js.Object ] ] = {
        component( props )( children : _* )
    }

}

@JSImport( "@material-ui/core", "MenuItem" )
@js.native
object MuiMenuItemRaw extends js.Object

object MuiMenuItem {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var classes : JsClasses = js.native
        var value : Any = js.native
        var key : String = js.native
    }

    val component = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiMenuItemRaw )

    def apply( props : JsProps )( children : VdomNode* ) : UnmountedWithRawType[ JsProps, js.Object, RawMounted[JsProps, js.Object ] ] = {
        component( props )( children : _* )
    }

}