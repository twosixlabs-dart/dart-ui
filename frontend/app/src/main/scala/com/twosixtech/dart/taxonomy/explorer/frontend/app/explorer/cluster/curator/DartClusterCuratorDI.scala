package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, CuratedClusterDI, DartClusterConceptBridgeDeps, DartClusterDI, DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps}
import japgolly.scalajs.react.Callback
import org.scalajs.dom.window

import java.util.UUID
import scala.annotation.tailrec
import scala.collection.immutable.ListMap

trait DartClusterCuratorDI {
    this : DartCircuitDeps
      with DartComponentDI
      with CuratedClusterDI
      with DartConceptDeps
      with DartTaxonomyDI
      with DartClusterDI
      with DartStateDI
      with DartLoadingDI
      with DartClusterConceptBridgeDeps
      with DartConceptExplorerDI =>

    object DartClusterCurator {

        val CLUSTERS_BRANCH : ConceptPath = Seq( "clusters" )

        case class Props( )

        sealed trait ClusterState

        case object NoClusterState extends ClusterState

        case object InitialClusterPending extends ClusterState

        sealed trait ClusterStateWithResults extends ClusterState { self =>
            val clusters : Vector[ CuratedCluster ]
            val activeCluster : Int

            def copyCSWR(
                clusters : Vector[ CuratedCluster ] = self.clusters,
                activeCluster : Int = self.activeCluster,
            ) : ClusterStateWithResults
        }

        case class ClusterComplete(
            override val clusters : Vector[ CuratedCluster ],
            jobId : Option[ UUID ] = None,
            viewedConcept : Option[ TaxonomyId ],
            override val activeCluster : Int = 0,
        ) extends ClusterStateWithResults {
            override def copyCSWR(
                clusters: Vector[ CuratedCluster ], activeCluster: Int
            ) : ClusterComplete = copy( clusters = clusters, activeCluster = activeCluster )
        }

        case class ReclusterPending(
            override val clusters : Vector[ CuratedCluster ],
            jobId : UUID,
            override val activeCluster : Int = 0,
        ) extends ClusterStateWithResults {
            override def copyCSWR(
                clusters: Vector[ CuratedCluster ], activeCluster: Int
            ) : ReclusterPending = copy( clusters = clusters, activeCluster = activeCluster )
        }

        case class State(
            clusterState : ClusterState = NoClusterState,
            reclusterable : Boolean = false,
            loadingState : DartLoading.State = DartLoading.State(),
        )

        trait ClusterAction extends CoreAction
        trait ClusterUpdateAction extends ClusterAction

        case class ClusterResults( clusters : Seq[ Cluster ] ) extends ClusterAction
        case class RescoreResults( clusters : Seq[ Cluster ] ) extends ClusterAction
        case class LoadClusterState( clusters : Seq[ CuratedCluster ], jobId : Option[ UUID ], activeCluster : Option[ Int ] = None )
          extends ClusterAction
        case object ClearClusterState extends ClusterAction
        case class ReclusterSubmitted( jobId : UUID ) extends ClusterAction
        case class ViewConcept( target : TaxonomyId ) extends ClusterAction
        case object ClearConceptView extends ClusterAction

        case class SetActiveCluster( newCluster : Int ) extends ClusterAction

