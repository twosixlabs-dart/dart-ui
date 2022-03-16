package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, NodeSimilarity}
import upickle.default.{ReadWriter => RW, _}

object ClusterSerialization {

    implicit val nodeSimilarityRw : RW[ NodeSimilarity ] = macroRW
    implicit val clusterRw : RW[ Cluster ] = macroRW

    implicit class SerializableCluster( cluster : Cluster ) {
        def marshalJson : String = write( cluster )
        def marshalBinary : Array[ Byte ] = writeBinary( cluster )
    }

    implicit class SerializableClusterSeq( clusterSeq : Seq[ Cluster ] ) {
        def marshalJson : String = write( clusterSeq )
        def marshalBinary : Array[ Byte ] = writeBinary( clusterSeq )
    }

    implicit class SerializableConceptSeq( conceptSeq : Seq[ String ] ) {
        def marshalJson : String = write( conceptSeq )
        def marshalBinary : Array[ Byte ] = writeBinary( conceptSeq )
    }

    implicit class DeserializableClusterJson( json : String ) {
        def unmarshalCluster : Cluster = read[ Cluster ]( json )
        def unmarshalClusterSeq : Seq[ Cluster ] = read[ Seq[ Cluster ] ]( json )
        def unmarshalConceptSeq : Seq[ String ] = read[ Seq[ String ] ]( json )
    }

    implicit class DeserializableClusterBinary( binary : Array[ Byte ] ) {
        def unmarshalCluster : Cluster = readBinary[ Cluster ]( binary )
        def unmarshalClusterSeq : Seq[ Cluster ] = readBinary[ Seq[ Cluster ] ]( binary )
        def unmarshalConceptSeq : Seq[ String ] = readBinary[ Seq[ String ] ]( binary )
    }

}
