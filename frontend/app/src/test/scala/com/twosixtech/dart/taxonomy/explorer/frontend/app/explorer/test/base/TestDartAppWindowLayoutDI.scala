package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context.TestDartContextDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartAppWindowDI, DartAppWindowLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait TestDartAppWindowLayoutDI
  extends DartAppWindowLayoutDeps {
    this : DartAppWindowDI
      with DartComponentDI
      with DartContextDeps =>

    // DartAppWindowRenderContext = Unit
    type DartAppWindowLayoutState = Unit
    lazy val dartAppWindowLayout : DartAppWindowLayout = new DartAppWindowLayout {
        override def render(
            scope: Scope, state: SnapshotType,
            props: DartAppWindow.LayoutProps
        )(
            implicit renderProps: SnapshotType,
            context: DartContext
        ): VdomElement = <.div()

        override val initialState: SnapshotType = ()
    }
}
