package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test

import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.GenericDartRootDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.TestDartAppWindowLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.backend.MockedBackendDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.configuration.DartTestConfigDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context.TestDartContextDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.router.StubbedRouterDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartAppWindowDI, DartFrameDI, DartFrameStack, DartMenuBarDI, DartMenuDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartRootDeps, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.{DartBackendDeps, KeycloakXhrDartBackendDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.{DartCircuitDeps, GenericDartCircuitDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.{DartRouterDI, DartRouterDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.{DartConfigDeps, RootApiDI}
import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterConceptBridgeDeps, DartClusterDI, DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps, TaxonomyIdSerializationDeps}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps

object TestApp {

    trait AppStack
      extends DartFrameStack.Wm
        with DartApp.WmDependencies
        with DartApp.TestableDependencies
        with DartTestConfigDI
        with MockedBackendDI
        with StubbedRouterDI
        with TestDartContextDI
        with TestDartAppWindowLayoutDI {

    }

}
