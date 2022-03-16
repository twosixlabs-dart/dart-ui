package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, NodeSimilarity}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class ClusterSerializationTest extends AnyFlatSpecLike with Matchers {

    behavior of "ClusterSerialization"

    val cluster1 = Cluster( "test-id-1", 0.234231, "test-name", Seq( "test-name", "test-name-2", "test-name-3", "test-name-4" ), Seq() )
    val cluster2 = Cluster( "test-id-2", 0.9999, "test-word", Seq( "test-word", "test-word-2", "test-word-3", "test-word-4" ), Seq( NodeSimilarity( Seq( "test-path-1", "test-path-2" ), 0.243845 ), NodeSimilarity( Seq( "test-path-a", "test-path-b" ), 0.00023432 ) ) )

    it should "serialize and deserialize a cluster" in {
        import ClusterSerialization._

        cluster1.marshalJson.unmarshalCluster shouldBe cluster1
        cluster1.marshalBinary.unmarshalCluster shouldBe cluster1
    }

    it should "serialize and deserialize a sequence of clusters" in {
        import ClusterSerialization._

        Seq( cluster1, cluster2 ).marshalJson.unmarshalClusterSeq shouldBe Seq( cluster1, cluster2 )
        Seq( cluster1, cluster2 ).marshalBinary.unmarshalClusterSeq shouldBe Seq( cluster1, cluster2 )
    }

}
