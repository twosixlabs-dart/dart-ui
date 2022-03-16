package com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit

import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.{ErrorHandlerDI, LogQueue}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{DartAppWindowDI, DartFrameDI, DartMenuBarDI, DartMenuDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}

import scala.collection.immutable.SortedSet

trait GenericDartCircuitDI
  extends DartCircuitDeps {
    this : DartConfigDeps
      with DartStateDI
      with DartFrameDI
      with DartMenuBarDI
      with DartAppWindowDI
      with DartMenuDI
      with DartTaxonomyDI
      with DartConceptExplorerDI
      with ErrorHandlerDI
      with DartLoadingDI
      with DartConceptDeps
      with DartContextDeps
      with DartClusterCuratorDI
      with StateAccessComponentDI
      with TenantOntologyComponentDI =>

    trait GenericDartCircuitContext extends DartCircuitContext {

        override lazy val initState : DartState = DartState(
            CoreState(),
            LayoutState(),
        )

        override lazy val coreHandlers : Seq[ CoreHandler[ _ ] ] = {
            DartMenuBar.dartMenuHandler +:
            DartConceptExplorer.dartConceptHandler +:
            DartConceptExplorer.conceptExplorerLoadingHandler +:
            DartClusterCurator.clusterCuratorLoadingHandler +:
            DartClusterCurator.clusterHandler +:
            StateAccessComponent.stateAccessHandler +:
            TenantOntologyComponent.tenantOntologyHandler +:
            ErrorHandler.errorHandler +:
            DartLoading.loadingHandler( DartContextBuilder.globalLoaderId, _.zoomTo( _.loadingState ) ) +:
            Nil
        }

        override lazy val layoutHandlers : Seq[ LayoutHandler[ _ ] ] = Nil

    }

    override val DartCircuitContext : DartCircuitContext = new GenericDartCircuitContext {}

}
