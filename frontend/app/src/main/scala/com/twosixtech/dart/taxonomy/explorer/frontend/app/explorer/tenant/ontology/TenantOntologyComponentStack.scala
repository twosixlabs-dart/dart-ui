package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology

import com.twosixtech.dart.taxonomy.explorer.api.OntologyPublicationApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.layouts.GenericTenantOntologyComponentLayoutDI

object TenantOntologyComponentStack {
    trait Base
      extends TenantOntologyComponentDI
        with OntologyPublicationApiDI {
        this : TenantOntologyComponentLayoutDeps
          with DartConceptExplorerDI
          with StateAccessComponentDI
          with DartClusterCuratorDI
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }

    trait Generic
      extends Base
        with GenericTenantOntologyComponentLayoutDI {
        this : DartConceptExplorerDI
          with StateAccessComponentDI
          with DartClusterCuratorDI
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }
}
