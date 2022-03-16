package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access

import com.twosixtech.dart.taxonomy.explorer.api.{RootApiDeps, StateAccessApiDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts.GenericStateAccessComponentLayoutDI

object StateAccessComponentStack {
    trait Base
      extends StateAccessComponentDI
        with StateAccessApiDI {
        this : StateAccessComponentLayoutDeps
          with DartConceptExplorerDI
          with RootApiDeps
          with DartClusterCuratorDI
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }

    trait Generic
      extends Base
        with GenericStateAccessComponentLayoutDI {
        this : DartConceptExplorerDI
          with DartClusterCuratorDI
          with RootApiDeps
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }
}
