package com.twosixtech.dart.scalajs.layout.facade.mui

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@js.native
@JSImport( "@material-ui/core/CircularProgress", JSImport.Default )
object MuiCircularProgressRaw extends js.Object

//@js.native
//@JSImport( "@material-ui/core/CircularProgressWithLabel", JSImport.Default )
//object MuiCircularProgressWithLabelRaw extends js.Object

@js.native
@JSImport( "@material-ui/core/CircularProgress", JSImport.Default )
object MuiLinearProgressRaw extends js.Object

//@js.native
//@JSImport( "@material-ui/core/LinearProgressWithLabel", JSImport.Default )
//object MuiLinearProgressWithLabelRaw extends js.Object

object MuiProgress {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var variant : String = js.native // determinate, indeterminate (default), static (circle only), buffer (linear only), query (linear only)
        var color : String = js.native // inherit (circle only), primary (default), secondary
        var size : Float | String = js.native // pixel size, or css size of circle
        var thickness : Float = js.native // thickness of circle
        var value : Float = js.native // only for determinate variant
        var valueBuffer : Float = js.native // only for linear progres
        var classes : JsClasses = js.native
    }

}