        // Update individual cluster
        def AcceptPhrase( index : Int, phrase : String* ) : AcceptPhrases = AcceptPhrases( index, phrase )
        case class AcceptPhrases( index : Int, phrase : Iterable[ String ] ) extends ClusterUpdateAction
        def RejectPhrase( index : Int, phrase : String* ) : RejectPhrases = RejectPhrases( index, phrase )
        case class RejectPhrases( index : Int, phrases : Iterable[ String ] ) extends ClusterUpdateAction
        def RestorePhrase( index : Int, phrase : String* ) : RestorePhrases = RestorePhrases( index, phrase )
        case class RestorePhrases( index : Int, phrase : Iterable[ String ] ) extends ClusterUpdateAction
        case class RejectCluster( index : Int ) extends ClusterUpdateAction
        case class RejectUncurated( index : Int ) extends ClusterUpdateAction
        case class RestoreCluster( index : Int ) extends ClusterUpdateAction
        case class AddDefaultConcept( index : Int ) extends ClusterUpdateAction
        case class TargetConcept( index : Int, target : TaxonomyId ) extends ClusterUpdateAction
        def SelectPhrase( index : Int, phrase : String* ) : SelectPhrases = SelectPhrases( index, phrase )
        case class SelectPhrases( index : Int, phrases : Iterable[ String ] ) extends ClusterUpdateAction
        def DeselectPhrase( index : Int, phrase : String* ) : DeselectPhrases = DeselectPhrases( index, phrase )
        case class DeselectPhrases( index : Int, phrases : Iterable[ String ] ) extends ClusterUpdateAction
        case class SelectAllPhrases( index : Int ) extends ClusterUpdateAction
        case class SelectAcceptedPhrases( index : Int ) extends ClusterUpdateAction
        case class SelectRejectedPhrases( index : Int ) extends ClusterUpdateAction
        case class SelectUncuratedPhrases( index : Int ) extends ClusterUpdateAction
        case class ClearPhraseSelection( index : Int ) extends ClusterUpdateAction
        case class InvertPhraseSelection( index : Int ) extends ClusterUpdateAction
        case class AcceptSelectedPhrases( index : Int ) extends ClusterUpdateAction
        case class RejectSelectedPhrases( index : Int ) extends ClusterUpdateAction
        case class RestoreSelectedPhrases( index : Int ) extends ClusterUpdateAction
        case class SetRecommendedName( index : Int, name : String ) extends ClusterUpdateAction

        import clusterConceptBridge._

        def updateCluster( prevState : DartConceptExplorer.State, index : Int, updater : CuratedCluster => CuratedCluster ) : DartConceptExplorer.State = {
            val newClusterCuratorState : State = getCluster( prevState, index ) match {
                case None => prevState.cluster
                case Some( originalCluster ) =>
                    val updatedCluster = updater( originalCluster )
                    if ( updatedCluster == originalCluster ) prevState.cluster
                    else prevState.cluster.copy(
                        reclusterable =
                            if ( (updatedCluster.acceptedPhrases ++ updatedCluster.acceptedPhrases).nonEmpty ) true
                            else prevState.cluster.reclusterable,
                        clusterState = {
                            prevState.cluster.clusterState match {
                                case cc@ClusterComplete( oldClusters, _, _, _ ) =>
                                    val newClusters = oldClusters.updated( index, updatedCluster )
                                    cc.copy( clusters = newClusters )
                                case rp@ReclusterPending( oldClusters, _, _ ) =>
                                    val newClusters = oldClusters.updated( index, updatedCluster )
                                    rp.copy( clusters = newClusters )
                                case _ => throw new IllegalStateException( "updateCluster called when clusterState is not ClusterStateWithResults" )
                            }
                        },
                    )
            }
            prevState.copy( cluster = newClusterCuratorState )
        }

        def updateTaxonomy( prevState : DartConceptExplorer.State )( update : DartTaxonomy => DartTaxonomy ) : DartConceptExplorer.State = {
            prevState.copy( taxonomy = update( prevState.taxonomy ) )
        }

        def getDefaultConcept( prevState : DartConceptExplorer.State, index : Int ) : Option[ TaxonomyId ] = {
            val tax = prevState.taxonomy
            val cluster = getCluster( prevState, index ).get
            val name = cluster.selectedName.getOrElse( cluster.cluster.recommendedName )
            def getHighestConceptNumber( startingAt : Int ) : (Int, Option[ TaxonomyId ]) = {
                ( if ( startingAt == 0 ) tax.pathEntry( CLUSTERS_BRANCH :+ name )
                else tax.pathEntry( CLUSTERS_BRANCH :+ s"${name}_${startingAt}" ) ) match {
                    case Some( entry ) =>
                        val (nextNumber, nextIdOpt) = getHighestConceptNumber( startingAt + 1 )
                        nextIdOpt match {
                            case None => (startingAt, Some( entry.id ))
                            case _ => (nextNumber, nextIdOpt)
                        }
                    case None => (startingAt, None)
                }
            }
            getHighestConceptNumber( 0 )._2
        }

