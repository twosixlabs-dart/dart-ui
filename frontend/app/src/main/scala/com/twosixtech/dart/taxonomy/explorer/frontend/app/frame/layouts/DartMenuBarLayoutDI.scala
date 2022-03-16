package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts

import com.twosixtech.dart.scalajs.layout.menu.dartnavbar.{DartNavBar, DartNavBarTranslation}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartMenuBarDI, DartMenuBarLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.CallbackTo
import japgolly.scalajs.react.vdom.VdomElement

trait DartMenuBarLayoutDI extends DartMenuBarLayoutDeps {
    this : DartComponentDI
      with DartMenuBarDI
      with DartContextDeps =>

    override type DartMenuBarRenderContext = Unit
    override type DartMenuBarLayoutState = Unit
    override lazy val dartMenuBarLayout : DartMenuBarLayout[ Unit, Unit ] = new DartMenuBarLayoutBasic

    class DartMenuBarLayoutBasic extends DartMenuBarLayout[ Unit, Unit ] {

        override protected def shouldComponentUpdate(
            props : DartMenuBar.LayoutProps, nextProps : DartMenuBar.LayoutProps, state : SnapshotType, nextState : SnapshotType,
            nextContext : DartContext )(
            implicit
            context : DartContext ) : CallbackTo[ Boolean ] = {
            CallbackTo( props.menuIsOpen != nextProps.menuIsOpen )
        }

        override def render( scope : Scope, state : Unit, props : DartMenuBar.LayoutProps )
          ( implicit renderProps : SnapshotType, context : DartContext ) : VdomElement = {

            import props._

            DartNavBar( DartNavBarTranslation.Props(
                menuIsOpen,
                openMenu,
                closeMenu,
            ) )

        }

        override val initialState : SnapshotType = ()
    }

}
