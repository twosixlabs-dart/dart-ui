package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology

import com.twosixtech.dart.scalajs.backend.HttpBody.{NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{HttpMethod, HttpRequest, HttpResponse}
import com.twosixtech.dart.taxonomy.explorer.models.DartTaxonomyTestDataDI
import teststate.Exports._
import utest.{Tests, test}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

trait TenantOntologyComponentTest
  extends TenantOntologyComponentTestConfig
    with DartTaxonomyTestDataDI {

    import japgolly.univeq.UnivEq.AutoDerive.autoDeriveUnivEq

    implicit def taxEq : Equal[ DartTaxonomy ] = new Equal[ DartTaxonomy ]( (a, b) => a == b )
    implicit def clsEq : Equal[ DartClusterCurator.ClusterState ] = new Equal[ DartClusterCurator.ClusterState ]( (a, b) => a == b )

    val testTOState = TenantOntologyComponent.State(
        Map( "test-tenant" -> TenantVersion( Some( 3 ), Some( 2 ) ) ),
    )

    val testTenantStateMap = Map(
        "test-tenant-1" -> TenantVersion( Some( 3 ), Some( 2 ) ),
        "test-tenant-2" -> TenantVersion( None, Some( 5 ) ),
        "test-tenant-3" -> TenantVersion( Some( 1 ), None ),
        "test-tenant-4" -> TenantVersion( None, None ),
        "test-tenant-5" -> TenantVersion( Some( 20 ), Some( 6000 ) ),
    )

    override def tests : Tests = Tests {
        test( "make sure test hook is working" ) {
            test( "dispatch" ) {
                Plan.action(
                    dsl.emptyAction
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( "{}" ) ) ) )
                       +> tenantOntologyState.assert.equal( TenantOntologyComponent.State() )
                    >> dispatch( TenantOntologyComponent.RefreshTenantOntologiesMap( testTOState.tenantOntologies ) )
                       +> tenantOntologyState.assert.equal( testTOState )
                ).run().map( _.assert() )
            }
        }

        test( "Tenant ontology state" ) {
            test( "should be reflected in displayed published and staged ontology versions" ) {
                Plan.action(
                    dsl.emptyAction
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( "{}" ) ) ) )
                       +> tenantOntologyState.assert.equal( TenantOntologyComponent.State() )
                       +> allTenants.assert.equal()
                       +> tenantOntologyContents( "test-tenant-1" ).assert.empty
                       +> tenantOntologyContents( "test-tenant-2" ).assert.empty
                       +> tenantOntologyContents( "test-tenant-3" ).assert.empty
                       +> tenantOntologyContents( "test-tenant-4" ).assert.empty
                       +> tenantOntologyContents( "test-tenant-5" ).assert.empty
                    >> dispatch( TenantOntologyComponent.RefreshTenantOntologiesMap(
                        testTenantStateMap,
                    ) )
                       +> tenantOntologyState.assert.equal( TenantOntologyComponent.State( testTenantStateMap ) )
                       +> allTenants.assert.equal( testTenantStateMap.keys.toSeq : _* )
                       +> tenantOntologyContents( "test-tenant-1" ).assert.contains( TOContents( true, true ) )
                       +> tenantOntologyContents( "test-tenant-2" ).assert.contains( TOContents( false, true ) )
                       +> tenantOntologyContents( "test-tenant-3" ).assert.contains( TOContents( true, false ) )
                       +> tenantOntologyContents( "test-tenant-4" ).assert.contains( TOContents( false, false ) )
                       +> tenantOntologyContents( "test-tenant-5" ).assert.contains( TOContents( true, true ) )
                ).run().map( _.assert() )
            }
        }

        test( "importing" ) {

            def importPlan(
                clusterStateInitializer : DartClusterCurator.ClusterAction,
                initialClusterStateShouldBe : DartClusterCurator.ClusterState,
                importAction: dsl.Actions,
                importMenuAction : dsl.Actions,
                clusterStateShouldBe : DartClusterCurator.ClusterState,
            )( assertOnReq : (HttpMethod, HttpRequest) => Unit ) : dsl.Plan = {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( clusterStateInitializer )
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( "{}" ) ) ) )
                    >> dispatch( TenantOntologyComponent.RefreshTenantOntologiesMap(
                        testTenantStateMap,
                    ) )
                       +> tenantOntologyState.assert.equal( TenantOntologyComponent.State( testTenantStateMap ) )
                       +> taxonomyState.assert.equal( DartTaxonomy( Set.empty ) )
                       +> clusterState.assert.equal( initialClusterStateShouldBe )
                       +> importMenuIsVisible.assert.equal( false )
                    >> setBackendResponse( _.setStaticAndHandle(
                        HttpResponse(
                            Map( "Content-Type" -> "application/json" ),
                            200,
                            TextBody( DartTaxonomyData.taxonomy.retrievedTaxonomyJson ),
                        ),
                    )( assertOnReq ) )
                    >> importAction
                       +> importMenuIsVisible.assert.equal( true )
                    >> importMenuAction
                       +> importMenuIsVisible.assert.equal( false )
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> clusterState.assert.equal( clusterStateShouldBe )
                )
            }

            test( "latest published version, keeping current cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importPublishedLatestVersion( "test-tenant-1" ),
                    importMenuAction = selectKeepClusterState(),
                    clusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveEndpoint( "test-tenant-1" ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "latest published version, clearing cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importPublishedLatestVersion( "test-tenant-1" ),
                    importMenuAction = selectClearClusterState(),
                    clusterStateShouldBe = DartClusterCurator.NoClusterState,
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveEndpoint( "test-tenant-1" ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "specific published version, clearing cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importPublishedVersion( "test-tenant-1", 2 ),
                    importMenuAction = selectClearClusterState(),
                    clusterStateShouldBe = DartClusterCurator.NoClusterState,
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveEndpoint( "test-tenant-1", Some( 2 ) ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "specific published version, keeping current cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importPublishedVersion( "test-tenant-1", 2 ),
                    importMenuAction = selectKeepClusterState(),
                    clusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveEndpoint( "test-tenant-1", Some( 2 ) ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "latest staged version, keeping current cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importStagedLatestVersion( "test-tenant-1" ),
                    importMenuAction = selectKeepClusterState(),
                    clusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveStagedEndpoint( "test-tenant-1" ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "latest staged version, clearing cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importStagedLatestVersion( "test-tenant-1" ),
                    importMenuAction = selectClearClusterState(),
                    clusterStateShouldBe = DartClusterCurator.NoClusterState,
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveStagedEndpoint( "test-tenant-1" ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "specific staged version, keeping current cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importStagedVersion( "test-tenant-1", 1 ),
                    importMenuAction = selectKeepClusterState(),
                    clusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveStagedEndpoint( "test-tenant-1", Some( 1 ) ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }

            test( "specific staged version, clearing cluster state" ) {
                val testId = UUID.randomUUID()
                importPlan(
                    clusterStateInitializer = DartClusterCurator.ReclusterSubmitted( testId ),
                    initialClusterStateShouldBe = DartClusterCurator.ReclusterPending( Vector.empty, testId ),
                    importAction = importStagedVersion( "test-tenant-1", 1 ),
                    importMenuAction = selectClearClusterState(),
                    clusterStateShouldBe = DartClusterCurator.NoClusterState,
                )( ( method, req ) => {
                    assert( method == HttpMethod.Get )
                    assert( req.url.startsWith( retrieveStagedEndpoint( "test-tenant-1", Some( 1 ) ) ) )
                    assert( req.body == NoBody )
                } ).run().map( _.assert() )
            }
        }

        test( "staging" ) {
            test( "if staging is confirmed, it should submit a staging request with the current taxonomy, and update the number of staging versions to the number in the response (it should leave published version count alone)" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomyData.taxonomy ) )
                    >> dispatch( TenantOntologyComponent.RefreshTenantOntologiesMap(
                        testTenantStateMap,
                    ) )
                    >> setBackendResponse( _.setStaticAndHandle(
                        HttpResponse(
                            Map( "Content-Type" -> "application/json" ),
                            201,
                            TextBody( "8" ),
                        ),
                    )( ( method, req ) => {
                        assert( method == HttpMethod.Post )
                        assert( req.url.startsWith( submitStagedEndpoint( "test-tenant-1" ) ) )
                        req.body match { case TextBody( res ) => assert( res.parseStageRequest == DartTaxonomyData.taxonomy ) }
                    } ) )
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> confirmationMenuIsVisible.assert.equal( false )
                    >> stageTaxonomyTo( "test-tenant-1" )
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> confirmationMenuIsVisible.assert.equal( true )
                    >> confirmStageOrPublish
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> confirmationMenuIsVisible.assert.equal( false )
                       +> tenantOntologyState.test( "should be updated with new published versions" )( v => {
                        val toState = v.tenantOntologies( "test-tenant-1" )
                        toState.stagedVersion.contains( 8 ) && toState.publishedVersion.contains( 3 )
                    } )
                ).run().map( _.assert() )
            }
        }

        test( "publishing" ) {
            test( "if publication is confirmed it should submit a publication request and update the number of published versions to the number in the response, and should set the number of staged versions to 0" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomyData.taxonomy ) )
                    >> dispatch( TenantOntologyComponent.RefreshTenantOntologiesMap(
                        testTenantStateMap,
                    ) )
                    >> setBackendResponse( _.setStaticAndHandle(
                        HttpResponse(
                            Map( "Content-Type" -> "application/json" ),
                            201,
                            TextBody( "8" ),
                        ),
                    )( ( method, req ) => {
                        assert( method == HttpMethod.Post )
                        assert( req.url.startsWith( publishEndpoint( "test-tenant-1" ) ) )
                        assert( req.body == NoBody )
                    } ) )
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy ) // irrelevant
                       +> confirmationMenuIsVisible.assert.equal( false )
                    >> publishStagedTaxonomy( "test-tenant-1" )
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> confirmationMenuIsVisible.assert.equal( true )
                    >> confirmStageOrPublish
                       +> taxonomyState.assert.equal( DartTaxonomyData.taxonomy )
                       +> confirmationMenuIsVisible.assert.equal( false )
                       +> tenantOntologyState.test( "should be updated with new published versions" )( v => {
                        val toState = v.tenantOntologies( "test-tenant-1" )
                        toState.stagedVersion.isEmpty && toState.publishedVersion.contains( 8 )
                    } )
                ).run().map( _.assert() )
            }
        }
    }

}