        def addDefaultConcept( prevState : DartConceptExplorer.State, index : Int ) : (DartConceptExplorer.State, TaxonomyId) = {
            val stateWithClustersBranch = {
                if ( prevState.taxonomy.getConcept( CLUSTERS_BRANCH ).isDefined ) prevState
                else {
                    updateTaxonomy( prevState )( _.addConcept(
                        DartConcept( CLUSTERS_BRANCH.last, Set.empty ),
                        CLUSTERS_BRANCH.dropRight( 1 ) ).getOrElse( prevState.taxonomy ),
                    )
                }
            }

            val tax = stateWithClustersBranch.taxonomy
            val cluster = getCluster( stateWithClustersBranch, index ).get
            val name = cluster.selectedName.getOrElse( cluster.cluster.recommendedName )

            @tailrec
            def getHighestConceptNumber( startingAt : Int ) : (Int, DartConceptExplorer.State, TaxonomyId) = {
                ( if ( startingAt == 0 ) tax.pathEntry( CLUSTERS_BRANCH :+ name )
                  else tax.pathEntry( CLUSTERS_BRANCH :+ s"${name}_${startingAt}" ) ) match {
                    case Some( entry ) if !conceptHasAnyPhrases( entry.concept ) =>
                        (startingAt, stateWithClustersBranch, entry.id)
                    case Some( _ ) => getHighestConceptNumber( startingAt + 1 )
                    case None =>
                        val newId = generateTaxonomyId()
                        val stateWithNewConcept : DartConceptExplorer.State =
                            updateTaxonomy( stateWithClustersBranch ) { prevTaxonomy =>
                                val newName = if ( startingAt == 0 ) name else s"${name}_$startingAt"
                                prevTaxonomy.addConcept( DartConcept( newName, Set.empty ), CLUSTERS_BRANCH, Some( newId ) ).get
                            }
                        (startingAt, stateWithNewConcept, newId)
                }
            }
            val result = getHighestConceptNumber( 0 )
            (result._2, result._3)
        }

        def getOrMakeTarget( prevState : DartConceptExplorer.State, index : Int ) : (DartConceptExplorer.State, TaxonomyId) = {
            getCluster( prevState, index ).get.currentTarget match {
                case Some( target ) => (prevState, target)
                case None =>
                    val (stateWithDefaultTarget, targetId) = addDefaultConcept( prevState, index )
                    val stateWithDefaultTargetSetAsClusterTarget =
                        updateCluster( stateWithDefaultTarget, index, _.copy( currentTarget = Some( targetId ) ) )
                    (stateWithDefaultTargetSetAsClusterTarget, targetId)
            }
        }

        def getCluster( explorerState : DartConceptExplorer.State, index : Int ) : Option[ CuratedCluster ] = {
            explorerState.cluster.clusterState match {
                case cs : ClusterStateWithResults =>
                    cs.clusters.lift( index )
                case _ => None
            }
        }

        def acceptPhrase( prevState : DartConceptExplorer.State, index : Int, phrase : String ) : DartConceptExplorer.State = {
            val cluster = getCluster( prevState, index ).get
            if ( !cluster.cluster.rankedWords.contains( phrase ) ||
                 cluster.acceptedPhrases.keySet.contains( phrase ) ) prevState
            else {
                val (stateWithTarget, target) = getOrMakeTarget( prevState, index )
                val targetEntry = stateWithTarget.taxonomy.idEntry( target ).get
                if ( conceptHasPhrase( targetEntry.concept, phrase ) ) prevState
                else {
                    val restoredState = restorePhrase( stateWithTarget, index, phrase )

                    val stateWithUpdatedCluster = updateCluster(
                        restoredState,
                        index,
                        cl => cl.copy( acceptedPhrases = cl.acceptedPhrases + (phrase -> target) ),
                    )
                    updateTaxonomy( stateWithUpdatedCluster ) { prevTaxonomy =>
                        prevTaxonomy.updateConceptAt(
                            targetEntry.path,
                            acceptPhraseConceptUpdater( phrase )( targetEntry.concept ),
                        ).get
                    }
                }
            }
        }

