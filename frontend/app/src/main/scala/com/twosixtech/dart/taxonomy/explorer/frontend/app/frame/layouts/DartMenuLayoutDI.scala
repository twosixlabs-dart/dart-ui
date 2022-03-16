package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts

import com.twosixtech.dart.scalajs.layout.events.{ClickOffHandler, ClickOffHandlerProps}
import com.twosixtech.dart.scalajs.layout.menu.dartnavbar.dartnavmenu.{DartNavMenu, DartNavMenuTranslation}
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartFrameDI, DartMenuDI, DartMenuLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js

trait DartMenuLayoutDI extends DartMenuLayoutDeps {
    this : DartFrameDI
      with DartComponentDI
      with DartMenuDI
      with DartContextDeps
      with DartRouterDI =>

    override type DartMenuLayoutState = Unit
    override type DartMenuRenderContext = Unit
    override lazy val dartMenuLayout : DartMenuLayout = new DartMenuLayoutBasic

    class DartMenuLayoutBasic extends DartMenuLayout {

        object InnerComponent extends ReactComponent[ DartMenu.LayoutProps, Unit ] {
            override type BackendType = Backend

            class Backend( scope : BackendScope[ DartMenu.LayoutProps, Unit ] ) {
                var ref : html.Element = null

                def updateRef( r : dom.Node ) : Unit = {
                    if ( r != ref && r != null ) {
                        ref = r.asInstanceOf[ html.Element ]
                        scope.forceUpdate
                    }
                }

                def render( props : DartMenu.LayoutProps ) : VdomElement = {
                    import props._

                    ClickOffHandler( ClickOffHandlerProps(
                        _ => closeMenu,
                        DartNavMenu( DartNavMenuTranslation.Props(
                            isOpen,
                            closeMenu,
                            menuItems.map( mi => DartNavMenuTranslation.MenuItem(
                                mi.text.substring( 0, 5 ).replace( ' ', '_' ),
                                mi.text,
                                mi.onClick,
                                isSelected = mi.isSelected,
                            ) ),
                            refFn = Some( updateRef )
                        ) ),
                        Some( ref ),
                    ) )
                }
            }

            val component = ScalaComponent.builder[ DartMenu.LayoutProps ]
              .backend( new Backend( _ ) )
              .renderBackend
              .build

            override def apply( props : DartMenu.LayoutProps ) : Unmounted[ DartMenu.LayoutProps, Unit, Backend ] =
                component( props )


        }

        def bgc( choice1 : DartFrame.AppChoice, choice2 : DartFrame.AppChoice ) : TagMod = {
            if ( choice1 == choice2 ) ^.style.:=( js.Dictionary( "backgroundColor" -> "lightblue" ) )
            else ^.style.:=( js.Dictionary.empty )
        }

        override def render( scope : Scope, state : Unit, props : DartMenu.LayoutProps )
          ( implicit renderProps : Unit, context : DartContext ) : VdomElement = {
            InnerComponent( props )
        }

        override val initialState : Unit = Unit
    }

}
