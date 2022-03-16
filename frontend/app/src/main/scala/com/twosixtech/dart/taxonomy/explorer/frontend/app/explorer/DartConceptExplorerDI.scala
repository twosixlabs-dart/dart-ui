package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer

import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps}
import japgolly.scalajs.react.Callback

import java.util.UUID
import scala.annotation.tailrec

trait DartConceptExplorerDI {
    this : DartConceptDeps
      with DartCircuitDeps
      with DartStateDI
      with DartTaxonomyDI
      with TaxonomyIdDeps
      with DartLoadingDI
      with DartClusterCuratorDI
      with StateAccessComponentDI
      with TenantOntologyComponentDI =>

    object DartConceptExplorer {

        case class State(
            taxonomy : DartTaxonomy = DartTaxonomy( Set.empty ),
            conceptChoice : Option[ TaxonomyId ] = None,
            cluster : DartClusterCurator.State = DartClusterCurator.State(),
            loadingState : DartLoading.State = DartLoading.State(),
            stateAccessState : StateAccessComponent.State = StateAccessComponent.State(),
            tenantOntologyState : TenantOntologyComponent.State = TenantOntologyComponent.State(),
        )

        def printConcept( indent : Int, concept : DartConcept ) : Unit = {
            val indentString = "  " * indent
            println( indentString + concept.name )
            concept.children.foreach( printConcept( indent + 1, _ ) )
        }

        trait DartConceptAction extends CoreAction

        case class LoadTaxonomy( taxonomy : DartTaxonomy ) extends DartConceptAction
        case class AddConceptToChoice( concept : DartConcept, newId : Option[ TaxonomyId ] = None ) extends DartConceptAction
        case class AddConcept( concept : DartConcept, to : Option[ TaxonomyId ] = None, newId : Option[ TaxonomyId ] = None ) extends DartConceptAction
        case object RemoveChoice extends DartConceptAction
        case class RemoveConcept( concept : TaxonomyId ) extends DartConceptAction
        case class MoveChoice( toParent : TaxonomyId ) extends DartConceptAction
        case class MoveConcept( concept : TaxonomyId, newParent : Option[ TaxonomyId ] ) extends DartConceptAction
        case class ChooseConcept( concept : TaxonomyId ) extends DartConceptAction
        case object ChooseParent extends DartConceptAction
        case object ClearConceptChoice extends DartConceptAction
        case class UpdateChoice( updated : DartConcept ) extends DartConceptAction
        case class UpdateConcept( concept : TaxonomyId, updated : DartConcept ) extends DartConceptAction

        def updateTaxonomy( updater : DartTaxonomy => DartTaxonomy, newChoice : Option[ TaxonomyId ] )( implicit prevState : State ) : State = {
            val newTaxonomy = updater( prevState.taxonomy )
            prevState.copy( taxonomy = newTaxonomy, conceptChoice = newChoice )
        }

        def updateTaxonomy( updater : DartTaxonomy => DartTaxonomy )( implicit prevState : State ) : State = {
            updateTaxonomy( updater, prevState.conceptChoice )
        }

        @tailrec
        def tryToPreservePath(
            newTaxonomy : DartTaxonomy,
            oldPath : ConceptPath,
        ) : Option[ TaxonomyId ] = {
            oldPath.toList match {
                case Nil => None
                case nonEmpty =>
                    newTaxonomy.pathEntry( oldPath ) match {
                        case Some( entry ) => Some( entry.id )
                        case None => tryToPreservePath( newTaxonomy, nonEmpty.dropRight( 1 ) )
                    }
            }
        }

        def tryToPreserveChoice(
            oldTaxonomy : DartTaxonomy,
            newTaxonomy : DartTaxonomy,
            oldChoice : Option[ TaxonomyId ],
        ) : Option[ TaxonomyId ] = oldChoice flatMap { oldId =>
            newTaxonomy.idEntry( oldId ) match {
                case None =>
                    oldTaxonomy.idEntry( oldId ).flatMap( oldEntry => {
                        val oldPath = oldEntry.path
                        tryToPreservePath( newTaxonomy, oldPath )
//                        newTaxonomy.pathEntry( oldPath ).map( _.id )
                    } )
                case Some( newEntry ) =>
                    Some( newEntry.id )
            }
        }

        val dartConceptHandler : CoreHandler[ State ] = DartCircuitContext.coreHandler[ State ]( _.zoomTo( _.conceptState ) ) {
            implicit prevState : State => {
                val taxonomy = prevState.taxonomy
                val choice = prevState.conceptChoice
                def idPath( taxonomyId: TaxonomyId ) : ConceptPath =
                    taxonomy.idEntry( taxonomyId ) match {
                        case None => Nil
                        case Some( entry ) => entry.path
                    }
                val choicePath = choice.map( idPath ).toList.flatten

                // Return a partial function
                {
                    case LoadTaxonomy( taxonomy ) =>
                        prevState.copy(
                            taxonomy = taxonomy,
                            conceptChoice = tryToPreserveChoice( prevState.taxonomy, taxonomy, prevState.conceptChoice ),
                        )
                    case AddConceptToChoice( concept, id ) => updateTaxonomy( _.addConcept( concept, choicePath, id ).get )
                    case AddConcept( concept, at, id ) =>
                        updateTaxonomy( _.addConcept( concept, at.map( idPath ).toList.flatten, id ).get )
                    case RemoveChoice => updateTaxonomy( _.removeConcept( choicePath ).get )
                    case RemoveConcept( concept ) =>
                        val conceptParentOpt =
                            taxonomy
                              .idEntry( concept )
                              .flatMap( e =>
                                  taxonomy
                                    .pathEntry( e.path.dropRight( 1 ) )
                                    .map( _.id )
                              )
                        updateTaxonomy( _.removeConcept( idPath( concept ) ).get, conceptParentOpt )
                    case MoveChoice( toParent ) => updateTaxonomy( _.moveConcept( choicePath, idPath( toParent ) ).get )
                    case MoveConcept( from, toParent ) =>
                        updateTaxonomy( _.moveConcept( idPath( from ), toParent.map( idPath ).toList.flatten ).get )
                    case ChooseConcept( concept ) => updateTaxonomy( v => v, Some( concept ) )
                    case ChooseParent => updateTaxonomy( v => v, taxonomy.pathEntry( choicePath.dropRight( 1 ) ).map( _.id ) )
                    case ClearConceptChoice => updateTaxonomy( v => v, None )
                    case UpdateChoice( updated ) => updateTaxonomy( _.updateConceptAt( choicePath, updated ).get )
                    case UpdateConcept( concept, updated ) => updateTaxonomy( _.updateConceptAt( idPath( concept ), updated ).get )
                }
            }
        }

        // Generate handler and loader for handling loading for concept explorer
        val loadingComponentId : UUID = UUID.randomUUID()

        val conceptExplorerLoadingHandler = DartLoading.loadingHandler( loadingComponentId, _.zoomTo( _.conceptState.loadingState ) )

        def loader( dispatcher : DartAction => Callback ) : DartLoading.Loader = new DartLoading.Loader( loadingComponentId, dispatcher )
    }

}
