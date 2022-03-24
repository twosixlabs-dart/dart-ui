package com.twosixtech.dart.taxonomy.explorer.frontend

import com.twosixtech.dart.scalajs.dom.DomUtils
import com.twosixtech.dart.taxonomy.explorer.api.{ ClusteringApiDI, RootApiDeps, UserDataApiDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.GenericDartRootDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.wm.WmDartAppWindowLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{ DartAppWindowDI, DartFrameDI, DartFrameStack, DartMenuBarDI, DartMenuDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.{ DartBackendDeps, KeycloakXhrDartBackendDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.{ DartCircuitDeps, GenericDartCircuitDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.js.JsDartContextProviderDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.{ DartTenantsContextDeps, GenericDartTenantContextDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.{ DartContextDeps, GenericDartContextDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.{ DartRouterDI, DartRouterDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ DartComponentDI, DartRootDeps, DartStateDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.{ DartConfigDeps, GenericDartConfigDI, RootApiDI }
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{ CuratedClusterDI, DartClusterConceptBridgeDeps, DartClusterDI, DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps, TaxonomyIdSerializationDeps, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI }
import com.twosixtech.dart.taxonomy.explorer.serialization.{ DartSerializationDeps, WmDartSerializationDI }


object DartApp {

    /////////////////////////////////////////////////////////////////
    //                                                             //
    //                         DEPENDENCIES                        //
    //                                                             //
    /////////////////////////////////////////////////////////////////

    trait TestableDependencies
      extends GenericDartCircuitDI
        with DartConfigDeps
        with DartRouterDI
        with DartTenantsContextDeps
        with DartContextDeps
        with DartBackendDeps {
        this : DartComponentDI
          with DartRootDeps
          with ErrorHandlerDI
          with DartStateDI
          with StateAccessComponentDI
          with TenantOntologyComponentDI
          with DartClusterCuratorDI
          with DartConceptExplorerDI
          with DartFrameDI
          with DartAppWindowDI
          with DartMenuDI
          with DartMenuBarDI
          with DartClusterDI
          with DartLoadingDI
          with DartConceptDeps
          with DartTaxonomyDI =>
    }

    // Implemented testable dependencies to be mixed into to app
    trait AppTestableDependencies
      extends TestableDependencies
        with GenericDartConfigDI
        with GenericDartCircuitDI
        with DartRouterDI
        with GenericDartTenantContextDI
        with GenericDartContextDI
        with JsDartContextProviderDI
        with KeycloakXhrDartBackendDI {
        this : DartComponentDI
          with UserDataApiDI
          with DartRootDeps
          with ErrorHandlerDI
          with DartStateDI
          with StateAccessComponentDI
          with TenantOntologyComponentDI
          with DartClusterCuratorDI
          with DartConceptExplorerDI
          with DartFrameDI
          with DartAppWindowDI
          with DartMenuDI
          with DartMenuBarDI
          with DartClusterDI
          with DartLoadingDI
          with DartConceptDeps
          with DartTaxonomyDI =>
    }

    trait BaseDependencies
        // Implemented dependencies
        extends DartStateDI
          with GenericDartRootDI
          with DartComponentDI
          with CuratedClusterDI
          with DartTaxonomyDI
          with DartClusterDI
          with ErrorHandlerDI
          with DartLoadingDI
          with DartLoadingInterfaceDI
          with DartLoadingInterfaceLayoutDI
          with RootApiDI
          with UserDataApiDI
          with ClusteringApiDI
          // Abstract dependencies
          with DartConceptDeps
          with DartSerializationDeps
          with TaxonomyIdDeps
          with TaxonomyIdSerializationDeps {

        // Required self-types
        this : TestableDependencies
          with DartConceptExplorerDI
          with StateAccessComponentDI
          with TenantOntologyComponentDI
          with DartFrameDI
          with DartClusterCuratorDI
          with DartMenuDI
          with DartMenuBarDI
          with DartAppWindowDI
          with DartClusterConceptBridgeDeps
        =>

    }

    trait WmDependencies
      extends BaseDependencies
        with WmDartClusterConceptBridgeDI
        with WmDartConceptDI
        with WmDartSerializationDI
        with UUIDTaxonomyIdDI
        with UUIDTaxonomyIdSerializationDI  {
        this : TestableDependencies
          with DartConceptExplorerDI
          with StateAccessComponentDI
          with TenantOntologyComponentDI
          with DartFrameDI
          with DartClusterCuratorDI
          with DartMenuDI
          with DartMenuBarDI
          with DartAppWindowDI
        =>
    }


    /////////////////////////////////////////////////////////////////
    //                                                             //
    //                             APP                             //
    //                                                             //
    /////////////////////////////////////////////////////////////////

    trait DartApp {
        this : BaseDependencies
          with TestableDependencies =>

        def run( target : String = "app" ) : Unit = {
            val appDiv = DomUtils.targetDiv( target )
            appDiv.setAttribute( "style", "height:100%;" )
            DartContextBuilder.AppComponent( dartRoot.apply ).renderIntoDOM( appDiv )
        }
    }

    trait Wm
      extends DartFrameStack.Wm
        with WmDependencies
        with DartApp
        with AppTestableDependencies
          // app window needs to be set explicitly because
          // it needs to be overridable for tests (app window brings in
          // raw-js components that we don't want mixed in for all
          // tests
        with WmDartAppWindowLayoutDI

}



