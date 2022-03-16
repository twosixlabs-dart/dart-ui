package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer

import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.{DartClusterCuratorDI, DartClusterStack}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.DartConceptMetadataViewDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.{DartConceptFrameDI, DartConceptStack}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.layouts.wm.WmDartConceptExplorerFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.{DartConceptExplorerFrameDI, DartConceptExplorerFrameLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.{ConceptSearchDeps, ConceptSearchStack}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.{StateAccessComponentLayoutDeps, StateAccessComponentStack}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.layouts.{GenericDartConceptBranchLayoutDI, GenericDartConceptTreeLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.{DartConceptBranchDI, DartConceptBranchLayoutDeps, DartConceptTreeDI, DartConceptTreeLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentStack

object DartConceptExplorerStack {

    trait Base
      extends DartConceptExplorerDI
        with DartConceptExplorerFrameDI
        with DartConceptTreeDI
        with DartConceptBranchDI
        with StateAccessComponentStack.Generic
        with TenantOntologyComponentStack.Generic
        with DartConceptMetadataViewDeps {
        this : DartClusterCuratorDI
          with DartConceptExplorerFrameLayoutDeps
          with DartConceptTreeLayoutDeps
          with DartConceptBranchLayoutDeps
          with StateAccessComponentLayoutDeps
          with ConceptSearchDeps
          with DartConceptFrameDI
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }

    trait Wm
      extends Base
        with WmDartConceptExplorerFrameLayoutDI
        with GenericDartConceptTreeLayoutDI
        with DartConceptBranchDI
        with GenericDartConceptBranchLayoutDI
        with DartConceptStack.Wm
        with DartClusterStack.Wm
        with ConceptSearchStack.Wm {
        this : DartApp.WmDependencies
          with DartApp.TestableDependencies =>
    }

}
