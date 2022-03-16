package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, NodeSimilarity}
import teststate.Exports._
import utest._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


trait DartClusterCuratorClusterTest
  extends DartClusterCuratorClusterTestStateConfig {

    override def tests : Tests = Tests {

        test( "Recommended phrases" ) {
            test( "Curating without selection" ) {

                test( "accepting a recommended phrase should make it an accepted phrase" ) {
                    Plan.action(
                        initializeState
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal()
                        >> acceptRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName )
                           +> rejectedPhrases.assert.equal()
                        >> acceptRecommendedPhrase( testPhrase2 )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName, testPhrase2 )
                           +> rejectedPhrases.assert.equal()
                        >> acceptRecommendedPhrase( testPhrase1 )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> rejectedPhrases.assert.equal()
                    ).run().map( _.assert() )
                }

                test( "rejecting a recommended phrase should move it to rejected phrases" ) {
                    Plan.action(
                        initializeState
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal()
                        >> rejectRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName )
                        >> rejectRecommendedPhrase( testPhrase2 )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName, testPhrase2 )
                        >> rejectRecommendedPhrase( testPhrase1 )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                    ).run().map( _.assert() )
                }

                test( "accepting a rejected phrase should make it an accepted phrase" ) {
                    Plan.action(
                        initializeState
                        >> rejectRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName )
                        >> acceptRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName )
                           +> rejectedPhrases.assert.equal()
                    ).run().map( _.assert() )
                }

                test( "rejecting an accepted phrase should make it a rejected phrase" ) {
                    Plan.action(
                        initializeState
                        >> acceptRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName )
                           +> rejectedPhrases.assert.equal()
                        >> rejectRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName )
                    ).run().map( _.assert() )
                }

                test( "restoring an accepted phrase should make neither accepted nor rejected" ) {
                    Plan.action(
                        initializeState
                        >> acceptRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal( testName )
                           +> rejectedPhrases.assert.equal()
                        >> restorePhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal()
                    ).run().map( _.assert() )
                }

                test( "restoring a rejected phrase should make neither accepted nor rejected" ) {
                    Plan.action(
                        initializeState
                        >> rejectRecommendedPhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal( testName )
                        >> restorePhrase( testName )
                           +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                           +> acceptedPhrases.assert.equal()
                           +> rejectedPhrases.assert.equal()
                    ).run().map( _.assert() )
                }

            }

            test( "Curating with selection" ) {
                test( "Selecting and deselecting" ) {

                    test( "selecting one or more phrases should make them show up as selected, whether or not they are accepted or rejected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testName ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName )
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                               +> acceptedPhrases.assert.equal( testPhrase1 )
                               +> rejectedPhrases.assert.equal( testPhrase2 )
                               +> selectedPhrases.assert.equal( testName )
                            >> selectPhrases( Seq( testPhrase1, testPhrase2 ) )
                               +> acceptedPhrases.assert.equal( testPhrase1 )
                               +> rejectedPhrases.assert.equal( testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all should make all selected" ) {
                        Plan.action(
                            initializeState
                            >> selectAllPhrases
                               +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all should make all selected even if some are already selected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testPhrase1 ) )
                              +> selectedPhrases.assert.equal( testPhrase1 )
                            >> selectAllPhrases
                               +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all accepted should make only accepted phrases selected" ) {
                        Plan.action(
                            initializeState
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> acceptRecommendedPhrase( testPhrase2 )
                            >> selectAcceptedPhrases
                               +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> acceptedPhrases.assert.equal( testPhrase1, testPhrase2 )
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all accepted should make only accepted phrases selected, deselecting other selected phrases" ) {
                        Plan.action(
                            initializeState
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> acceptRecommendedPhrase( testPhrase2 )
                            >> selectPhrases( Seq( testName, testPhrase1 ) )
                               +> acceptedPhrases.assert.equal( testPhrase1, testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> selectAcceptedPhrases
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all rejected should make only rejected phrases selected" ) {
                        Plan.action(
                            initializeState
                            >> rejectRecommendedPhrase( testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                            >> selectRejectedPhrases
                               +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testPhrase1, testPhrase2 )
                               +> selectedPhrases.assert.equal( testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all rejected should make only rejected phrases selected, deselecting other selected phrases" ) {
                        Plan.action(
                            initializeState
                            >> rejectRecommendedPhrase( testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                            >> selectPhrases( Seq( testName, testPhrase1 ) )
                               +> rejectedPhrases.assert.equal( testPhrase1, testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> selectRejectedPhrases
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all uncurated should make only uncurated phrases selected" ) {
                        Plan.action(
                            initializeState
                            >> rejectRecommendedPhrase( testName )
                            >> selectUncuratedPhrases
                               +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testName )
                               +> selectedPhrases.assert.equal( testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "selecting all uncurated should make only uncurated phrases selected, deselecting other selected phrases" ) {
                        Plan.action(
                            initializeState
                            >> rejectRecommendedPhrase( testName )
                            >> selectPhrases( Seq( testName, testPhrase1 ) )
                               +> rejectedPhrases.assert.equal( testName )
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> selectUncuratedPhrases
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "clearing selection should make no phrases be selected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testPhrase1 ) )
                               +> selectedPhrases.assert.equal( testPhrase1 )
                            >> clearSelection
                               +> selectedPhrases.assert.equal()
                            >> selectAllPhrases
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                            >> clearSelection
                               +> selectedPhrases.assert.equal()
                        ).run().map( _.assert() )
                    }
                }

                test( "Curating" ) {

                    test( "accepting selection should set all selected phrases to accepted whether they were uncurated, accepted, or rejected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testName ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName )
                            >> acceptSelectedPhrases
                               +> acceptedPhrases.assert.equal( testName )
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName )
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> selectPhrases( Seq( testPhrase1 ) )
                               +> acceptedPhrases.assert.equal( testName, testPhrase1 )
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                            >> selectPhrases( Seq( testPhrase2 ) )
                               +> acceptedPhrases.assert.equal( testName, testPhrase1 )
                               +> rejectedPhrases.assert.equal( testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                            >> acceptSelectedPhrases
                               +> acceptedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "rejecting selection should set all selected phrases to rejected whether they were uncurated, accepted, or rejected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testName ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName )
                            >> rejectSelectedPhrases
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testName )
                               +> selectedPhrases.assert.equal( testName )
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> selectPhrases( Seq( testPhrase1 ) )
                               +> acceptedPhrases.assert.equal( testPhrase1 )
                               +> rejectedPhrases.assert.equal( testName )
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> rejectSelectedPhrases
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testName, testPhrase1 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                            >> selectPhrases( Seq( testPhrase2 ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                            >> rejectSelectedPhrases
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                    test( "restoring selection should set all selected phrases to neither accepted nor rejected whether they were uncurated, accepted, or rejected" ) {
                        Plan.action(
                            initializeState
                            >> selectPhrases( Seq( testName ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName )
                            >> acceptRecommendedPhrase( testPhrase1 )
                            >> selectPhrases( Seq( testPhrase1 ) )
                               +> acceptedPhrases.assert.equal( testPhrase1 )
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> restoreSelectedPhrases
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1 )
                            >> rejectRecommendedPhrase( testPhrase2 )
                            >> selectPhrases( Seq( testPhrase2 ) )
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal( testPhrase2 )
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                            >> restoreSelectedPhrases
                               +> acceptedPhrases.assert.equal()
                               +> rejectedPhrases.assert.equal()
                               +> selectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                        ).run().map( _.assert() )
                    }

                }
            }
        }

        test( "Concept navigation" ) {
            test( "Searching concepts" ) {

                test( "search results should not display if search input is empty" ) {
                    Plan.action(
                        initializeState
                        +> conceptSearchInput.assert.equal( "" )
                        +> searchedConcepts.assert.empty
                    ).run().map( _.assert() )
                }

                test( "changing text in concept search input should update search results" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput(
                            "leaf",
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                           +> conceptSearchInput.assert.equal( "leaf" )
                           +> searchedConcepts.assert.contains(
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                        >> updateConceptSearchInput(
                            "leaf_2",
                            Set( testConcept2 )
                        )
                           +> conceptSearchInput.assert.equal( "leaf_2" )
                           +> searchedConcepts.assert.contains(
                            Set( testConcept2 )
                        )
                    ).run().map( _.assert() )
                }

            }

            test( "Targeting concepts" ) {

                test( "should be able to target recommended concept" ) {
                    Plan.action(
                        initializeState
                           +> recommendedConcepts.assert.equal( testConcept1, testConcept2 )
                           +> target.assert.empty
                        >> targetRecommendedConcept( testConcept2 )
                           +> target.assert.contains( testConcept2 )
                    ).run().map( _.assert() )
                }

                test( "should be able to target search result" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput(
                            "leaf",
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                           +> searchedConcepts.assert.contains(
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                           +> target.assert.empty
                        >> targetSearchedConcept( testConcept1 )
                           +> target.assert.contains( testConcept1 )
                        >> updateConceptSearchInput(
                            "leaf",
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                           +> searchedConcepts.assert.contains(
                            Set( testConcept1, testConcept2, testConcept3 )
                        )
                        >> targetSearchedConcept( testConcept2 )
                           +> target.assert.contains( testConcept2 )
                    ).run().map( _.assert() )
                }

                test( "should be able to target accepted phrase's target concept" ) {
                    Plan.action(
                        initializeState
                           +> target.assert.empty
                        >> targetRecommendedConcept( testConcept1 )
                           +> target.assert.contains( testConcept1 )
                        >> targetRecommendedConcept( testConcept2 )
                           +> target.assert.contains( testConcept2 )
                    ).run().map( _.assert() )
                }

            }

            test( "Default concept" ) {

                test( "creating a default concept should target an expected concept path terminating in recommended name" ) {
                    Plan.action(
                        initializeState
                           +> target.assert.empty
                           +> recommendedName.assert.equal( testName )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", testName ) )
                    ).run().map( _.assert() )
                }

                test( "creating a default concept should not make a new target if no phrases have been added to existing concept" ) {
                    Plan.action(
                        initializeState
                           +> target.assert.empty
                           +> recommendedName.assert.equal( testName )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", testName ) )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", testName ) )
                    ).run().map( _.assert() )
                }

                test( "creating a default concept should create a new target with a name [cluster_name]_1 if default concept has already been generated, and so on" ) {
                    Plan.action(
                        initializeState
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", testName ) )
                        >> acceptRecommendedPhrase( testName )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", s"${testName}_1" ) )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", s"${testName}_1" ) )
                        >> acceptRecommendedPhrase( testPhrase1 )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", s"${testName}_2" ) )
                        >> acceptRecommendedPhrase( testPhrase2 )
                        >> addDefaultTarget
                           +> target.assert.contains( Vector( "clusters", s"${testName}_3" ) )
                        >> updateConceptSearchInput(
                            testName,
                            Set(
                                Vector( "clusters", testName ),
                                Vector( "clusters", s"${testName}_1"),
                                Vector( "clusters", s"${testName}_2"),
                                Vector( "clusters", s"${testName}_3"),
                            )
                        )
                           +> searchedConcepts.assert.contains(
                            Set(
                                Vector( "clusters", testName ),
                                Vector( "clusters", s"${testName}_1"),
                                Vector( "clusters", s"${testName}_2"),
                                Vector( "clusters", s"${testName}_3"),
                            )
                        )
                    ).run().map( _.assert() )
                }

                test( "a default concept should be created and targeted if a phrase is accepted without a target" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput( testName, Set.empty )
                           +> searchedConcepts.assert.contains( Set.empty )
                        >> updateConceptSearchInput( "", Set.empty )
                           +> searchedConcepts.assert.empty
                        >> acceptRecommendedPhrase( testPhrase1 )
                           +> target.assert.contains( Vector( "clusters", testName ) )
                        >> updateConceptSearchInput( testName, Set( Vector( "clusters", testName ) ) )
                           +> searchedConcepts.assert.contains( Set( Vector( "clusters", testName ) ) )
                    ).run().map( _.assert() )
                }

            }

            test( "Accepted phrases' targets" ) {

                test( "a phrase accepted without a target should target a new default concept with recommended name" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput( testName, Set.empty )
                           +> searchedConcepts.assert.contains( Set.empty )
                        >> updateConceptSearchInput( "", Set.empty )
                           +> searchedConcepts.assert.empty
                        >> acceptRecommendedPhrase( testPhrase1 )
                           +> target.assert.contains( Vector( "clusters", testName ) )
                           +> acceptedPhraseTarget( testPhrase1 ).assert.equal( "clusters", testName )
                        >> updateConceptSearchInput( testName, Set( Vector( "clusters", testName ) ) )
                           +> searchedConcepts.assert.contains( Set( Vector( "clusters", testName ) ) )
                    ).run().map( _.assert() )
                }

                test( "a phrase accepted when a concept is targeted from search should target that concept" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput( "leaf", Set( testConcept1, testConcept2, testConcept3 ) )
                           +> searchedConcepts.assert.defined
                        >> targetSearchedConcept( testConcept1 )
                           +> target.assert.contains( testConcept1 )
                        >> acceptRecommendedPhrase( testPhrase1 )
                           +> acceptedPhraseTarget( testPhrase1 ).assert.equal( testConcept1 : _* )
                    ).run().map( _.assert() )
                }

                test( "multiple phrases should be able to target multiple different concepts" ) {
                    Plan.action(
                        initializeState
                        >> updateConceptSearchInput( "leaf", Set( testConcept1, testConcept2, testConcept3 ) )
                           +> searchedConcepts.assert.defined
                        >> targetSearchedConcept( testConcept1 )
                           +> target.assert.contains( testConcept1 )
                        >> acceptRecommendedPhrase( testPhrase1 )
                           +> acceptedPhraseTarget( testPhrase1 ).assert.equal( testConcept1 : _* )
                        >> updateConceptSearchInput( "leaf", Set( testConcept1, testConcept2, testConcept3 ) )
                           +> searchedConcepts.assert.defined
                        >> targetSearchedConcept( testConcept2 )
                           +> target.assert.contains( testConcept2 )
                        >> acceptRecommendedPhrase( testName )
                           +> acceptedPhraseTarget( testPhrase1 ).assert.equal( testConcept1 : _* )
                           +> acceptedPhraseTarget( testName ).assert.equal( testConcept2 : _* )
                        >> updateConceptSearchInput( "leaf", Set( testConcept1, testConcept2, testConcept3 ) )
                           +> searchedConcepts.assert.defined
                        >> targetSearchedConcept( testConcept3 )
                           +> target.assert.contains( testConcept3 )
                        >> acceptRecommendedPhrase( testPhrase2 )
                           +> acceptedPhraseTarget( testPhrase1 ).assert.equal( testConcept1 : _* )
                           +> acceptedPhraseTarget( testName ).assert.equal( testConcept2 : _* )
                           +> acceptedPhraseTarget( testPhrase2 ).assert.equal( testConcept3 : _* )
                    ).run().map( _.assert() )
                }

            }
        }

        test( "Rejecting/Curating cluster" ) {

            test( "rejecting the cluster should make all phrases rejected" ) {
                Plan.action(
                    initializeState
                       +> rejected.assert.equal( false )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal()
                       +> rejectedPhrases.assert.equal()
                    >> setClusterToRejected
                       +> rejected.assert.equal( true )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal(  )
                       +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                ).run().map( _.assert() )
            }

            test( "rejecting the cluster should make all accepted phrases rejected and should clear selection" ) {
                Plan.action(
                    initializeState
                    >> acceptRecommendedPhrase( testName )
                    >> selectPhrases( Seq( testName, testPhrase2 ) )
                       +> acceptedPhrases.assert.equal( testName )
                       +> selectedPhrases.assert.equal( testName, testPhrase2 )
                    >> setClusterToRejected
                       +> rejected.assert.equal( true )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal(  )
                       +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> selectedPhrases.assert.equal()
                ).run().map( _.assert() )
            }

            test( "setting a cluster to curate from rejected should restore all phrases" ) {
                Plan.action(
                    initializeState
                    >> setClusterToRejected
                       +> rejected.assert.equal( true )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal(  )
                       +> rejectedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> selectedPhrases.assert.equal()
                    >> setClusterToCurate
                       +> rejected.assert.equal( false )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal(  )
                       +> rejectedPhrases.assert.equal()
                       +> selectedPhrases.assert.equal()
                ).run().map( _.assert() )
            }

            test( "setting a cluster to curate when it's already set to curate shouldn't do anything" ) {
                Plan.action(
                    initializeState
                    >> acceptRecommendedPhrase( testName )
                    >> selectPhrases( Seq( testName, testPhrase2 ) )
                       +> acceptedPhrases.assert.equal( testName )
                       +> selectedPhrases.assert.equal( testName, testPhrase2 )
                    >> setClusterToCurate
                       +> rejected.assert.equal( false )
                       +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                       +> acceptedPhrases.assert.equal( testName )
                       +> rejectedPhrases.assert.equal()
                       +> selectedPhrases.assert.equal( testName, testPhrase2 )
                ).run().map( _.assert() )
            }

        }

        test( "should filter out invalid similar-nodes" ) {

            Plan.action(
                dispatch( DartConceptExplorer.LoadTaxonomy( taxonomy ) )
                >> dispatch( DartClusterCurator.ClusterResults(
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
                                ),
                                // Invalid similar node
                                NodeSimilarity(
                                    Vector( testBranch1, testLeaf3 ),
                                    0.156,
                                )
                            ),
                        )
                    ),
                ) )
                +> recommendedPhrases.assert.equal( testName, testPhrase1, testPhrase2 )
                +> acceptedPhrases.assert.equal()
                +> rejectedPhrases.assert.equal()
                +> recommendedConcepts.assert.equal( testConcept1, testConcept2 )
            ).run().map( _.assert() )
        }
    }

}
