package com.twosixtech.dart.scalajs.layout.menu.dartnavbar

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "DartNavBar" )
object DartNavBarRaw extends js.Object

object DartNavBar extends AbstractReactFacade( DartNavBarRaw, DartNavBarTranslation.Translation )

object DartNavBarTranslation {
    @js.native
    trait JsProps extends js.Object {
        var menuOpened : Boolean = js.native
        var openMenu : js.Function0[ Unit ] = js.native
        var closeMenu : js.Function0[ Unit ] = js.native
    }

    case class Props(
        menuOpened : Boolean,
        openMenu : Callback,
        closeMenu : Callback,
    ) extends {
        def toJs : JsProps = {
            val jsProps = ( new js.Object ).asInstanceOf[ JsProps ]
            jsProps.menuOpened = menuOpened
            jsProps.openMenu = () => openMenu.runNow()
            jsProps.closeMenu = () => closeMenu.runNow()
            jsProps
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps
        ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }
}
