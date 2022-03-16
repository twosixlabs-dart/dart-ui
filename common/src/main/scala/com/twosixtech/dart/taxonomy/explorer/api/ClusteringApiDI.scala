package com.twosixtech.dart.taxonomy.explorer.api

import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, CuratedClusterDI, DartClusterDI, DartTaxonomyDI, NodeSimilarity, TaxonomyIdDeps, TaxonomyIdSerializationDeps}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps
import upickle.default.{ReadWriter => RW, _}
import upickle.implicits.key

import java.util.UUID

trait ClusteringApiDI {
    this : DartTaxonomyDI
      with CuratedClusterDI
      with DartSerializationDeps
      with TaxonomyIdDeps
      with TaxonomyIdSerializationDeps
      with RootApiDeps
      with DartClusterDI
      with DartSerializationDeps =>

    object ClusteringApi {
        def PATH : String = RootApi.BASE_PATH + "/cluster"

        def RECLUSTER_PATH : String = PATH + "/recluster"
        def RESCORE_PATH : String = PATH + "/rescore"

        def reclusterSubmitEndpoint : String = RECLUSTER_PATH + "/submit"
        def rescoreSubmitEndpoint : String = RESCORE_PATH + "/submit"

        def reclusterResultsEndpoint : String = RECLUSTER_PATH + "/results"
        def rescoreResultsEndpoint : String = RESCORE_PATH + "/results"

        import TaxonomyIdSerialization._

        case class DiscoveryResults(
            allowed_words : Seq[ String ],
            ontology_metadata : String,
            relevant_doc_uuids : Seq[ String ],
        )

        object DiscoveryResults {
            lazy implicit val rw : RW[ DiscoveryResults ] = macroRW[ DiscoveryResults ]
        }

        case class DartReclusterRequest(
            phrases : Seq[ String ],
            ontologyJson : String,
        ) {
            def marshalJson : String = write( this )
        }

        object DartReclusterRequest {
            lazy implicit val rw : RW[ DartReclusterRequest ] = macroRW[ DartReclusterRequest ]

            def apply(
                phrases : Seq[ String ],
                ontology : DartTaxonomy,
            ) : DartReclusterRequest = DartReclusterRequest( phrases, DartSerialization.taxonomyToJson( ontology ) )
        }

        case class RescoreRequest(
            ontologyJson : String,
            clusterJobId : UUID,
        ) {
            def marshalJson : String = write( this )
        }

        object RescoreRequest {
            lazy implicit val rw : RW[ RescoreRequest ] = macroRW[ RescoreRequest ]

            def apply(
                ontology : DartTaxonomy,
                clusterJobId : UUID,
            ) : RescoreRequest = RescoreRequest( DartSerialization.taxonomyToJson( ontology ), clusterJobId )
        }

        case class ClusterResults(
            jobId : Option[ UUID ],
            clusters : Option[ Seq[ SingleResult ] ],
        ) {
            def marshalJson : String = write( this )
            def marshalBinary : Array[ Byte ] = writeBinary( this )
        }

        object ClusterResults {
            implicit def optionReadWriter[ T: RW ] : RW[Option[ T ] ] = readwriter[ ujson.Value.Value ].bimap[ Option[ T ] ](
                {
                    case Some( value ) => writeJs( value )
                    case None => ujson.Null
                }, {
                    case ujson.Null => None
                    case jsValue => Some( read[ T ]( jsValue ) )
                }
            )

            lazy implicit val rw : RW[ ClusterResults ] = macroRW[ ClusterResults ]
        }

        case class SimilarConcept(
            concept : Seq[ String ],
            score : Double,
        ) {
            def marshalJson : String = write( this )
            def marshalBinary : Array[ Byte ] = writeBinary( this )

            def toSimilarNode : NodeSimilarity = NodeSimilarity( concept, score )
        }

        object SimilarConcept {
            lazy implicit val rw : RW[ SimilarConcept ] = macroRW[ SimilarConcept ]

            def fromNodeSimilarity( nodeSimilarity: NodeSimilarity ) : SimilarConcept = SimilarConcept(
                nodeSimilarity.path,
                nodeSimilarity.score,
            )
        }

        case class SingleResult(
            clusterId : String,
            score : Double,
            recommendedName : String,
            phrases : Seq[ String ],
            similarConcepts : Seq[ SimilarConcept ],
        ) {
            def marshalJson : String = write( this )
            def marshalBinary : Array[ Byte ] = writeBinary( this )

            def toCluster : Cluster = Cluster( clusterId, score, recommendedName, phrases, similarConcepts.map( _.toSimilarNode ) )
        }

        object SingleResult {
            lazy implicit val rw : RW[ SingleResult ] = macroRW[ SingleResult ]

            def fromCluster( cluster : Cluster ) : SingleResult = SingleResult(
                cluster.id,
                cluster.score,
                cluster.recommendedName,
                cluster.rankedWords,
                cluster.similarNodes.map( SimilarConcept.fromNodeSimilarity )
            )
        }

        implicit class DeserializableJson( json : String ) {
            def unmarshalDartReclusterRequest : DartReclusterRequest = read[ DartReclusterRequest ]( json )
            def unmarshalRescoreRequest : RescoreRequest = read[ RescoreRequest ]( json )
            def unmarshalClusterResults : ClusterResults = read[ ClusterResults ]( json )
            def unmarshalSimilarConcept : SimilarConcept = read[ SimilarConcept ]( json )
            def unmarshalSingleResult : SingleResult = read[ SingleResult ]( json )
        }

        implicit class DeserializableBinary( bytes : Array[ Byte ] ) {
            def unmarshalDartReclusterRequest : DartReclusterRequest = readBinary[ DartReclusterRequest ]( bytes )
            def unmarshalRescoreRequest : RescoreRequest = readBinary[ RescoreRequest ]( bytes )
            def unmarshalClusterResults : ClusterResults = readBinary[ ClusterResults ]( bytes )
            def unmarshalSimilarConcept : SimilarConcept = readBinary[ SimilarConcept ]( bytes )
            def unmarshalSingleResult : SingleResult = readBinary[ SingleResult ]( bytes )
        }
    }
}
