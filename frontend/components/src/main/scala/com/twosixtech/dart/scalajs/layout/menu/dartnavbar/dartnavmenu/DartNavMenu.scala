package com.twosixtech.dart.scalajs.layout.menu.dartnavbar.dartnavmenu

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "DartMenu" )
object DartNavMenuRaw extends js.Object

object DartNavMenu extends AbstractReactFacade( DartNavMenuRaw, DartNavMenuTranslation.Translation )

object DartNavMenuTranslation {

    @js.native
    trait JsMenuItem extends js.Object {
        var key : String = js.native
        var text : String = js.native
        var onClick : js.Function0[ Unit ] = js.native
        var isSelected : Boolean = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var menuOpened : Boolean = js.native
        var closeMenu : js.Function0[ Unit ] = js.native
        var menuItems : js.Array[ JsMenuItem ] = js.native
        var refFn : js.Function1[ dom.Node, Unit ] = js.native
    }

    case class MenuItem(
        key : String,
        text : String,
        onClick : Callback,
        isSelected : Boolean,
    )

    case class Props(
        menuOpened : Boolean,
        closeMenu : Callback,
        menuItems : Vector[ MenuItem ],
        refFn: Option[ Function1[ dom.Node, Unit ] ] = None,
    ) extends  {
        def toJs : JsProps = {
            val jp = ( new js.Object ).asInstanceOf[ JsProps ]
            jp.menuOpened = menuOpened
            jp.closeMenu = () => closeMenu.runNow()
            jp.menuItems = menuItems.map( mi => {
                val jsMi = ( new js.Object ).asInstanceOf[ JsMenuItem ]
                jsMi.key = mi.key
                jsMi.text = mi.text
                jsMi.onClick = () => mi.onClick.runNow()
                jsMi.isSelected = mi.isSelected
                jsMi
            } ).toJSArray
            refFn.foreach( ( fn : Function1[ dom.Node, Unit ] ) => jp.refFn = fn : js.Function1[ dom.Node, Unit ] )
            jp
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }
}
