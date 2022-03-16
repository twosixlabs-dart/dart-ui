package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.ConceptSearchDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.wm.{Event, Positive, WmConceptMetadata}
import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterConceptBridgeDeps, DartClusterDI, DartConceptDeps, DartTaxonomyDI, NodeSimilarity, TaxonomyIdDeps, WmDartConceptDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.util.Try

trait DartClusterCuratorClusterDeps {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with DartConceptExplorerDI
      with CuratedClusterDI
      with ConceptSearchDeps
      with TaxonomyIdDeps
      with DartClusterDI
      with DartTaxonomyDI
      with DartClusterConceptBridgeDeps
      with DartConceptDeps
      with DartClusterCuratorClustorLayoutDeps =>

    type DartClusterCuratorClusterRenderContext

    val dartClusterCuratorCluster : DartClusterCuratorCluster = new DartClusterCuratorCluster

    class DartClusterCuratorCluster
      extends ViewedDartComponent[
        DartClusterCuratorCluster.Props,
        DartClusterCuratorClusterRenderContext,
        DartClusterCurator.State,
      ] {

        override def stateView( coreState : CoreState ) : DartClusterCurator.State =
            coreState.conceptState.cluster

        override protected def componentDidUpdate(
            modState : ( SnapshotType => SnapshotType ) => Callback,
            props : DartClusterCuratorCluster.Props,
            prevProps : DartClusterCuratorCluster.Props,
            state : Unit,
            prevState : Unit,
            contextView : DartClusterCurator.State,
            prevContextView : DartClusterCurator.State,
            prevContext : DartContext,
        )(
            implicit context : DartContext,
        ) : Callback = {
            val taxonomy = context.coreState.conceptState.taxonomy
            props.cluster.acceptedPhrases.foldLeft( Callback() ) { ( callback, nextPhraseTuple ) =>
                val (phrase, id) = nextPhraseTuple
                val restoreCallback =
                    callback >> context.dispatch( DartClusterCurator.RestorePhrase( props.index, phrase ) )
                ( taxonomy.idEntry( id ) match {
                    case None => restoreCallback
                    case Some( entry ) if clusterConceptBridge.conceptHasPhrase( entry.concept, phrase ) =>
                        callback
                    case Some( _ ) => restoreCallback
                } )
            } >>
            props.cluster.rejectedPhrases.foldLeft( Callback() ) { ( callback, nextPhraseTuple ) =>
                val (phrase, idOpt) = nextPhraseTuple
                val restoreCallback =
                    callback >> context.dispatch( DartClusterCurator.RestorePhrase( props.index, phrase ) )
                idOpt match {
                    case None => callback
                    case Some( id ) => taxonomy.idEntry( id ) match {
                        case None => restoreCallback
                        case Some( entry ) if !clusterConceptBridge.conceptHasPhrase( entry.concept, phrase ) =>
                            Callback()
                        case Some( _ ) => restoreCallback
                    }
                }
            }
        }

        override def render( props : DartClusterCuratorCluster.Props, stateView : DartClusterCurator.State )(
            implicit renderContext : DartClusterCuratorClusterRenderContext,
            stateContext : DartContext ) : VdomElement = {

            import DartClusterCuratorCluster.ConceptTarget

            val taxonomy = stateContext.coreState.conceptState.taxonomy

            val targetId : Option[ TaxonomyId ] = props.cluster.currentTarget
            val targetEntry : Option[ DartTaxonomyEntry ] =
                targetId.flatMap( id => taxonomy.idEntry( id ) )
            val dispatch : DartAction => Callback = stateContext.dispatch

            val recommendedPhrases : Set[ String ] = props.cluster.cluster.rankedWords.toSet
            val acceptedPhrases : Map[ String, ConceptTarget ] =
                props.cluster.acceptedPhrases.flatMap( tup => {
                    import DartClusterCurator._
                    val (phrase, conceptId) = tup
                    taxonomy.idEntry( conceptId ) map { entry =>
                        phrase ->
                        ConceptTarget(
                            entry.path,
                            dispatch( TargetConcept( props.index, conceptId ) ),
                            dispatch( ViewConcept( conceptId ) )
                        )
                    }
                } )

            val rejectedPhrases : Map[ String, Option[ ConceptTarget ] ] =
                props.cluster.rejectedPhrases.map( tup => {
                    import DartClusterCurator._
                    val (phrase, conceptIdOpt) = tup

                    phrase ->
                    conceptIdOpt.flatMap( conceptId => {
                        taxonomy.idEntry( conceptId ) map { entry =>
                            ConceptTarget(
                                entry.path,
                                dispatch( TargetConcept( props.index, conceptId ) ),
                                dispatch( ViewConcept( conceptId ) )
                            )
                        }
                    } )
                } )

            val uncuratedPhrases : Set[ String ] = recommendedPhrases -- acceptedPhrases.keySet -- rejectedPhrases.keySet
            val selectedPhrases : Set[ String ] = props.cluster.selectedPhrases

            import DartClusterCurator._
            import clusterConceptBridge._

            val layoutProps : DartClusterCuratorCluster.LayoutProps =
                DartClusterCuratorCluster.LayoutProps(
                    // Basic data
                    name = props.cluster.cluster.recommendedName,
                    phrases = props.cluster.cluster.rankedWords,
                    // Curation
                    rejectNode = ( ) => dispatch( RejectCluster( props.index ) ) >> dispatch( ClearPhraseSelection( props.index ) ),
                    restoreNode = ( ) => dispatch( RestoreCluster( props.index ) ) >> dispatch( ClearPhraseSelection( props.index ) ),
                    acceptedPhrases = acceptedPhrases,
                    rejectedPhrases = rejectedPhrases,
                    setName = newName => dispatch( SetRecommendedName( props.index, newName ) ),
                    acceptPhrases = phrases => dispatch( AcceptPhrases( props.index, phrases ) ),
                    rejectPhrases = phrases => dispatch( RejectPhrases( props.index, phrases ) ),
                    restorePhrases = phrases => dispatch( RestorePhrases( props.index, phrases ) ),
                    // Phrase selection (and curation)
                    selectedPhrases = selectedPhrases,
                    selectPhrases = phrases => dispatch( SelectPhrases( props.index, phrases ) ),
                    unselectPhrases = phrases => dispatch( DeselectPhrases( props.index, phrases ) ),
                    selectAll = dispatch( SelectAllPhrases( props.index ) ),
                    selectAccepted = dispatch( SelectAcceptedPhrases( props.index ) ),
                    selectRejected = dispatch( SelectRejectedPhrases( props.index ) ),
                    selectUncurated = dispatch( SelectUncuratedPhrases( props.index ) ),
                    clearSelection = dispatch( ClearPhraseSelection( props.index ) ),
                    clearAcceptedSelection = dispatch( DeselectPhrases( props.index, acceptedPhrases.keySet ) ),
                    clearRejectedSelection = dispatch( DeselectPhrases( props.index, rejectedPhrases.keySet ) ),
                    clearUncuratedSelection = dispatch( DeselectPhrases( props.index, uncuratedPhrases ) ),
                    acceptSelection = dispatch( AcceptSelectedPhrases( props.index ) ),
                    rejectSelection = dispatch( RejectSelectedPhrases( props.index ) ),
                    restoreSelection = dispatch( RestoreSelectedPhrases( props.index ) ),
                    // Concept targeting
                    target = targetEntry.map( _.path ),
                    recommendedConcepts = props.cluster.cluster.similarNodes
                      .flatMap( ( sn : DartNodeSimilarity ) => {
                          taxonomy.idEntry( sn.conceptId ).map( entry => {
                              val callback = stateContext
                                .dispatch( TargetConcept( props.index, sn.conceptId ) )
                              (entry.path, sn.score, callback)
                          } ).toList
                      } ),
                    conceptSearch = ConceptSearch.Props(
                        taxonomy = taxonomy,
                        onSelectConcept = {
                            conceptId => {
                                stateContext.dispatch( TargetConcept( props.index, conceptId ) )
                            }
                        },
                    ),
                    createDefaultTarget = stateContext.dispatch( AddDefaultConcept( props.index ) )
                )

            dartClusterCuratorClusterLayout( layoutProps.toDartProps )
        }


    }


