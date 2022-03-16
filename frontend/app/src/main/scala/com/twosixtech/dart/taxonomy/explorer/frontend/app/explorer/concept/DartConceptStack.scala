package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept

import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm.WmDartConceptFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.{DartConceptChildrenLayoutDI, DartConceptNameLayoutDI, DartConceptParentLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.DartConceptMetadataViewDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.wm.{WmDartConceptMetadataViewDI, WmDartConceptMetadataViewLayoutDI}

object DartConceptStack {

    trait Base
      extends DartConceptFrameDI
        with DartConceptNameDI
        with DartConceptParentDI
        with DartConceptChildrenDI {
        this : DartConceptExplorerDI
          with DartConceptMetadataViewDeps
          with DartConceptFrameLayoutDeps
          with DartConceptNameLayoutDeps
          with DartConceptParentLayoutDeps
          with DartConceptChildrenLayoutDeps
          with DartApp.BaseDependencies
          with DartApp.TestableDependencies =>
    }

    trait Wm
      extends Base
        with WmDartConceptFrameLayoutDI
        with DartConceptParentLayoutDI
        with DartConceptNameLayoutDI
        with DartConceptChildrenLayoutDI
        with WmDartConceptMetadataViewDI
        with WmDartConceptMetadataViewLayoutDI {
        this : DartConceptExplorerDI
          with DartApp.WmDependencies
          with DartApp.TestableDependencies =>
    }
}
