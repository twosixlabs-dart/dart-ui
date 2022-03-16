package com.twosixtech.dart.scalajs.layout.menu.tabs

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "DartTabs" )
object DartTabsRaw extends js.Object

object DartTabs extends AbstractReactFacade( DartTabsRaw, DartTabsTranslation.Translation )


object DartTabsTranslation {

    @js.native
    trait JsClasses extends js.Object {
        var root : String = js.native
    }

    @js.native
    trait JsTab extends js.Object {
        var label : String = js.native
        var value : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var onChange : js.Function1[ String, Unit ] = js.native
        var value : String = js.native
        var tabs : js.Array[ JsTab ] = js.native
        var classes : JsClasses = js.native
    }

    case class Classes(
        root : Option[ String ] = None,
    ) {
        def toJs : JsClasses = {
            val jc = ( new js.Object ).asInstanceOf[ JsClasses ]
            root.foreach( jc.root = _ )
            jc
        }
    }

    case class Tab(
        label : String,
        value : String,
    ) {
        def toJs : JsTab = {
            val jt = ( new js.Object ).asInstanceOf[ JsTab ]
            jt.label = label
            jt.value = value
            jt
        }
    }

    case class Props(
        onChange : String => Callback,
        value : String,
        tabs : Vector[ Tab ],
        classes : Classes = Classes(),
    ) extends {
        def toJs : JsProps = {
            val jsProps = ( new js.Object ).asInstanceOf[ JsProps ]
            jsProps.onChange = newValue => onChange( newValue ).runNow()
            jsProps.value = value
            jsProps.tabs = tabs.map( _.toJs ).toJSArray
            jsProps.classes = classes.toJs
            jsProps
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }
}
