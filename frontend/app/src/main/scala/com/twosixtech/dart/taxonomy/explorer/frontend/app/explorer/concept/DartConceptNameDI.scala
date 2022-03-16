package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartConceptNameDI {
    this : DartComponentDI
      with DartConceptNameLayoutDeps
      with DartContextDeps
      with DartConceptDeps
      with DartTaxonomyDI
      with DartConceptExplorerDI
      with DartCircuitDeps =>

    lazy val dartConceptName : DartConceptName = new DartConceptName

    class DartConceptName
      extends SimpleDartComponent[ DartConceptName.Props, DartConceptNameRenderContext ] {
        override def render( props : DartConceptName.Props )(
            implicit renderProps : DartConceptNameRenderContext,
            context : DartContext ) : VdomElement = {

            import props._
            import DartConceptName._
            import DartConceptExplorer.{UpdateConcept, ChooseConcept}

            def updateName( newName : String ) : Callback = {
                val fixedName = fixConceptName( newName )
                if ( fixedName.isEmpty ) Callback()
                else {
                    val updatedConcept = taxonomyEntry.concept.copy( name = fixedName )
                    context.dispatch( UpdateConcept( taxonomyEntry.id, updatedConcept ) )
                }
            }
            def searchNames( name : String ) : Set[ String ] = searchTaxonomy( name ).map( _.concept.name )

            dartConceptNameLayout( LayoutProps(
                taxonomyEntry.concept.name,
                updateName,
                searchNames,
            ).toDartProps )
        }
    }

    object DartConceptName {
        case class Props(
            taxonomyEntry : DartTaxonomyEntry,
            searchTaxonomy : String => Set[ DartTaxonomyEntry ]
        )
        case class LayoutProps(
            name : String,
            updateName : String => Callback,
            searchNames : String => Set[ String ]
        )
    }
}

trait DartConceptNameLayoutDeps {
    this : DartConceptNameDI with DartComponentDI =>

    type DartConceptNameRenderContext
    type DartConceptNameLayoutState

    val dartConceptNameLayout : DartConceptNameLayout

    trait DartConceptNameLayout
      extends DartLayoutComponent[
        DartConceptName.LayoutProps,
        DartConceptNameRenderContext,
        DartConceptNameLayoutState
      ]
}