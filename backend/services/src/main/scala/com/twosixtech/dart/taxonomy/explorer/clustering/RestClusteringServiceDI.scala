package com.twosixtech.dart.taxonomy.explorer.clustering

import com.twosixtech.dart.concepts.client.DartClusteringClient
import com.twosixtech.dart.concepts.client.DartClusteringClient.Job
import com.twosixtech.dart.concepts.models.{SimilarConcept, SingleResult}
import com.twosixtech.dart.taxonomy.explorer.api.{ClusteringApiDI, RootApiDeps}
import com.twosixtech.dart.taxonomy.explorer.models._
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, OntologyWriterDeps}
import upickle.default._
import upickle.implicits.key

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class ExtDartReclusterRequest(
    @key( "phrases" ) phrases : Seq[ String ],
    @key( "ontology" ) ontology : String,
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtDartReclusterRequest {
    implicit lazy val rw : ReadWriter[ ExtDartReclusterRequest ] = macroRW[ ExtDartReclusterRequest ]
}

case class ExtDartRescoreRequest(
    @key( "ontology" ) ontology : String,
    @key( "cluster_job_id" ) clusterJobId : String,
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtDartRescoreRequest {
    implicit lazy val rw : ReadWriter[ ExtDartRescoreRequest ] = macroRW[ ExtDartRescoreRequest ]
}

case class ExtPollResponse(
    @key( "job_id" ) jobId : String,
    @key( "complete" ) complete : Boolean,
    @key( "message" ) message : String,
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtPollResponse {
    implicit lazy val rw : ReadWriter[ ExtPollResponse ] = macroRW[ ExtPollResponse ]
}

case class ExtClusterResults(
    @key( "job_id" ) jobId : String,
    @key( "clusters" ) clusters : Seq[ ExtSingleResult ],
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtClusterResults {
    implicit lazy val rw : ReadWriter[ ExtClusterResults ] = macroRW[ ExtClusterResults ]
}

case class ExtSimilarConcept(
    @key( "concept" ) concept : Seq[ String ],
    @key( "score" ) score : Double,
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtSimilarConcept {
    implicit lazy val rw : ReadWriter[ ExtSimilarConcept ] = macroRW[ ExtSimilarConcept ]
}

case class ExtSingleResult(
    @key( "cluster_id" ) clusterId : String,
    @key( "score" ) score : Double,
    @key( "recommended_name" ) recommendedName : String,
    @key( "phrases" ) phrases : Seq[ String ],
    @key( "similar_concepts" ) similarConcepts : Seq[ ExtSimilarConcept ],
) {
    def marshalJson : String = write( this )
    def marshalBinary : Array[ Byte ] = writeBinary( this )
}

object ExtSingleResult {
    implicit lazy val rw : ReadWriter[ ExtSingleResult ] = macroRW[ ExtSingleResult ]
}


trait RestClusteringServiceDI
  extends ClusteringServiceDeps[ UUID ]
    with DartTaxonomyDI
    with RootApiDeps
    with DartClusterDI
    with CuratedClusterDI
    with DartClusterConceptBridgeDeps
    with DartSerializationDeps
    with TaxonomyIdDeps
    with TaxonomyIdSerializationDeps
    with OntologyWriterDeps
    with ClusteringApiDI {

    class RestClusteringService( clusteringClient: DartClusteringClient )( implicit ec : ExecutionContext )
      extends ClusteringService {

        def convertSimilarConcept( sc : SimilarConcept ) : ClusteringApi.SimilarConcept =
            ClusteringApi.SimilarConcept(
                sc.concept,
                sc.score,
            )

        def convertClusterResult( clientRes : SingleResult ) : ClusteringApi.SingleResult =
            ClusteringApi.SingleResult(
                clientRes.clusterId,
                clientRes.score,
                clientRes.recommendedName,
                clientRes.phrases,
                clientRes.similarConcepts.map( convertSimilarConcept ),
            )

        val DEFAULT_JOB_ID : UUID =
            UUID.fromString( "00000000-0000-0000-0000-000000000000" )

        override def clusterResults( jobId : Option[ UUID ] ) : Future[ ClusteringApi.ClusterResults ] = {
            val job = jobId.getOrElse( DEFAULT_JOB_ID )

            for {
                pollRes <- clusteringClient.pollRecluster( job.toString )
                finalResult <- pollRes match {
                    case DartClusteringClient.Pending =>
                        Future.successful( ClusteringApi.ClusterResults( jobId, None ) )
                    case DartClusteringClient.Failed( message ) =>
                        Future.failed( new Exception( message ) )
                    case DartClusteringClient.Succeeded =>
                        clusteringClient.reclusterResults( job.toString ) map { res =>
                            ClusteringApi.ClusterResults(
                                jobId,
                                Some( res.map( convertClusterResult ) )
                            )
                        }
                }
            } yield finalResult
        }

        override def recluster( omittedConcepts : Set[ String ], taxonomy : DartTaxonomy, prevJob : Option[ UUID ] ) : Future[ UUID ] = {
            val taxonomyYml = OntologyWriter.taxonomyYaml( taxonomy )

            clusteringClient
              .recluster(
                  omittedConcepts.toSeq, taxonomyYml, Job( prevJob
                    .getOrElse( DEFAULT_JOB_ID )
                    .toString
                  )
              )
              .map( v => UUID.fromString( v.id ) )
        }

        override def rescore( jobId : UUID, newTaxonomy : DartTaxonomy ) : Future[ UUID ] = {
            val taxonomyYml = OntologyWriter.taxonomyYaml( newTaxonomy )

            clusteringClient.rescore( taxonomyYml, jobId.toString )
              .map( v => {
                  val uuid = UUID.fromString( v.id )
                  uuid
              }
              )
        }

        override def rescoreResults( jobId : Option[ UUID ] ) : Future[ ClusteringApi.ClusterResults ] = {
            val job = jobId.getOrElse( DEFAULT_JOB_ID )

            for {
                pollRes <- clusteringClient.pollRescore( job.toString )
                finalResult <- pollRes match {
                    case DartClusteringClient.Pending =>
                        Future.successful( ClusteringApi.ClusterResults( jobId, None ) )
                    case DartClusteringClient.Failed( message ) =>
                        Future.failed( new Exception( message ) )
                    case DartClusteringClient.Succeeded =>
                        clusteringClient.rescoreResults( job.toString ) map { res =>
                            ClusteringApi.ClusterResults(
                                jobId,
                                Some( res.map( convertClusterResult ) )
                            )
                        }
                }
            } yield finalResult
        }

        override def initialClustering(
            tenant : String
        ) : Future[ Unit ] = {
            clusteringClient.initialClustering( tenant )
        }
    }
}
