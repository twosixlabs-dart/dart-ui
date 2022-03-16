package com.twosixtech.dart.scalajs.layout.facade.mui

import japgolly.scalajs.react.component.Js.{RawMounted, UnmountedWithRawType}
import japgolly.scalajs.react.{Children, JsComponent, ReactEventFrom, ReactEventFromInput}
import japgolly.scalajs.react.raw.React.{Element, Node}
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.vdom.VdomNode
import org.scalajs.dom.html

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "RadioGroup" )
@js.native
object MuiRadioGroupRaw extends js.Object

object MuiRadioGroup {

    @js.native
    trait JsProps extends js.Object {
        var className : String = js.native
        var defaultValue : String = js.native
        var name : String = js.native
        var onChange : js.Function1[ SyntheticEvent[ html.Input ], Unit ] = js.native
        var value : String = js.native
    }

    val component = JsComponent[ JsProps, Children.Varargs, js.Object ]( MuiRadioGroupRaw )

    def apply( props : JsProps )( children : VdomNode* ) : UnmountedWithRawType[ JsProps, js.Object, RawMounted[JsProps, js.Object ] ] =
        component( props )( children : _* )
}

@JSImport( "@material-ui/core", "FormControlLabel" )
@js.native
object MuiFormControlLabelRaw extends js.Object

object MuiFormControlLabel {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
        var label : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var checked : Boolean = js.native
        var classes : JsClasses = js.native
        var control : Element = js.native
        var disabled : Boolean = js.native
        var inputRef : js.Object = js.native
        var label : Node = js.native
        var labelPlacement : String = js.native // bottom' | 'end' | 'start' | 'top'
        var onChange : js.Function1[ ReactEventFrom[ html.Element ], Unit ] = js.native
        var value : String = js.native
    }

    val component = JsComponent[ JsProps, Children.None, js.Object ]( MuiFormControlLabelRaw )

    def apply( props : JsProps ) : UnmountedWithRawType[ JsProps, js.Object, RawMounted[JsProps, js.Object ] ] =
        component( props )

}

@JSImport( "@material-ui/core", "Radio" )
@js.native
object MuiRadioRaw extends js.Object

object MuiRadio {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var checked : Boolean = js.native
        var checkedIcon : Node = js.native
        var classes : JsClasses = js.native
        var color : String = js.native // default | primary | secondary
        var disabled : Boolean = js.native
        var disableRipple : Boolean = js.native
        var icon : Node = js.native
        var id : String = js.native
        var name : String = js.native
        var onChange : js.Function1[ html.Element, Unit ] = js.native
        var required : Boolean = js.native
        var size : String = js.native // medium | small
        var value : String = js.native
    }

    val component = JsComponent[ JsProps, Children.None, js.Object ]( MuiRadioRaw )

    def apply( props : JsProps ) : UnmountedWithRawType[ JsProps, js.Object, RawMounted[ JsProps, js.Object ] ] =
        component( props )

}