    object DartClusterCuratorCluster {

        case class RenderParams(
            props : Props,
            stateView : DartClusterCurator.State,
            dartContext : DartContext,
        )

        case class Props(
            cluster : CuratedCluster,
            index : Int,
        )

        case class ConceptTarget(
            path : ConceptPath,
            target : Callback,
            view : Callback,
        )

        case class LayoutProps(
            // Reject/curate parameters
            rejectNode : () => Callback,
            restoreNode : () => Callback,

            // Cluster name parameters
            name : String,
            setName : String => Callback,

            // Phrase parameters
            phrases : Seq[ String ],
            acceptedPhrases : Map[ String, ConceptTarget ],
            rejectedPhrases : Map[ String, Option[ ConceptTarget ] ],
            acceptPhrases : Seq[ String ] => Callback,
            rejectPhrases : Seq[ String ] => Callback,
            restorePhrases : Seq[ String ] => Callback,


            // Selection parameters
            selectedPhrases : Set[ String ],
            selectPhrases : Seq[ String ] => Callback,
            unselectPhrases : Seq[ String ] => Callback,
            selectAll : Callback,
            selectAccepted : Callback,
            selectRejected : Callback,
            selectUncurated : Callback,
            clearSelection : Callback,
            clearAcceptedSelection : Callback,
            clearRejectedSelection : Callback,
            clearUncuratedSelection : Callback,
            acceptSelection : Callback,
            rejectSelection : Callback,
            restoreSelection : Callback,

            // Target concept parameters
            target : Option[ ConceptPath ],
            recommendedConcepts : Seq[ (Seq[ String ], Double, Callback) ],
            conceptSearch : ConceptSearch.Props,
            createDefaultTarget : Callback,
        )

    }

}

trait DartClusterCuratorClustorLayoutDeps
  extends DartClusterCuratorClusterDeps {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with DartConceptExplorerDI
      with TaxonomyIdDeps
      with DartTaxonomyDI
      with DartClusterDI
      with DartClusterConceptBridgeDeps
      with CuratedClusterDI
      with ConceptSearchDeps
      with DartConceptDeps =>

    val dartClusterCuratorClusterLayout : DartClusterCuratorClusterLayout

    type DartClusterCuratorClusterLayoutState

    trait DartClusterCuratorClusterLayout
      extends DartLayoutComponent[ DartClusterCuratorCluster.LayoutProps, DartClusterCuratorClusterRenderContext, DartClusterCuratorClusterLayoutState ]

}
