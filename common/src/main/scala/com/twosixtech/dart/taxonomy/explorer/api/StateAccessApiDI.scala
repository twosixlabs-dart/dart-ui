package com.twosixtech.dart.taxonomy.explorer.api

import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterDI, DartTaxonomyDI, TaxonomyIdDeps, TaxonomyIdSerializationDeps, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps
import upickle.default.{ReadWriter => RW, _}

import java.util.UUID

trait StateAccessApiDI {
    this : DartTaxonomyDI
      with CuratedClusterDI
      with DartSerializationDeps
      with TaxonomyIdDeps
      with TaxonomyIdSerializationDeps
      with RootApiDeps
      with DartClusterDI =>

    import DartSerialization.{DeserializableJson, SerializableTaxonomy}

    object StateAccessApi {

        val ENDPOINT : String = "/state"

        def PATH : String = RootApi.BASE_PATH + ENDPOINT


        case class ClusterState(
            clusters : Seq[ CuratedCluster ],
            currentJob : Option[ UUID ],
            activeCluster : Int = 0,
        )

        case class ConceptsState(
            taxonomy : DartTaxonomy,
            clusterState : Option[ ClusterState ],
        )

        case class DartNodeSimilarityDto(
            conceptId : String,
            score : Double,
        ) {
            import TaxonomyIdSerialization.DeserializableJson
            def toDartNodeSimilarity : DartNodeSimilarity = DartNodeSimilarity(
                conceptId.unmarshalTaxonomyId,
                score
            )
        }

        object DartNodeSimilarityDto {
            implicit val rw : RW[ DartNodeSimilarityDto ] = macroRW

            import TaxonomyIdSerialization.SerializableTaxonomyId
            def fromDartNodeSimilarity( dartNodeSimilarity : DartNodeSimilarity ) : DartNodeSimilarityDto = DartNodeSimilarityDto(
                dartNodeSimilarity.conceptId.marshalJson,
                dartNodeSimilarity.score,
            )
        }

        case class DartClusterDto(
            id : String,
            score : Double,
            recommendedName : String,
            rankedWords : Seq[ String ],
            similarNodes : Seq[ DartNodeSimilarityDto ]
        ) {
            def toDartCluster : DartCluster = DartCluster(
                id,
                score,
                recommendedName,
                rankedWords,
                similarNodes.map( _.toDartNodeSimilarity )
            )
        }

        object DartClusterDto {
            implicit val rw : RW[ DartClusterDto ] = macroRW

            def fromDartCluster( dartCluster : DartCluster ) : DartClusterDto = DartClusterDto(
                dartCluster.id,
                dartCluster.score,
                dartCluster.recommendedName,
                dartCluster.rankedWords,
                dartCluster.similarNodes.map( DartNodeSimilarityDto.fromDartNodeSimilarity ),
            )
        }

        case class CuratedClusterDto(
            cluster : DartClusterDto,
            acceptedPhrases : Map[ String, String ],
            rejectedPhrases : Map[ String, Option[ String ] ],
            selectedPhrases : Set[ String ],
            currentTarget : Option[ String ],
            selectedName : Option[ String ],
        ) {
            import TaxonomyIdSerialization.DeserializableJson
            def toCuratedCluster : CuratedCluster = CuratedCluster(
                cluster.toDartCluster,
                acceptedPhrases.mapValues( _.unmarshalTaxonomyId ),
                rejectedPhrases.mapValues( _.map( _.unmarshalTaxonomyId ) ),
                selectedPhrases,
                currentTarget.map( _.unmarshalTaxonomyId ),
                selectedName,
            )
        }

        object CuratedClusterDto {
            implicit val rw : RW[ CuratedClusterDto ] = macroRW

            import TaxonomyIdSerialization.SerializableTaxonomyId
            def fromCuratedCluster( curatedCluster : CuratedCluster ) : CuratedClusterDto = CuratedClusterDto(
                DartClusterDto.fromDartCluster( curatedCluster.cluster ),
                curatedCluster.acceptedPhrases.mapValues( _.marshalJson ),
                curatedCluster.rejectedPhrases.mapValues( _.map( _.marshalJson ) ),
                curatedCluster.selectedPhrases,
                curatedCluster.currentTarget.map( _.marshalJson ),
                curatedCluster.selectedName,
            )
        }

        case class ClusterStateDto(
            clusters : Seq[ CuratedClusterDto ],
            jobId : Option[ UUID ],
            activeCluster : Int = 0,
        ) {
            def toClusterState : ClusterState = ClusterState(
                clusters.map( _.toCuratedCluster ),
                jobId,
                activeCluster,
            )
        }

        object ClusterStateDto {
            implicit val rw : RW[ ClusterStateDto ] = macroRW

            def fromClusterState( clusterState : ClusterState ) : ClusterStateDto = ClusterStateDto(
                clusterState.clusters.map( CuratedClusterDto.fromCuratedCluster ),
                clusterState.currentJob,
                clusterState.activeCluster,
            )
        }

        case class ConceptsStateDto(
            taxonomy : String,
            clusterState : Option[ ClusterStateDto ],
        ) {
            def toConceptsState : ConceptsState = ConceptsState(
                taxonomy.unmarshalTaxonomy,
                clusterState.map( _.toClusterState ),
            )
        }

        object ConceptsStateDto {
            implicit val rw : RW[ ConceptsStateDto ] = macroRW

            def fromConceptState( conceptsState : ConceptsState ) : ConceptsStateDto = {
                ConceptsStateDto(
                    conceptsState.taxonomy.marshalJson,
                    conceptsState.clusterState.map( ClusterStateDto.fromClusterState ),
                )
            }
        }

        implicit class SerializableConceptsState( conceptsState : ConceptsState ) {
            def marshalJson : String = write( ConceptsStateDto.fromConceptState( conceptsState ) )( ConceptsStateDto.rw )
            def marshalBinary : Array[ Byte ] = writeBinary( ConceptsStateDto.fromConceptState( conceptsState ) )( ConceptsStateDto.rw )
        }

        implicit class DeserializableConceptsStateJson( json : String ) {
            def unmarshalConceptsState : ConceptsState =
                read[ ConceptsStateDto ]( json )( ConceptsStateDto.rw ).toConceptsState
        }

        implicit class DeserializableConceptsStateBinary( binary : Array[ Byte ] ) {
            def unmarshalConceptsState : ConceptsState =
                readBinary[ ConceptsStateDto ]( binary )( ConceptsStateDto.rw ).toConceptsState
        }

    }
}

