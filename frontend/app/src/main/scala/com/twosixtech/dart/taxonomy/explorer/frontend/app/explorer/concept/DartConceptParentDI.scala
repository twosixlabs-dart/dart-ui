package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartConceptParentDI {
    this : DartComponentDI
      with DartConceptParentLayoutDeps
      with DartContextDeps
      with DartConceptDeps
      with DartTaxonomyDI
      with DartCircuitDeps
      with DartConceptExplorerDI =>

    lazy val dartConceptParent : DartConceptParent = new DartConceptParent

    class DartConceptParent
      extends SimpleDartComponent[ DartConceptParent.Props, DartConceptParentRenderContext ] {
        override def render( props : DartConceptParent.Props )(
            implicit renderProps : DartConceptParentRenderContext,
            context : DartContext ) : VdomElement = {

            import DartConceptParent._
            import DartConceptExplorer.{MoveConcept, ChooseConcept}
            import props._

            val parentName = parentTaxonomyEntry.map( _.concept.name )

            def chooseNewParent( newParent : Option[ TaxonomyId ] ) : Callback = {
                context.dispatch( MoveConcept( taxonomyEntry.id, newParent ) )
            }

            def searchTaxonomyProp( parentName : String ) : Set[ (String, Callback) ] = {
                import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPathFormat
                searchTaxonomy( parentName )
                  .filter( v => {
                      val path = taxonomyEntry.path
                      v.path.take( path.length ) != path && v.path != path.dropRight( 1 )
                  } )
                  .map( v => (v.path.pathString, chooseNewParent( Some( v.id ) ) ) )
            }

            val navigateToParent = parentTaxonomyEntry match {
                case None => Callback()
                case Some( entry ) => props.navigateToConcept( entry.id )
            }

            dartConceptParentLayout( LayoutProps(
                parentName,
                searchTaxonomyProp,
                chooseNewParent( None ),
                navigateToParent,
            ).toDartProps )
        }
    }

    object DartConceptParent {
        case class Props(
            taxonomyEntry : DartTaxonomyEntry,
            parentTaxonomyEntry : Option[ DartTaxonomyEntry ],
            searchTaxonomy : String => Set[ DartTaxonomyEntry ],
            navigateToConcept : TaxonomyId => Callback,
        )
        case class LayoutProps(
            name : Option[ String ],
            searchParents : String => Set[ (String, Callback) ],
            removeParent : Callback,
            navigateToParent : Callback,
        )
    }
}


trait DartConceptParentLayoutDeps {
    this : DartConceptParentDI with DartComponentDI =>

    type DartConceptParentRenderContext
    type DartConceptParentLayoutState

    val dartConceptParentLayout : DartConceptParentLayout

    trait DartConceptParentLayout
      extends DartLayoutComponent[
        DartConceptParent.LayoutProps,
        DartConceptParentRenderContext,
        DartConceptParentLayoutState
      ]
}