        def rejectPhrase( prevState : DartConceptExplorer.State, index : Int, phrase : String ) : DartConceptExplorer.State = {
            val cluster = getCluster( prevState, index ).get
            if ( !cluster.cluster.rankedWords.contains( phrase ) ||
                 cluster.rejectedPhrases.keySet.contains( phrase ) ) prevState
            else {
                val restoredState = restorePhrase( prevState, index, phrase )

                val targetEntryOpt = cluster.currentTarget.flatMap( target => {
                    restoredState.taxonomy.idEntry( target ).flatMap( targetEntry => {
                        if ( conceptHasPhrase( targetEntry.concept, phrase ) ) Some( targetEntry )
                        else None
                    } )
                } )

                val stateWithUpdatedConcept = targetEntryOpt match {
                    case None => restoredState
                    case Some( targetEntry ) =>
                        updateTaxonomy( restoredState ) { prevTaxonomy =>
                            prevTaxonomy.updateConceptAt(
                                targetEntry.path,
                                rejectPhraseConceptUpdater( phrase )( targetEntry.concept ),
                            ).get
                        }
                }

                updateCluster(
                    stateWithUpdatedConcept,
                    index,
                    cl => cl.copy( rejectedPhrases = cl.rejectedPhrases + (phrase -> targetEntryOpt.map( _.id ) ) ),
                )
            }
        }

        def restorePhrase( prevState : DartConceptExplorer.State, index : Int, phrase : String ) : DartConceptExplorer.State = {
            val cluster = getCluster( prevState, index ).get
            val restoreConceptFromAcceptState = cluster.acceptedPhrases.find( _._1 == phrase ) match {
                case Some( (_, phraseTarget) ) =>
                    updateTaxonomy( prevState ) { prevTaxonomy =>
                        prevTaxonomy.idEntry( phraseTarget ) match {
                            case Some( targetEntry ) =>
                                prevTaxonomy.updateConceptAt(
                                    targetEntry.path,
                                    undoAcceptPhraseConceptUpdater( phrase )( targetEntry.concept ),
                                ).get
                            case None => prevTaxonomy
                        }
                    }
                case _ => prevState
            }

            val restoreConceptFromRejectionState = cluster.rejectedPhrases.find( _._1 == phrase ) match {
                case Some( (_, Some( phraseTarget ) ) ) =>
                    updateTaxonomy( restoreConceptFromAcceptState ) { prevTaxonomy =>
                        prevTaxonomy.idEntry( phraseTarget ) match {
                            case Some( targetEntry ) =>
                                prevTaxonomy.updateConceptAt(
                                    targetEntry.path,
                                    undoRejectPhraseConceptUpdater( phrase )( targetEntry.concept ),
                                ).get
                            case None => prevTaxonomy
                        }
                    }
                case _ => restoreConceptFromAcceptState
            }

            updateCluster(
                restoreConceptFromRejectionState,
                index,
                cl => cl.copy(
                    acceptedPhrases = cl.acceptedPhrases - phrase,
                    rejectedPhrases = cl.rejectedPhrases - phrase,
                ),
            )
        }

