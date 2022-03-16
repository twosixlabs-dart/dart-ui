package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.collection.immutable

trait DartConceptBranchDI {
    this : DartComponentDI
      with DartConceptBranchLayoutDeps
      with DartContextDeps
      with DartConceptDeps
      with DartConceptExplorerDI
      with DartCircuitDeps
      with DartTaxonomyDI
      with TaxonomyIdDeps =>

    lazy val dartConceptBranch : DartConceptBranch = new DartConceptBranch

    class DartConceptBranch
      extends SimpleDartComponent[ DartConceptBranch.Props, DartConceptBranchRenderContext ] {
        override def render( props : DartConceptBranch.Props )(
            implicit renderProps : DartConceptBranchRenderContext,
            context : DartContext ) : VdomElement = {

            import DartConceptBranch._
            import DartConceptExplorer.{ChooseConcept, RemoveConcept, AddConcept}

            val conceptsData : Set[ DartConceptBranch.ConceptLayoutData ] =
                props.concepts.toSet.map( ( conceptTup : (TaxonomyId, DartConcept) ) => {
                    val (id, concept) = conceptTup

                    ConceptLayoutData(
                        name = concept.name,
                        select = context.dispatch( ChooseConcept( id ) ),
                        remove = context.dispatch( RemoveConcept( id ) ),
                        isSelected = props.choice.contains( id ),
                    )
                } )

            def addConcept( name : String ) : Callback = {
                val fixedName = fixConceptName( name )
                if ( fixedName.isEmpty ) Callback()
                else context.dispatch( AddConcept( DartConcept( fixedName, Set.empty ), props.parent ) )
            }

            dartConceptBranchLayout( LayoutProps(
                concepts = conceptsData,
                addConcept = addConcept,
            ).toDartProps )
        }
    }

    object DartConceptBranch {
        case class Props(
            concepts : Map[ TaxonomyId, DartConcept ],
            parent : Option[ TaxonomyId ],
            choice : Option[ TaxonomyId ],
            terminal : Boolean,
        )

        case class ConceptLayoutData(
            name : String,
            select : Callback,
            remove : Callback,
            isSelected : Boolean,
        )

        case class LayoutProps(
            concepts : Set[ ConceptLayoutData ],
            addConcept : String => Callback,
        )
    }
}

trait DartConceptBranchLayoutDeps { this : DartConceptBranchDI with DartComponentDI =>
    type DartConceptBranchRenderContext
    type DartConceptBranchLayoutState
    val dartConceptBranchLayout : DartConceptBranchLayout

    trait DartConceptBranchLayout
      extends DartLayoutComponent[
        DartConceptBranch.LayoutProps,
        DartConceptBranchRenderContext,
        DartConceptBranchLayoutState
      ]
}