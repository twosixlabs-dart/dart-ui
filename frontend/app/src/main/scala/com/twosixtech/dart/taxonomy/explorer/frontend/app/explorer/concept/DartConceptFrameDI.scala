package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.DartConceptMetadataViewDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI, Taxonomy}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartConceptFrameDI {
    this : DartComponentDI
      with DartConceptExplorerDI
      with DartConceptFrameLayoutDeps
      with DartStateDI
      with DartCircuitDeps
      with DartTaxonomyDI
      with DartContextDeps
      with DartConceptDeps
      with DartConceptNameDI
      with DartConceptParentDI
      with DartConceptChildrenDI
      with DartConceptMetadataViewDeps =>

    lazy val dartConceptFrame : DartConceptFrame = new DartConceptFrame

    class DartConceptFrame
      extends ViewedDartComponent[ DartConceptFrame.Props, DartConceptFrameRenderContext, DartTaxonomy ] {

        import DartConceptExplorer._

        override def stateView( coreState : CoreState ) : DartTaxonomy = coreState.conceptState.taxonomy

        override def render( props : DartConceptFrame.Props, taxonomy : DartTaxonomy )(
            implicit renderContext : DartConceptFrameRenderContext,
            stateContext : DartContext ) : VdomElement = {

            import DartConceptFrame._

            val chosenConcept = props.taxonomyEntry.concept

            def searchTaxonomy( name : String ) : Set[ DartTaxonomyEntry ] = {
                taxonomy
                  .entries
                  .filter( _._2.concept.name.contains( name ) )
                  .values
                  .toSet
            }

            def updateMetadata( metadata : ConceptMetadataType ) : Callback = {
                stateContext.dispatch( UpdateConcept( props.taxonomyEntry.id, chosenConcept.copy( metadata = metadata ) ) )
            }

            dartConceptFrameLayout( LayoutProps(
                DartConceptName.Props( props.taxonomyEntry, searchTaxonomy ),
                DartConceptParent.Props(
                    props.taxonomyEntry,
                    taxonomy.parentEntry( props.taxonomyEntry.id ),
                    searchTaxonomy,
                    props.navigateToConcept,
                ),
                DartConceptChildren.Props( props.taxonomyEntry, props.navigateToConcept ),
                DartConceptMetadataView.Props( props.taxonomyEntry.concept.metadata, updateMetadata  ),
            ).toDartProps )
        }
    }

    object DartConceptFrame {
        case class Props(
            taxonomyEntry : DartTaxonomyEntry,
            navigateToConcept : TaxonomyId => Callback,
        )

        case class LayoutProps(
            nameProps : DartConceptName.Props,
            parentProps : DartConceptParent.Props,
            childrenProps : DartConceptChildren.Props,
            metadataProps : DartConceptMetadataView.Props,
        )
    }
}

trait DartConceptFrameLayoutDeps {
    this : DartConceptFrameDI with DartComponentDI =>

    type DartConceptFrameRenderContext
    type DartConceptFrameLayoutState

    val dartConceptFrameLayout : DartConceptFrameLayout

    trait DartConceptFrameLayout
      extends DartLayoutComponent[
        DartConceptFrame.LayoutProps,
        DartConceptFrameRenderContext,
        DartConceptFrameLayoutState
      ]
}