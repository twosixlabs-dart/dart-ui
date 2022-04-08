package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame

import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.MouseEvent

trait DartMenuDI {
    this : DartMenuLayoutDeps
      with DartFrameDI
      with DartComponentDI
      with DartContextDeps
      with DartRouterDI
      with DartStateDI
      with DartCircuitDeps
      with DartMenuBarDI =>

    lazy val dartMenu : DartMenu = new DartMenu

    class DartMenu extends SimpleDartComponent[ DartMenu.Props, DartMenuRenderContext ] {
        override def render( props : DartMenu.Props )(
            implicit renderProps : DartMenuRenderContext,
            context : DartContext ) : VdomElement = {

            dartMenuLayout( DartMenu.LayoutProps(
                props.isOpen,
                context.dispatch( DartMenuBar.CloseDartMenu ),
                Vector(
                    DartMenu.MenuItem(
                        "Document Upload",
                        context.route( DartRouter.ForkliftRoute ),
                        isSelected = props.appChoice == DartFrame.Forklift
                    ),
                    DartMenu.MenuItem(
                        "Corpus Exploration",
                        context.route( DartRouter.CorpexRoute ),
                        isSelected = props.appChoice == DartFrame.Corpex
                    ),
                    DartMenu.MenuItem(
                        "Concepts Explorer",
                        context.route( DartRouter.ConceptExplorerRoute ),
                        isSelected = props.appChoice == DartFrame.ConceptExplorer,
                    ),
                    DartMenu.MenuItem(
                        "Manage Tenants",
                        context.route( DartRouter.TenantsRoute ),
                        isSelected = props.appChoice == DartFrame.Tenants,
                    ),
                )
            ).toDartProps )
        }
    }

    object DartMenu {
        case class Props(
            isOpen : Boolean,
            appChoice : DartFrame.AppChoice
        )

        case class State()

        case class MenuItem(
            text : String,
            onClick : Callback,
            isSelected : Boolean,
        )

        case class LayoutProps(
            isOpen : Boolean,
            closeMenu : Callback,
            menuItems : Vector[ MenuItem ],
        )
    }

}

trait DartMenuLayoutDeps { this : DartMenuDI with DartComponentDI =>

    type DartMenuRenderContext
    type DartMenuLayoutState
    val dartMenuLayout : DartMenuLayout

    trait DartMenuLayout
      extends DartLayoutComponent[ DartMenu.LayoutProps, DartMenuRenderContext, DartMenuLayoutState ]

}

