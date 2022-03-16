package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.taxonomy.explorer.api.ClusteringApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.layouts.wm.WmDartClusterCuratorClusterLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.{DartClusterCuratorClusterDeps, DartClusterCuratorClustorLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts.DartClusterCuratorNavigationLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts.wm.WmDartClusterCuratorFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.DartConceptFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm.WmDartConceptFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.ConceptSearchDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm.{WmConceptSearchDI, WmConceptSearchLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{DartClusterConceptBridgeDeps, DartClusterDI, DartConceptDeps, UUIDTaxonomyIdDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, WmDartSerializationDI}

object DartClusterStack {


    trait Base
      extends DartClusterCuratorDI
        with DartClusterCuratorFrameDI
        with DartClusterCuratorNavigationDI
        with DartClusterCuratorClusterDeps {

        this : DartConceptDeps
          with DartStateDI
          with DartConceptExplorerDI
          with DartContextDeps
          with ConceptSearchDeps
          with DartClusterDI
          with DartConceptFrameDI
          with ClusteringApiDI
          with DartSerializationDeps
          with DartClusterConceptBridgeDeps
          with DartClusterCuratorFrameLayoutDeps
          with DartClusterCuratorNavigationLayoutDeps
          with DartClusterCuratorClustorLayoutDeps
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>

    }

    trait Wm
      extends Base
        with WmDartClusterCuratorFrameLayoutDI
        with DartClusterCuratorNavigationLayoutDI
        with WmDartClusterCuratorClusterLayoutDI
        with WmDartClusterConceptBridgeDI {
        this : WmDartConceptDI
          with DartConceptExplorerDI
          with WmConceptSearchDI
          with WmConceptSearchLayoutDI
          with DartConceptFrameDI
          with ClusteringApiDI
          with WmDartSerializationDI
          with WmDartConceptFrameLayoutDI
          with WmDartClusterConceptBridgeDI
          with UUIDTaxonomyIdDI
          with DartApp.WmDependencies
          with DartApp.TestableDependencies =>
    }

}
