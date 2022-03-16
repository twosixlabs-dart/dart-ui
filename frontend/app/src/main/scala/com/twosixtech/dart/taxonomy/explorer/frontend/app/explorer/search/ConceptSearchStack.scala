package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm.{WmConceptSearchDI, WmConceptSearchLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, WmDartConceptDI}

object ConceptSearchStack {

    trait Wm
      extends WmConceptSearchLayoutDI
        with WmConceptSearchDI {
        this : DartComponentDI
          with WmDartConceptDI
          with DartTaxonomyDI
          with DartContextDeps =>

    }

}
