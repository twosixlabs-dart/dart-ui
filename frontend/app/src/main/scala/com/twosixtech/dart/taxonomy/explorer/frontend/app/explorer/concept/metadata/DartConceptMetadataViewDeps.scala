package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata

import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps
import japgolly.scalajs.react.Callback

trait DartConceptMetadataViewDeps {
    this : DartComponentDI
      with DartContextDeps
      with DartConceptDeps =>

    type DartConceptMetadataViewRenderContext

    trait DartConceptMetadataView
      extends SimpleDartComponent[ DartConceptMetadataView.Props, DartConceptMetadataViewRenderContext ]

    object DartConceptMetadataView {
        case class Props(
            metadata : ConceptMetadataType,
            updateMetadata : ConceptMetadataType => Callback,
        )
    }
}
