package com.twosixtech.dart.scalajs.layout.facade.mui

import japgolly.scalajs.react.{ReactEventFromInput, ReactKeyboardEvent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/core", "TextField" )
@js.native
object MuiTextFieldRaw extends js.Object

object MuiTextField {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsInputClasses extends js.Object {
        var input : String = js.native
    }

    @js.native
    trait JsInputProps extends js.Object {
        var classes : JsInputClasses = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var autoFocus : Boolean = js.native
        var classes : JsClasses = js.native
        var color : String = js.native
        var defaultValue : String = js.native
        var disabled : Boolean = js.native
        var error : Boolean = js.native
        var fullWidth : Boolean = js.native
        var helperText : String = js.native
        var id : String = js.native
        var inputRef : js.Object = js.native
        var margin : String = js.native
        var multiline : Boolean = js.native
        var name : String = js.native
        var onBlur : js.Function1[ ReactEventFromInput, Unit ] = js.native
        var onChange : js.Function1[ ReactEventFromInput, Unit ] = js.native
        var placeholder : String = js.native
        var required : Boolean = js.native
        var rows : Int = js.native
        var rowsMax : Int = js.native
        var size : String = js.native
        var value : String = js.native
        var variant : String = js.native
        var InputProps : JsInputProps = js.native
        var onKeyDown : js.Function1[ ReactKeyboardEvent, Unit ] = js.native
    }

}
