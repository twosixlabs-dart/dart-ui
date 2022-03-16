package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.wm

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.DartConceptMetadataViewDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.WmDartConceptDI
import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Polarity, Positive, SemanticType, WmConceptMetadata}
import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._


trait WmDartConceptMetadataViewDI
  extends DartConceptMetadataViewDeps {
    this : DartComponentDI
      with DartCircuitDeps
      with DartContextDeps
      with WmDartConceptDI
      with DartConceptExplorerDI
      with WmDartConceptMetadataViewLayoutDeps =>

    lazy val wmDartConceptMetadataView : WmDartConceptMetadataView = new WmDartConceptMetadataView

    class WmDartConceptMetadataView extends DartConceptMetadataView {
        import WmDartConceptMetadataView._
        import DartConceptExplorer._

        override def render( props : DartConceptMetadataView.Props )(
            implicit renderProps : DartConceptMetadataViewRenderContext,
            context : DartContext ) : VdomElement = {

            val blankMetadata = WmConceptMetadata(
                Set.empty,
                Set.empty,
                Seq.empty,
                Seq.empty,
                Positive,
                Entity,
            )

            def metadataUpdater( fn : WmConceptMetadata => WmConceptMetadata ) : Callback = {
                val newMeta = if ( props.metadata.isEmpty ) fn( blankMetadata ) else fn( props.metadata.get )
                if ( newMeta.examples.isEmpty && newMeta.patterns.isEmpty ) props.updateMetadata( None )
                else props.updateMetadata( Some( newMeta ) )
            }

            val updatePatterns : Set[ String ] => Callback =
                v => metadataUpdater( _.copy( patterns = v ) )

            val updateExamples : Set[ String ] => Callback =
                v => metadataUpdater( _.copy( examples = v ) )

            val updateDescriptions : Seq[ String ] => Callback =
                v => {
                    metadataUpdater( _.copy( descriptions = v ) )
                }

            val updatePolarity : Polarity => Callback =
                v => metadataUpdater( _.copy( polarity = v ) )

            val updateSemanticType : SemanticType => Callback =
                v => metadataUpdater( _.copy( semanticType = v ) )

            props.metadata match {
                case None =>
                    wmDartConceptMetadataViewLayout(
                        LayoutProps(
                            isDefined = false,
                            Set.empty,
                            updatePatterns,
                            Set.empty,
                            updateExamples,
                            Seq.empty,
                            updateDescriptions,
                            Positive,
                            updatePolarity,
                            Entity,
                            updateSemanticType,
                        ).toDartProps
                    )
                case Some( metadata ) =>
                    wmDartConceptMetadataViewLayout(
                        LayoutProps(
                            isDefined = true,
                            metadata.patterns,
                            updatePatterns,
                            metadata.examples,
                            updateExamples,
                            metadata.descriptions,
                            updateDescriptions,
                            metadata.polarity,
                            updatePolarity,
                            metadata.semanticType,
                            updateSemanticType,
                        ).toDartProps
                    )
            }

        }
    }

    object WmDartConceptMetadataView {

        case class LayoutProps(
            isDefined : Boolean,
            patterns : Set[ String ],
            updatePatterns : Set[ String ] => Callback,
            examples : Set[ String ],
            updateExamples : Set[ String ] => Callback,
            descriptions : Seq[ String ],
            updateDescriptions : Seq[ String ] => Callback,
            polarity : Polarity,
            updatePolarity : Polarity => Callback,
            semanticType : SemanticType,
            updateSemanticType : SemanticType => Callback,
        )

    }

}

trait WmDartConceptMetadataViewLayoutDeps
  extends WmDartConceptMetadataViewDI {
    this : DartComponentDI
      with DartContextDeps
      with DartConceptExplorerDI
      with DartCircuitDeps
      with WmDartConceptDI =>

    val wmDartConceptMetadataViewLayout : WmDartConceptMetadataViewLayout

    type WmDartConceptMetadataViewLayoutState

    trait WmDartConceptMetadataViewLayout
      extends DartLayoutComponent[ WmDartConceptMetadataView.LayoutProps, DartConceptMetadataViewRenderContext, WmDartConceptMetadataViewLayoutState ]

}
