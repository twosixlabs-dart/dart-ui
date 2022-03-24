package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context.NoOpDartTenantsContextDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test.DartComponentTestStateConfiguration
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait StateAccessComponentTestConfig
  extends DartComponentTestStateConfiguration
    with NoOpDartTenantsContextDI {

    val testRenderContext : StateAccessComponentRenderContext

    override def defaultRenderer( implicit context : DartContext ) : VdomElement = {
        stateAccessComponent( StateAccessComponent.Props().toDartPropsRC( testRenderContext ) )
    }

    import StateAccessApi.ConceptsState
    import StateAccessComponent.StateId

    abstract class ObsType( ele : HTMLElement ) extends TestHookObserver( ele ) {

        // Read methods
        def getCurrentStateId : Option[ StateId ]
        def getKeys : Seq[ String ]
        def getVersions( key : String ) : Option[ Int ]
        def getNewKeyText : String
        def getCurrentState : ConceptsState = {
            val context = getDartContext()
            val taxonomy = context.coreState.conceptState.taxonomy
            val clusters : Option[ Seq[ CuratedCluster ] ] = context.coreState.conceptState.cluster.clusterState match {
                case cs : DartClusterCurator.ClusterStateWithResults =>
                    Some( cs.clusters )
                case _ => None
            }
            ConceptsState( taxonomy, Some( StateAccessApi.ClusterState( clusters.toSeq.flatten, None ) ) )
        }

        // Action methods
        def refresh() : Unit
        def chooseKey( key : String ) : Unit
        def chooseVersion( key : String, version : Int ) : Unit
        def setKeyText( text : String ) : Unit
        def gotoPreviousState() : Unit
        def gotoNextState() : Unit
        def gotoOldestState() : Unit
        def gotoLatestState() : Unit
        def saveCurrentState() : Unit
        def saveCurrentStateToNewKey() : Unit

    }

    override type Obs <: ObsType

    def genObs( ele : HTMLElement ) : Obs

    override val defaultInitialState : Unit = ()

    override type St = Unit

    // Focus definitions
    def currentStateId : dsl.FocusOption[ StateId ] = dsl.focus( "Current StateId" ).option( _.obs.getCurrentStateId )
    def currentState : dsl.FocusValue[ ConceptsState ] = dsl.focus( "Current ConceptsState" ).value( _.obs.getCurrentState )
    def allKeys : dsl.FocusColl[ Seq, String ] = dsl.focus( "Saved state keys" ).collection( _.obs.getKeys )
    def versionsCount( key : String ) : dsl.FocusOption[ Int ] = dsl.focus( s"Number of versions of $key" ).option( _.obs.getVersions( key ) )
    def newKeyText : dsl.FocusValue[ String ] = dsl.focus( "Text of new key input" ).value( _.obs.getNewKeyText )

    // Actions definitions
    def refresh : dsl.Actions = dsl.action( "Refresh saved state keys" )( v => Future( v.obs.refresh() ) )
    def chooseKey( key : String ) : dsl.Actions = dsl.action( s"Select saved state in key: $key" )( v => Future( v.obs.chooseKey( key ) ) )
    def chooseVersion( key : String, version : Int ) : dsl.Actions =
        dsl.action( s"Select saved state version $version in key $key" )( v => Future( v.obs.chooseVersion( key, version ) ) )
    def setKeyText( text : String ) : dsl.Actions =
        dsl.action( s"Set text of new key input to: $text" )( v => Future( v.obs.setKeyText( text ) ) )
    def gotoPreviousState : dsl.Actions = dsl.action( "Go to previous state" )( v => Future( v.obs.gotoPreviousState() ) )
    def gotoNextState : dsl.Actions = dsl.action( "Go to next state" )( v => Future( v.obs.gotoNextState() ) )
    def gotoOldestState : dsl.Actions = dsl.action( "Go to previous state" )( v => Future( v.obs.gotoOldestState() ) )
    def gotoLatestState : dsl.Actions = dsl.action( "Go to next state" )( v => Future( v.obs.gotoLatestState() ) )
    def saveCurrentState : dsl.Actions = dsl.action( "Save current state" )( v => Future( v.obs.saveCurrentState() ) )
    def saveCurrentStateToNewKey : dsl.Actions =
        dsl.action( "Go save current state to new key" )( v => Future( v.obs.saveCurrentStateToNewKey() ) )
}
