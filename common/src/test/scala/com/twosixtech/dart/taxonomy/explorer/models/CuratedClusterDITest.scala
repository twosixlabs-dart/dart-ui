package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Positive, WmConceptMetadata, WmDartClusterConceptBridgeDI}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class CuratedClusterDITest
  extends AnyFlatSpecLike
    with Matchers
    with WmDartConceptDI
    with CuratedClusterDI
    with DartClusterDI
    with DartTaxonomyDI
    with UUIDTaxonomyIdDI
    with WmDartClusterConceptBridgeDI {

    behavior of "ClusterCuration.reclusterablePhrases"

    val testId = generateTaxonomyId()
    val testConcept = DartConcept(
        "test concept",
        Set.empty,
        Some( WmConceptMetadata(
            Set( "3 word 1", "3 word 2", "4 word 2" ),
            Set.empty,
            Seq.empty,
            Seq.empty,
            Positive,
            Entity,
        ) ),
    )

    val taxonomy = DartTaxonomy(
        Set( testConcept ),
        Map( testId -> DartTaxonomyEntry( testId, Seq( testConcept.name ), testConcept ) ),
    )

    val testCluster1 = DartCluster( "test cluster 1", 0.987, "1 word 1", Seq( "1 word 1", "1 word 2", "1 word 3", "1 word 4" ), Nil )
    val testCluster2 = DartCluster( "test cluster 2", 0.987, "2 word 1", Seq( "2 word 1", "2 word 2", "2 word 3", "2 word 4" ), Nil )
    val testCluster3 = DartCluster( "test cluster 3", 0.987, "3 word 1", Seq( "3 word 1", "3 word 2", "3 word 3", "3 word 4" ), Nil )
    val testCluster4 = DartCluster( "test cluster 4", 0.987, "4 word 1", Seq( "4 word 1", "4 word 2", "4 word 3", "4 word 4" ), Nil )

    val cc1 = CuratedCluster( testCluster1, Map.empty, Map.empty, Set.empty, None, None )
    val cc2 = CuratedCluster( testCluster2, Map.empty, testCluster2.rankedWords.map( v => v -> None ).toMap, Set.empty, None, None )
    val cc3 = CuratedCluster( testCluster3, Map( "3 word 1" -> testId, "3 word 2" -> testId ), Map.empty, Set.empty, None, None )
    val cc4 = CuratedCluster( testCluster4, Map( "4 word 2" -> testId ), Map( "4 word 4" -> None, "4 word 3" -> None ), Set.empty, None, None )

    it should "filter out accepted and rejected words (should not necessarily filter out recommended name)" in {
        val clusterSeq = Seq( cc1, cc2, cc3, cc4 )
        val clusterablePhrases = clusterSeq.reclusterablePhrases( taxonomy )
        clusterablePhrases shouldBe Set( "1 word 1", "1 word 2", "1 word 3", "1 word 4", "3 word 3", "3 word 4", "4 word 1" )
    }

    it should "not filter out accepted or rejected phrases if target concept does not exist" in {
        val nonExistentId = generateTaxonomyId()
        val clusterSeq = Seq( CuratedCluster( testCluster1, Map( "1 word 1" -> nonExistentId ), Map( "1 word 2" -> Some( nonExistentId ) ), Set.empty, None, None  ) )
        val clusterablePhrases = clusterSeq.reclusterablePhrases( taxonomy )
        clusterablePhrases shouldBe ( testCluster1.rankedWords.toSet + testCluster1.recommendedName )
    }

    it should "not filter out accepted phrases if target concept does not contain that phrase and should not filter out rejected phrases if target concept does contain that phrase" in {
        val nonExistentId = generateTaxonomyId()
        val clusterSeq = Seq( CuratedCluster( testCluster3, Map( "3 word 3" -> testId ), Map( "3 word 2" -> Some( testId ) ), Set.empty, None, None  ) )
        val clusterablePhrases = clusterSeq.reclusterablePhrases( taxonomy )
        clusterablePhrases shouldBe ( testCluster3.rankedWords.toSet + testCluster3.recommendedName )
    }

}
