package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartConceptChildrenDI {
    this : DartComponentDI
      with DartConceptChildrenLayoutDeps
      with DartContextDeps
      with DartConceptDeps
      with DartStateDI
      with DartCircuitDeps
      with DartConceptExplorerDI
      with DartTaxonomyDI
      with TaxonomyIdDeps =>

    lazy val dartConceptChildren : DartConceptChildren = new DartConceptChildren

    class DartConceptChildren
      extends SimpleDartComponent[ DartConceptChildren.Props, DartConceptChildrenRenderContext ] {
        override def render( props : DartConceptChildren.Props )(
            implicit renderProps : DartConceptChildrenRenderContext,
            context : DartContext ) : VdomElement = {

            import DartConceptChildren._
            import DartConceptExplorer.{AddConcept, RemoveConcept, ChooseConcept}
            import props._

            val thisConcept = taxonomyEntry.concept

            def addConcept( name : String ) : Callback = {
                val fixedName = fixConceptName( name )
                if ( fixedName.isEmpty ) Callback()
                else context.dispatch(
                    AddConcept( DartConcept( fixedName, Set.empty ), Some( taxonomyEntry.id ) )
                )
            }

            def removeConcept( name : String ) : Callback = {
                val childPath = taxonomyEntry.path :+ name
                val childId : TaxonomyId = context.coreState.conceptState.taxonomy.pathEntry( childPath ).get.id
                context.dispatch( RemoveConcept( childId ) )
            }

            def navigateToChild( name : String ) : Callback = {
                val childPath = taxonomyEntry.path :+ name
                val childId : TaxonomyId = context.coreState.conceptState.taxonomy.pathEntry( childPath ).get.id
                props.navigateToConcept( childId )
            }

            dartConceptChildrenLayout( LayoutProps(
                concepts = thisConcept.children.map( _.name ),
                addConcept = addConcept,
                removeConcept = removeConcept,
                navigateToChild = navigateToChild,
            ).toDartProps )
        }
    }

    object DartConceptChildren {
        case class Props(
            taxonomyEntry : DartTaxonomyEntry,
            navigateToConcept : TaxonomyId => Callback,
        )
        case class LayoutProps(
            concepts : Set[ String ],
            addConcept : String => Callback,
            removeConcept : String => Callback,
            navigateToChild : String => Callback,
        )
    }
}

trait DartConceptChildrenLayoutDeps {
    this : DartConceptChildrenDI
      with DartComponentDI =>

    type DartConceptChildrenRenderContext
    type DartConceptChildrenLayoutState

    val dartConceptChildrenLayout : DartConceptChildrenLayout

    trait DartConceptChildrenLayout
      extends DartLayoutComponent[
        DartConceptChildren.LayoutProps,
        DartConceptChildrenRenderContext,
        DartConceptChildrenLayoutState
      ]
}