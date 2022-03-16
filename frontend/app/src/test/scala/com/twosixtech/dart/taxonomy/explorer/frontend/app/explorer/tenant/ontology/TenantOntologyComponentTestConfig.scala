package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test.DartComponentTestStateConfiguration
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait TenantOntologyComponentTestConfig
  extends DartComponentTestStateConfiguration {

    val testRenderContext : TenantOntologyComponentRenderContext

    override def defaultRenderer( implicit context : DartContext ) : VdomElement = {
        tenantOntologyComponent( TenantOntologyComponent.Props().toDartPropsRC( testRenderContext ) )
    }

    import StateAccessApi.ConceptsState
    import StateAccessComponent.StateId

    import TenantOntologyComponent.{State => TOState}

    case class TOContents(
        publishedVersion : Boolean,
        stagedVersion : Boolean,
    )

    abstract class ObsType( ele : HTMLElement )
      extends TestHookObserver( ele ) {

        // Read methods
        def getTenants : Seq[ String ]
        def getTenantOntologyContents( tenant : String ) : Option[ TOContents ]
        // Empty if tenant doesn't exist; 0 if no versions in tenant
        def getPublishedOntologyVersionsCount( tenant : String ) : Option[ Int ]
        // Empty if tenant doesn't exist; 0 if no versions in tenant
        def getStagedOntologyVersionsCount( tenant : String ) : Option[ Int ]
        def importMenuIsVisible : Boolean
        def confirmationMenuIsVisible : Boolean
        def getTenantOntologyState : TOState = {
            getDartContext().coreState.conceptState.tenantOntologyState
        }
        def getTaxonomyState : DartTaxonomy = {
            getDartContext().coreState.conceptState.taxonomy
        }
        def getClusterState : DartClusterCurator.ClusterState = {
            getDartContext().coreState.conceptState.cluster.clusterState
        }

        // Action methods
        def refresh() : Unit
        def importPublishedLatestVersion( tenant : String ) : Unit
        def importPublishedVersion( tenant : String, version : Int ) : Unit
        def importStagedLatestVersion( tenant : String ) : Unit
        def importStagedVersion( tenant : String, version : Int ) : Unit
        def selectKeepClusterState() : Unit
        def selectClearClusterState() : Unit
        def stageTaxonomyTo( tenant : String ) : Unit
        def publishStagedTaxonomy( tenant : String ) : Unit
        def confirmStageOrPublish() : Unit
        def cancelStageOrPublish() : Unit
    }

    override type Obs <: ObsType

    def genObs( ele : HTMLElement ) : Obs

    override type St = Unit

    override val defaultInitialState : Unit = ()

    // Focus definitions
    def allTenants : dsl.FocusColl[ Seq, String ] = dsl.focus( "Tenants" ).collection( _.obs.getTenants )
    def tenantOntologyContents( tenant : String ) : dsl.FocusOption[ TOContents ] = dsl.focus( s"Ontology state for tenant: $tenant" ).option( _.obs.getTenantOntologyContents( tenant ) )
    def publishedOntologyVersionsCount( tenant : String ) : dsl.FocusOption[ Int ] = dsl.focus( s"Number of published ontology versions in tenant: $tenant" ).option( _.obs.getPublishedOntologyVersionsCount( tenant ) )
    def stagedOntologyVersionsCount( tenant : String ) : dsl.FocusOption[ Int ] = dsl.focus( s"Number of versions of staged ontology versions in tenant: $tenant" ).option( _.obs.getStagedOntologyVersionsCount( tenant ) )
    def importMenuIsVisible : dsl.FocusValue[ Boolean ] = dsl.focus( "Ontology import menu visibility" ).value( _.obs.importMenuIsVisible )
    def confirmationMenuIsVisible : dsl.FocusValue[ Boolean ] = dsl.focus( "Staging or publishing confirmation menu visibility" ).value( _.obs.confirmationMenuIsVisible )
    def tenantOntologyState : dsl.FocusValue[ TOState ] = dsl.focus( "Tenant Ontology State" ).value( _.obs.getTenantOntologyState )
    def taxonomyState : dsl.FocusValue[ DartTaxonomy ] = dsl.focus( "Current working taxonomy" ).value( _.obs.getTaxonomyState )
    def clusterState : dsl.FocusValue[ DartClusterCurator.ClusterState ] = dsl.focus( "Cluster State" ).value( _.obs.getClusterState )

    // Actions definitions
    def refresh : dsl.Actions = dsl.action( "Refresh saved state keys" )( v => Future( v.obs.refresh() ) )
    def importPublishedLatestVersion( tenant : String ) : dsl.Actions = dsl.action( s"Import latest published ontology version in tenant: $tenant" )( v => Future( v.obs.importPublishedLatestVersion( tenant ) ) )
    def importPublishedVersion( tenant : String, version : Int ) : dsl.Actions = dsl.action( s"Import published ontology version $version in tenant $tenant" )( v => Future( v.obs.importPublishedVersion( tenant, version ) ) )
    def importStagedLatestVersion( tenant : String ) : dsl.Actions = dsl.action( s"Import latest staged ontology version in tenant $tenant" )( v => Future( v.obs.importStagedLatestVersion( tenant ) ) )
    def importStagedVersion( tenant : String, version : Int ) : dsl.Actions = dsl.action( s"Import staged ontology version $version in tenant $tenant" )( v => Future( v.obs.importStagedVersion( tenant, version ) ) )
    def selectKeepClusterState() : dsl.Actions = dsl.action( "Choose to keep current cluster state after import" )( v => Future( v.obs.selectKeepClusterState() ) )
    def selectClearClusterState() : dsl.Actions = dsl.action( "Choose to clear cluster state after import" )( v => Future( v.obs.selectClearClusterState() ) )
    def stageTaxonomyTo( tenant : String ) : dsl.Actions = dsl.action( s"Stage current taxonomy in tenant $tenant" )( v => Future( v.obs.stageTaxonomyTo( tenant ) ) )
    def publishStagedTaxonomy( tenant : String ) : dsl.Actions = dsl.action( s"Publish staged ontology in tenant $tenant" )( v => Future( v.obs.publishStagedTaxonomy( tenant ) ) )
    def confirmStageOrPublish : dsl.Actions = dsl.action( "Confirm staging or publication" )( v => Future( v.obs.confirmStageOrPublish() ) )
    def cancelStageOrPublish : dsl.Actions = dsl.action( "Cancel staging or publication" )( v => Future( v.obs.cancelStageOrPublish() ) )
}