        val clusterHandler : CoreHandler[ DartConceptExplorer.State ] = {

            DartCircuitContext.coreHandler[ DartConceptExplorer.State ]( _.zoomTo( _.conceptState ) ) {
                prevState =>
                    val taxonomy = prevState.taxonomy
                    val cluster = prevState.cluster

                    // Return partial function
                    {
                        case SetActiveCluster( newCluster ) =>
                            prevState.cluster.clusterState match {
                                case cc@ClusterComplete( _, _, _, _ ) =>
                                    prevState.copy( cluster = prevState.cluster.copy( clusterState = cc.copy( activeCluster = newCluster ) ) )
                                case rc@ReclusterPending( _, _, _ ) =>
                                    prevState.copy( cluster = prevState.cluster.copy( clusterState = rc.copy( activeCluster = newCluster ) ) )
                                case _ => throw new IllegalStateException( "clusterHandler called when clusterState is not ClusterStateWithResults" )
                            }

                        case ClearClusterState =>
                            prevState.copy( cluster = prevState.cluster.copy( clusterState = NoClusterState ) )

                        case LoadClusterState( clusters, jobId, activeCluster ) =>
                            prevState.copy(
                                cluster = prevState.cluster.copy(
                                    clusterState = ClusterComplete(
                                        clusters.toVector,
                                        jobId,
                                        None,
                                        activeCluster.getOrElse( 0 ),
                                    ),
                                )
                            )

                        case ClusterResults( clusters : Seq[ Cluster ] ) =>
                            val updatedState = prevState.copy(
                                cluster = cluster.copy(
                                    clusterState = {
                                        ClusterComplete(
                                            clusters = clusters.map( ( c : Cluster ) => {
                                                CuratedCluster(
                                                    cluster = DartCluster.fromCluster( c, taxonomy ),
                                                    acceptedPhrases = Map.empty,
                                                    rejectedPhrases = Map.empty,
                                                    selectedPhrases = Set.empty,
                                                    currentTarget = None,
                                                    selectedName = None,
                                                )
                                            } ).toVector,
                                            jobId = prevState.cluster.clusterState match {
                                                case ReclusterPending( _, id, _ ) => Some( id )
                                                case ClusterComplete( _, Some( id ), _, _ ) => Some( id )
                                                case _ => None
                                            },
                                            viewedConcept = None,
                                        )
                                    }
                                )
                            )

                            updateTaxonomy( updatedState ) { prevTaxonomy =>
                                if ( prevTaxonomy.pathEntry( CLUSTERS_BRANCH ).isEmpty ) {
                                    CLUSTERS_BRANCH.indices.reverse.foldLeft( prevTaxonomy ) { (oldTax, dropVal) =>
                                        val path = CLUSTERS_BRANCH.dropRight( dropVal )
                                        oldTax.getConcept( path ) match {
                                            case None => oldTax.addConcept( DartConcept( path.last, Set.empty ), path.dropRight( 1 ) ).get
                                            case Some( _ ) => oldTax
                                        }
                                    }
                                } else prevTaxonomy
                            }

                        case RescoreResults( clusters : Seq[ Cluster ] ) =>
                            prevState.copy(
                                cluster = cluster.copy(
                                    clusterState = prevState.cluster.clusterState match {
                                        case cs : ClusterStateWithResults =>
                                            val clusterMap = ( cs.clusters map { cl =>
                                                cl.cluster.id -> cl
                                            } ).toMap
                                            val newClusterState = ClusterComplete(
                                                jobId = cs match {
                                                    case ClusterComplete(_, jobId, _, _) => jobId
                                                    case ReclusterPending( _, jobId, _ ) => Some( jobId )
                                                },
                                                viewedConcept = cs match {
                                                    case ClusterComplete(_, _, viewedConcept, _) => viewedConcept
                                                    case ReclusterPending( _, _, _ ) => None
                                                },
                                                activeCluster = cs match {
                                                    case ClusterComplete(_, _, _, activeCluster) => activeCluster
                                                    case ReclusterPending( _, _, activeCluster ) => activeCluster
                                                },
                                                clusters = clusters.toVector flatMap { theirCluster =>
                                                    ( clusterMap.get( theirCluster.id ) map { ourCluster =>
                                                        ourCluster.copy(
                                                            cluster = ourCluster.cluster.copy(
                                                                score = theirCluster.score,
                                                                similarNodes = theirCluster.similarNodes
                                                                  .flatMap(
                                                                      v => DartNodeSimilarity.fromNodeSimilarity(
                                                                          v, taxonomy
                                                                      )
                                                                  ),
                                                            )
                                                        )
                                                    } ).toVector
                                                }
                                            )
                                            newClusterState
                                        case v => v
                                    }
                                )
                            )

                        case ReclusterSubmitted( jobId ) =>
                            val clusters = prevState.cluster.clusterState match {
                                case cs : ClusterStateWithResults => cs.clusters
                                case _ => Vector.empty
                            }
                            prevState.copy(
                                cluster = prevState.cluster.copy(
                                    clusterState = ReclusterPending( clusters, jobId )
                                )
                            )

                        case ViewConcept( concept ) =>
                            prevState.cluster.clusterState match {
                                case cs : ClusterComplete =>
                                    prevState.copy(
                                        cluster = prevState.cluster.copy(
                                            clusterState = cs.copy(
                                                viewedConcept = Some( concept )
                                            )
                                        )
                                    )
                                case _ => prevState
                            }

                        case ClearConceptView =>
                            prevState.cluster.clusterState match {
                                case cs : ClusterComplete =>
                                    prevState.copy(
                                        cluster = prevState.cluster.copy(
                                            clusterState = cs.copy(
                                                viewedConcept = None
                                            )
                                        )
                                    )
                                case _ => prevState
                            }

                        case AcceptPhrases( index, phrases ) =>
                            phrases.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                acceptPhrase( lastState, index, nextPhrase )
                            }

                        case RejectPhrases( index, phrases ) =>
                            phrases.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                rejectPhrase( lastState, index, nextPhrase )
                            }

                        case RestorePhrases( index, phrases ) =>
                            phrases.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                restorePhrase( lastState, index, nextPhrase )
                            }

