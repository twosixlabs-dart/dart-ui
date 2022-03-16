package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame

import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.{ErrorInterfaceDI, ErrorInterfaceLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.layouts.ErrorInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerStack
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.wm.{WmDartAppWindowLayoutDI, WmDartFrameLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.{DartMenuBarLayoutDI, DartMenuLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentStack
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentStack
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDI
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.GenericDartConfigDI

object DartFrameStack {

    trait Base
      extends DartFrameDI
        with DartAppWindowDI
        with DartMenuDI
        with DartMenuBarDI
        with ErrorInterfaceDI
        with ErrorInterfaceLayoutDI
        with DartLoadingInterfaceDI
        with DartLoadingInterfaceLayoutDI {
        this : DartApp.BaseDependencies
          with DartApp.TestableDependencies
          with DartFrameLayoutDeps
          with DartMenuLayoutDeps
          with DartMenuBarLayoutDeps
          with DartAppWindowLayoutDeps
        =>
    }

    trait Wm
      extends Base
        with WmDartFrameLayoutDI
        with DartMenuBarLayoutDI
        with DartMenuLayoutDI
        with DartConceptExplorerStack.Wm
        with StateAccessComponentStack.Generic
        with TenantOntologyComponentStack.Generic {
        this : DartApp.WmDependencies
          with DartApp.TestableDependencies
          with DartAppWindowLayoutDeps
        =>
    }
}
