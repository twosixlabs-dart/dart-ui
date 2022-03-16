package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access

import com.twosixtech.dart.scalajs.backend.HttpBody.{BinaryBody, NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{HttpMethod, HttpResponse}
import com.twosixtech.dart.taxonomy.explorer.models.DartTaxonomyTestDataDI
import teststate.Exports._
import utest._

import scala.concurrent.ExecutionContext


trait StateAccessComponentTest
  extends StateAccessComponentTestConfig
    with DartTaxonomyTestDataDI {

    implicit val ec : ExecutionContext = utest.framework.ExecutionContext.RunNow

    override def tests : Tests = Tests {

        val initialState = StateAccessApi.ConceptsState(
            DartTaxonomyData.taxonomy,
            Some( StateAccessApi.ClusterState( Seq.empty, None ) ),
        )

        val concept1bId = {
            val taxonomy = DartTaxonomyData.taxonomy
            val concept1 = DartTaxonomyData.concept1
            val concept1b = DartTaxonomyData.concept1b
            taxonomy.pathEntry( concept1.name +: concept1b.name +: Nil ).get.id
        }

        val alteredTaxonomy = {
            val taxonomy = DartTaxonomyData.taxonomy
            val concept1 = DartTaxonomyData.concept1
            val concept1b = DartTaxonomyData.concept1b
            taxonomy.removeConcept( concept1.name +: concept1b.name +: Nil ).get
        }

        val alteredState = initialState.copy( taxonomy = alteredTaxonomy )

        import japgolly.univeq.UnivEq.AutoDerive.autoDeriveUnivEq

        test( "make sure test hook is working" ) {
            test( "dispatch" ) {
                Plan.action(
                    dsl.emptyAction
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( "{}" ) ) ) )
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomyData.taxonomy ) )
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomy( Set.empty ) ) )
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomyData.taxonomy ) )
                       +> currentState.assert.equal( initialState )
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( alteredTaxonomy ) )
                       +> currentState.assert.equal( alteredState )
                ).run().map( _.assert() )
            }
        }

        test( "refresh should get new keys" ) {
            Plan.action(
                dsl.emptyAction
                >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( """{"key-1":5}""" ) ) ) )
                >> refresh
                   +> allKeys.assert.equal( "key-1" )
                   +> versionsCount( "key-1" ).assert.contains( 5 )
            ).run().map( _.assert() )
        }

        test( "New key input" ) {
            test( "should be able to set text" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dsl.emptyAction
                       +> newKeyText.assert.equal( "" )
                    >> setKeyText( "new-key" )
                       +> newKeyText.assert.equal( "new-key" )
                ).run().map( _.assert() )
            }

            test( "save as should send request to save state as input text, should set stateid to newest version of that key, and should clear key input" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( DartConceptExplorer.LoadTaxonomy( DartTaxonomyData.taxonomy ) )
                       +> newKeyText.assert.equal( "" )
                       +> currentState.assert.equal( initialState )
                       +> currentStateId.assert.empty
                    >> setKeyText( "new-key" )
                       +> newKeyText.assert.equal( "new-key" )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( "1" ) ) ) { ( method, req ) =>
                        assert( method == HttpMethod.Post )
                        assert( req.url.contains( s"${StateAccessApi.PATH}/new-key" ) )
                        req.body match {
                            case TextBody( text ) =>
                                import StateAccessApi.DeserializableConceptsStateJson
                                val state = text.unmarshalConceptsState
                                assert( state.taxonomy == DartTaxonomyData.taxonomy )
                            case BinaryBody( data ) =>
                                import StateAccessApi.DeserializableConceptsStateBinary
                                val state = data.unmarshalConceptsState
                                assert( state.taxonomy == DartTaxonomyData.taxonomy )
                            case _ => throw AssertionError( "Unexpected request body", Nil )
                        }
                    } )
                    >> saveCurrentStateToNewKey
                       +> allKeys.assert.equal( "new-key" )
                       +> versionsCount( "new-key" ).assert.contains( 1 )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "new-key", 1 ) )
                       +> currentState.assert.equal( initialState )
                       +> newKeyText.assert.equal( "" )
                    >> setKeyText( "another-key" )
                       +> newKeyText.assert.equal( "another-key" )
                    >> dispatch( DartConceptExplorer.RemoveConcept( concept1bId ) )
                       +> currentState.assert.equal( alteredState )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( "4" ) ) ) { ( method, req ) =>
                        assert( method == HttpMethod.Post )
                        assert( req.url.contains( s"${StateAccessApi.PATH}/another-key" ) )
                        req.body match {
                            case TextBody( text ) =>
                                import StateAccessApi.DeserializableConceptsStateJson
                                val state = text.unmarshalConceptsState
                                assert( state.taxonomy == alteredTaxonomy )
                            case BinaryBody( data ) =>
                                import StateAccessApi.DeserializableConceptsStateBinary
                                val state = data.unmarshalConceptsState
                                assert( state.taxonomy == alteredTaxonomy )
                            case _ => throw AssertionError( "Unexpected request body", Nil )
                        }
                    } )
                    >> saveCurrentStateToNewKey
                       +> allKeys.assert.equal( "new-key", "another-key" )
                       +> versionsCount( "new-key" ).assert.contains( 1 )
                       +> versionsCount( "another-key" ).assert.contains( 4 )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "another-key", 4 ) )
                       +> currentState.assert.equal( alteredState )
                ).run().map( _.assert() )
            }
        }

        test( "Version selection" ) {
            test( "should be able to select a key, which sends backend request for latest version, and selects latest version on success" ) {
                Plan.action(
                    dsl.emptyAction
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( """{"key-2":5}""" ) ) ) )
                       +> currentStateId.assert.empty
                    >> refresh
                       +> allKeys.assert.equal( "key-2" )
                       +> versionsCount( "key-2" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( alteredState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/key-2/5" ) )
                            assert( request.body == NoBody )
                    } )
                    >> chooseKey( "key-2" )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "key-2", 5 ) )
                ).run().map( _.assert() )
            }

            test( "should be able to select a version of a key, which sends backend request for that version, and selects that version on success" ) {
                Plan.action(
                    dsl.emptyAction
                    >> setBackendResponse( _.setStatic( HttpResponse( Map.empty, 200, TextBody( """{"key-2":5}""" ) ) ) )
                       +> currentStateId.assert.empty
                    >> refresh
                       +> allKeys.assert.equal( "key-2" )
                       +> versionsCount( "key-2" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/key-2/2" ) )
                            assert( request.body == NoBody )
                    } )
                    >> chooseVersion( "key-2", 2 )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "key-2", 2 ) )
                       +> currentState.assert.equal( initialState )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( alteredState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/key-2/4" ) )
                            assert( request.body == NoBody )
                    } )
                    >> chooseVersion( "key-2", 4 )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "key-2", 4 ) )
                       +> currentState.assert.equal( alteredState )
                ).run().map( _.assert() )
            }
        }

        test( "Version navigation" ) {
            test( "Navigating to previous version should send a request for that version, update the state based on the returned state, and set stateid to that key/version" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( StateAccessComponent.UpdateAndSwitchToStateId( "test-key", 5, 3 ) )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 3 ) )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/2" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoPreviousState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 2 ) )
                       +> currentState.assert.equal( initialState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( alteredState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/1" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoPreviousState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 1 ) )
                       +> currentState.assert.equal( alteredState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                ).run().map( _.assert() )
            }

            test( "Navigating to next version should send a request for that version, update the state based on the returned state, and set stateid to that key/version" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( StateAccessComponent.UpdateAndSwitchToStateId( "test-key", 5, 3 ) )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 3 ) )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/4" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoNextState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 4 ) )
                       +> currentState.assert.equal( initialState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( alteredState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/5" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoNextState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 5 ) )
                       +> currentState.assert.equal( alteredState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                ).run().map( _.assert() )
            }

            test( "Navigating to oldest version should send a request for that version, update the state based on the returned state, and set stateid to that key/version" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( StateAccessComponent.UpdateAndSwitchToStateId( "test-key", 5, 3 ) )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 3 ) )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/1" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoOldestState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 1 ) )
                       +> currentState.assert.equal( initialState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                ).run().map( _.assert() )
            }

            test( "Navigating to latest version should send a request for that version, update the state based on the returned state, and set stateid to that key/version" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( StateAccessComponent.UpdateAndSwitchToStateId( "test-key", 5, 2 ) )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 2 ) )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/5" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoLatestState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 5 ) )
                       +> currentState.assert.equal( initialState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                ).run().map( _.assert() )
            }

            test( "Navigating to latest version should send a request for that version, update the state based on the returned state, and set stateid to that key/version" ) {
                Plan.action(
                    dsl.emptyAction
                    >> dispatch( StateAccessComponent.UpdateAndSwitchToStateId( "test-key", 5, 2 ) )
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 2 ) )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                    >> setBackendResponse( _.setStaticAndHandle( HttpResponse( Map.empty, 200, TextBody( initialState.marshalJson ) ) ) {
                        ( method, request ) =>
                            assert( method == HttpMethod.Get )
                            assert( request.url.endsWith( StateAccessApi.PATH + "/test-key/5" ) )
                            assert( request.body == NoBody )
                    } )
                    >> gotoLatestState
                       +> currentStateId.assert.contains( StateAccessComponent.StateId( "test-key", 5 ) )
                       +> currentState.assert.equal( initialState )
                       +> allKeys.assert.equal( "test-key" )
                       +> versionsCount( "test-key" ).assert.contains( 5 )
                ).run().map( _.assert() )
            }
        }
    }
}