                        case RejectCluster( index ) =>
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    cl.cluster.rankedWords.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                        rejectPhrase( lastState, index, nextPhrase )
                                    }
                            }

                        case RejectUncurated( index ) =>
                            println( "rejecting uncurated" )
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    val rejectablePhrases =
                                        cl.cluster.rankedWords.toSet --
                                          cl.acceptedPhrases.keySet --
                                          cl.rejectedPhrases.keySet
                                    rejectablePhrases.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                        rejectPhrase( lastState, index, nextPhrase )
                                    }
                            }

                        case RestoreCluster( index ) =>
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    cl.cluster.rankedWords.foldLeft( prevState ) { ( lastState, nextPhrase ) =>
                                        restorePhrase( lastState, index, nextPhrase )
                                    }
                            }

                        case AddDefaultConcept( index ) =>
                            val (stateWithConcept, conceptId) = addDefaultConcept( prevState, index )
                            updateCluster(
                                stateWithConcept,
                                index,
                                cl => cl.copy( currentTarget = Some( conceptId ) )
                            )

                        case TargetConcept( index, target ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( currentTarget = Some( target ) )
                            )

                        case SelectPhrases( index, phrases ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( selectedPhrases = cl.selectedPhrases ++ phrases.toSet )
                            )

                       case DeselectPhrases( index, phrases )=>
                           updateCluster(
                               prevState,
                               index,
                               cl => cl.copy( selectedPhrases = cl.selectedPhrases -- phrases.toSet )
                           )

                        case SelectAllPhrases( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( selectedPhrases = cl.cluster.rankedWords.toSet )
                            )

                        case SelectAcceptedPhrases( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( selectedPhrases = cl.selectedPhrases ++ cl.acceptedPhrases.keySet )
                            )

                        case SelectRejectedPhrases( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( selectedPhrases = cl.selectedPhrases ++ cl.rejectedPhrases.keySet )
                            )

                        case SelectUncuratedPhrases( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy(
                                    selectedPhrases =
                                        cl.selectedPhrases ++
                                        ( cl.cluster.rankedWords.toSet -- cl.rejectedPhrases.keySet -- cl.acceptedPhrases.keySet )
                                )
                            )

                        case ClearPhraseSelection( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( selectedPhrases = Set.empty )
                            )

                        case InvertPhraseSelection( index ) =>
                            updateCluster(
                                prevState,
                                index,
                                cl => cl.copy(
                                    selectedPhrases = cl.cluster.rankedWords.toSet -- cl.selectedPhrases
                                )
                            )

                        case AcceptSelectedPhrases( index ) =>
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    cl.selectedPhrases.foldLeft( prevState ) { ( oldState, nextPhrase ) =>
                                        acceptPhrase( oldState, index, nextPhrase )
                                    }
                            }

                        case RejectSelectedPhrases( index ) =>
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    cl.selectedPhrases.foldLeft( prevState ) { ( oldState, nextPhrase ) =>
                                        rejectPhrase( oldState, index, nextPhrase )
                                    }
                            }

                        case RestoreSelectedPhrases( index ) =>
                            getCluster( prevState, index ) match {
                                case None => prevState
                                case Some( cl ) =>
                                    cl.selectedPhrases.foldLeft( prevState ) { ( oldState, nextPhrase ) =>
                                        restorePhrase( oldState, index, nextPhrase )
                                    }
                            }

                        case SetRecommendedName( index, newName ) =>
                            val stateWithNewName = updateCluster(
                                prevState,
                                index,
                                cl => cl.copy( cluster = cl.cluster.copy( recommendedName = newName ) )
                            )

                            getCluster( stateWithNewName, index ) match {
                                case Some( cl ) if (
                                  cl.cluster.rankedWords.contains( newName ) &&
                                  !cl.acceptedPhrases.contains( newName ) ) =>
                                    acceptPhrase( stateWithNewName, index, newName )
                                case _ => stateWithNewName
                            }
                }

            }
        }

        // Generate handler and loader for handling loading for concept explorer
        val loadingComponentId : UUID = UUID.randomUUID()

        val clusterCuratorLoadingHandler = DartLoading.loadingHandler( loadingComponentId, _.zoomTo( _.conceptState.cluster.loadingState ) )

        def buildLoader( dispatcher : DartAction => Callback ) : DartLoading.Loader = new DartLoading.Loader( loadingComponentId, dispatcher )
    }

}
