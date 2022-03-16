package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test.DartComponentTestStateConfiguration
import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, NodeSimilarity}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

trait DartClusterCuratorClusterTestStateConfig
  extends DartComponentTestStateConfiguration {

    override def defaultRenderer( implicit context : DartContext ) : VdomElement = {

        context.coreState.conceptState.cluster.clusterState match {
            case DartClusterCurator.NoClusterState => <.div( "NOTHING" )
            case DartClusterCurator.InitialClusterPending => <.div( "SOMETHING" )
            case withClusters : DartClusterCurator.ClusterStateWithResults =>
                dartClusterCuratorCluster(
                    DartClusterCuratorCluster.Props(
                        withClusters.clusters.head,
                        0,
                    ).toDartPropsRC(),
                )
        }

    }

    abstract class ObsType( ele : HTMLElement ) extends TestHookObserver( ele ) {
        def getRecommendedName : String
        def getRecommendedPhrases : Set[ String ]
        def getAcceptedPhrases : Set[ String ]
        def getRejectedPhrases : Set[ String ]
        def getIsRejected : Boolean
        def getRecommendedConcepts : Set[ Vector[ String ] ]
        def getConceptSearchInput : String
        def getSearchedConcepts : Option[ Set[ Vector[ String ] ] ]
        def getTargetConcept : Option[ Vector[ String ] ]
        def getSelectedPhrases : Set[ String ]

        def getAcceptedPhraseTarget( phrase : String ) : Seq[ String ]

        def getState : State = State(
            getRecommendedName,
            getRecommendedPhrases,
            getAcceptedPhrases,
            getRejectedPhrases,
            getSelectedPhrases,
            getIsRejected,
            getRecommendedConcepts,
            getConceptSearchInput,
            getSearchedConcepts,
            getTargetConcept
        )

        def setClusterToCurate() : Unit
        def setClusterToRejected() : Unit
        def acceptPhrase( phrase : String ) : Unit
        def rejectPhrase( phrase : String ) : Unit
        def usePhraseAsName( phrase : String ) : Unit
        def setCustomRecommendedName( name : String ) : Unit
        def restorePhrase( phrase : String ) : Unit

        // Phrase selection/curation actions
        def selectPhrases( phrases : Iterable[ String ] ) : Unit
        def selectAllPhrases() : Unit
        def selectAcceptedPhrases() : Unit
        def selectRejectedPhrases() : Unit
        def selectUncuratedPhrases() : Unit
        def clearSelection() : Unit
        def acceptSelectedPhrases() : Unit
        def rejectSelectedPhrases() : Unit
        def restoreSelectedPhrases() : Unit

        // Concept targeting/viewing actions
        def targetRecommendedConcept( value : Vector[ String ] ) : Unit
        def viewRecommendedConcept( value : Vector[ String ] ) : Unit
        def updateConceptSearchInput( value : String ) : Unit
        def usePhraseForConceptSearch( phrase : String ) : Unit
        def targetSearchedConcept( value : Vector[ String ] ) : Unit
        def viewSearchedConcept( value : Vector[ String ] ) : Unit
        def exitConceptSearch() : Unit
        def targetAcceptedPhraseTarget( value : String ) : Unit
        def viewAcceptedPhraseTarget( value : String ) : Unit
        def addDefaultTarget() : Unit
    }

    override type Obs <: ObsType

    def genObs( ele : HTMLElement ) : Obs

    case class State(
        recommendedName : String,
        recommendedPhrases : Set[ String ],
        acceptedPhrases : Set[ String ],
        rejectedPhrases : Set[ String ],
        selectedPhrases : Set[ String ],
        rejected : Boolean,
        recommendedConcepts : Set[ Vector[ String ] ],
        conceptSearchInput : String,
        searchedConcepts : Option[ Set[ Vector[ String ] ] ],
        targetConcept : Option[ Vector[ String ] ],
    )

    val testName = "test_name"
    val testPhrase1 = "test_phrase_1"
    val testPhrase2 = "test_phrase_2"

    val testNameSanitized = "test name"
    val testPhrase1Sanitized = "test phrase 1"
    val testPhrase2Sanitized = "test phrase 2"

    val testRoot = "test_root"
    val testBranch1 = "test_branch_1"
    val testBranch2 = "test_branch_2"
    val testBranch3 = "test_branch_3"
    val testLeaf1 = "test_leaf_1"
    val testLeaf2 = "test_leaf_2"
    val testLeaf3 = "test_leaf_3"

    val testConcept1 = Vector( testRoot, testBranch1, testLeaf1 )
    val testConcept2 = Vector( testRoot, testBranch1, testLeaf2 )
    val testConcept3 = Vector( testRoot, testBranch2, testBranch3, testLeaf3 )

    lazy val taxonomy = DartTaxonomy( Set(
        DartConcept( "clusters", Set.empty ),
        DartConcept(
            testRoot, Set(
                DartConcept(
                    testBranch1, Set(
                        DartConcept( testLeaf1, Set.empty ),
                        DartConcept( testLeaf2, Set.empty ),
                    )
                ),
                DartConcept(
                    testBranch2, Set(
                        DartConcept(
                            testBranch3, Set(
                                DartConcept( testLeaf3, Set.empty ),
                            )
                        )
                    )
                ),
            )
        )
    ) )

    override val defaultInitialState : State = State(
        recommendedName = testName,
        recommendedPhrases = Set( testName, testPhrase1, testPhrase2 ),
        acceptedPhrases = Set.empty,
        rejectedPhrases = Set.empty,
        selectedPhrases = Set.empty,
        rejected = false,
        recommendedConcepts = Set.empty,
        conceptSearchInput = "",
        searchedConcepts = None,
        targetConcept = None,
    )

    override type St = State

    //    override def setupAllPlans( plan : dsl.Plan ) : dsl.Plan =
    //        plan.addInvariants( dsl.test( "Expected and Observed State should Agree" )( os => os.state == os.obs.getState ) )

    // Focus definitions
    def rejected : dsl.FocusValue[ Boolean ] = dsl.focus( "Is cluster rejected?" ).value( _.obs.getIsRejected )
    def recommendedName : dsl.FocusValue[ String ] = dsl.focus( "Recommended name" ).value( _.obs.getRecommendedName )
    def recommendedPhrases : dsl.FocusColl[ Set, String ] = dsl.focus( "Recommended phrases" ).collection( _.obs.getRecommendedPhrases )
    def acceptedPhrases : dsl.FocusColl[ Set, String ] = dsl.focus( "Accepted phrases" ).collection( _.obs.getAcceptedPhrases )
    def acceptedPhraseTarget( phrase : String ) : dsl.FocusColl[ Seq, String ] = dsl.focus( s"Accepted phrase $phrase's target concept'" ).collection( _.obs.getAcceptedPhraseTarget( phrase ) )
    def rejectedPhrases : dsl.FocusColl[ Set, String ] = dsl.focus( "Rejected phrases" ).collection( _.obs.getRejectedPhrases )
    def selectedPhrases : dsl.FocusColl[ Set, String ] = dsl.focus( "Selected phrases" ).collection( _.obs.getSelectedPhrases )

    def target : dsl.FocusOption[ Vector[ String ] ] = dsl.focus( "Target concept" ).option( _.obs.getTargetConcept )
    def recommendedConcepts : dsl.FocusColl[ Set, Vector[ String ] ] = dsl.focus( "Recommended concepts" ).collection( _.obs.getRecommendedConcepts )
    def conceptSearchInput : dsl.FocusValue[ String ] = dsl.focus( "Concept search input" ).value( _.obs.getConceptSearchInput )
    def searchedConcepts : dsl.FocusOption[ Set[ Vector[ String ] ] ] = dsl.focus( "Searched concepts" ).option( _.obs.getSearchedConcepts )

    // Actions definitions

    final def initializeState : dsl.Actions =
        dispatch( DartConceptExplorer.LoadTaxonomy( taxonomy ) ) >>
        dispatch( DartClusterCurator.ClusterResults(
            clusters = Seq(
                Cluster(
                    "test-id",
                    0.78347283,
                    defaultInitialState.recommendedName,
                    defaultInitialState.recommendedPhrases.toSeq,
                    similarNodes = Seq(
                        NodeSimilarity(
                            testConcept1,
                            0.50,
                        ),
                        NodeSimilarity(
                            testConcept2,
                            0.30,
                        )
                    ),
                )
            ),
        ) )

    // Individual phrase curation actions
    final def setClusterToCurate : dsl.Actions = dsl.action( "Set cluster to curate" )( v => Future( v.obs.setClusterToCurate() ) ).updateState( _.copy( rejected = false ) )
    final def setClusterToRejected : dsl.Actions = dsl.action( "Set cluster to rejected" )( v => Future( v.obs.setClusterToRejected() ) )
      .updateState( _.copy( rejected = true, targetConcept = None ) )
    final def acceptRecommendedPhrase( phrase : String ) : dsl.Actions = dsl.action( s"Accept phrase: $phrase" )( v => Future( v.obs.acceptPhrase( phrase) ) )
      .updateState( s => s.copy( acceptedPhrases = s.acceptedPhrases + phrase ) )
    final def rejectRecommendedPhrase( phrase : String ) : dsl.Actions = dsl.action( s"Rejected phrase: $phrase" )( v => Future( v.obs.rejectPhrase( phrase ) ) )
      .updateState( s => s.copy( rejectedPhrases = s.rejectedPhrases + phrase ) )
    final def useRecommendedPhraseAsName( phrase : String ) : dsl.Actions = dsl.action( s"Use phrase as cluster name: $phrase" )( v => Future( v.obs.usePhraseAsName( phrase) ) )
      .updateState( s => s.copy( recommendedName = phrase, acceptedPhrases = s.acceptedPhrases + phrase, rejectedPhrases = s.rejectedPhrases - phrase ) )
    final def setCustomRecommendedName( name : String ) : dsl.Actions = dsl.action( s"Set recommended name to custom string: $name" )( v => Future( v.obs.setCustomRecommendedName( name ) ) )
      .updateState( s => s.copy( recommendedName = name, acceptedPhrases = s.acceptedPhrases + name, recommendedPhrases = s.recommendedPhrases + name, rejectedPhrases = s.rejectedPhrases - name ) )
    final def restorePhrase( phrase : String ) : dsl.Actions = dsl.action( s"Restore phrase: $phrase" )( v => Future( v.obs.restorePhrase( phrase ) ) )
      .updateState( s => s.copy( acceptedPhrases = s.acceptedPhrases - phrase, rejectedPhrases = s.rejectedPhrases - phrase ) )

    // Phrase selection/curation actions
    final def selectPhrases( phrases : Iterable[ String ] ) : dsl.Actions = {
        dsl.action( s"Select phrases" )( v => Future( v.obs.selectPhrases( phrases ) ) )
          .updateState( s => s.copy( selectedPhrases = s.selectedPhrases ++ phrases.toSet ) )
    }
    final def selectAllPhrases : dsl.Actions = dsl.action( "Select all phrases" )( v => Future( v.obs.selectAllPhrases() ) )
      .updateState( s => s.copy( selectedPhrases = s.recommendedPhrases ) )
    final def selectAcceptedPhrases : dsl.Actions = dsl.action( "Select accepted phrases" )( v => Future( v.obs.selectAcceptedPhrases() ) )
      .updateState( s => s.copy( selectedPhrases = s.selectedPhrases ++ s.acceptedPhrases ) )
    final def selectRejectedPhrases : dsl.Actions = dsl.action( "Select rejected phrases" )( v => Future( v.obs.selectRejectedPhrases() ) )
      .updateState( s => s.copy( selectedPhrases = s.selectedPhrases ++ s.rejectedPhrases ) )
    final def selectUncuratedPhrases : dsl.Actions = dsl.action( "Select uncurated phrases" )( v => Future( v.obs.selectUncuratedPhrases() ) )
      .updateState( s => s.copy( selectedPhrases = s.selectedPhrases ++ ( s.recommendedPhrases -- s.acceptedPhrases -- s.rejectedPhrases ) ) )
    final def clearSelection : dsl.Actions = dsl.action( "Clear phrase selection" )( v => Future( v.obs.clearSelection() ) )
      .updateState( s => s.copy( selectedPhrases = Set.empty ) )
    final def acceptSelectedPhrases : dsl.Actions = dsl.action( "Accept selected phrases" )( v => Future( v.obs.acceptSelectedPhrases() ) )
      .updateState( s => s.copy( acceptedPhrases = s.acceptedPhrases ++ s.selectedPhrases, rejectedPhrases = s.rejectedPhrases -- s.selectedPhrases ) )
    final def rejectSelectedPhrases : dsl.Actions = dsl.action( "Reject selected phrases" )( v => Future( v.obs.rejectSelectedPhrases() ) )
      .updateState( s => s.copy( acceptedPhrases = s.acceptedPhrases -- s.selectedPhrases, rejectedPhrases = s.rejectedPhrases ++ s.selectedPhrases ) )
    final def restoreSelectedPhrases : dsl.Actions = dsl.action( "Restore selected phrases" )( v => Future( v.obs.restoreSelectedPhrases() ) )
      .updateState( s => s.copy( acceptedPhrases = s.acceptedPhrases -- s.selectedPhrases, rejectedPhrases = s.rejectedPhrases -- s.selectedPhrases ) )

    // Concept targeting/viewing actions
    final def targetRecommendedConcept( conceptPath : Vector[ String ] ) : dsl.Actions = dsl.action( s"Target recommended concept: ${conceptPath.mkString( "/" )}" )( v => Future( v.obs.targetRecommendedConcept( conceptPath ) ) )
      .updateState( s => s.copy( targetConcept = Some( conceptPath ) ) )
    final def viewRecommendedConcept( conceptPath : Vector[ String ] ) : dsl.Actions = dsl.action( "View recommended concept" )( v => Future( v.obs.viewRecommendedConcept( conceptPath ) ) )
    final def updateConceptSearchInput( value : String, expectedResults : Set[ Vector[ String ] ] ) : dsl.Actions = dsl.action( "Update concept search input" )( v => Future( v.obs.updateConceptSearchInput( value ) ) )
      .updateState( s => s.copy( conceptSearchInput = value, searchedConcepts = if ( value.isEmpty ) None else Some( expectedResults ) ) )
    final def usePhraseForConceptSearch( phrase : String, expectedResults : Set[ Vector[ String ] ] ) : dsl.Actions = dsl.action( "Use phrase for concept search" )( v => Future( v.obs.usePhraseForConceptSearch( phrase ) ) )
      .updateState( s => s.copy( conceptSearchInput = phrase, searchedConcepts = Some( expectedResults ) ) )
    final def targetSearchedConcept( conceptPath : Vector[ String ] ) : dsl.Actions = dsl.action( "Target searched concept" )( v => Future( v.obs.targetSearchedConcept( conceptPath ) ) )
      .updateState( s => s.copy( targetConcept = Some( conceptPath ), conceptSearchInput = "", searchedConcepts = None ) )
    final def viewSearchedConcept( conceptPath : Vector[ String ] ) : dsl.Actions = dsl.action( "View searched concept" )( v => Future( v.obs.viewSearchedConcept( conceptPath ) ) )
      .updateState( s => s.copy( searchedConcepts = None, conceptSearchInput = "" ) )
    final def exitConceptSearch : dsl.Actions = dsl.action( "Exit concept search" )( v => Future( v.obs.exitConceptSearch() ) )
    final def targetAcceptedPhraseTarget( phrase : String, expectedTarget : Vector[ String ] ) : dsl.Actions = dsl.action( s"Target accepted phrase's target: $phrase" )( v => Future( v.obs.targetAcceptedPhraseTarget( phrase ) ) )
      .updateState( s => s.copy( targetConcept = Some( expectedTarget ) ) )
    final def viewAcceptedPhraseTarget( value : String ) : dsl.Actions = dsl.action( "View accepted phrase target" )( v => Future( v.obs.viewAcceptedPhraseTarget( value ) ) )
    final def addDefaultTarget : dsl.Actions = dsl.action( "Add default target" )( v => Future( v.obs.addDefaultTarget() ) )
}
