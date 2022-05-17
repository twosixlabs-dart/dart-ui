package com.twosixtech.dart.taxonomy.explorer.clustering

import better.files.Resource
import com.twosixtech.dart.concepts.clusters.ClusterRequest
import com.twosixtech.dart.taxonomy.explorer.api.{ClusteringApiDI, RootApiDI}
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, CuratedClusterDI, DartClusterConceptBridgeDeps, DartClusterDI, DartTaxonomyDI, NodeSimilarity, TaxonomyIdDeps, TaxonomyIdSerializationDeps, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, WmDartSerializationDI}

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Random

trait ClusteringServiceDeps[ JobID ] {
    this : DartTaxonomyDI
      with ClusteringApiDI =>

    trait ClusteringService {
        def initialClustering( tenant : String ) : Future[ Unit ]

        def recluster( omittedConcepts : Set[ String ], taxonomy : DartTaxonomy, prevJob : Option[ JobID ] ) : Future[ JobID ]

        def clusterResults( jobId : Option[ JobID ] = None ) : Future[ ClusteringApi.ClusterResults ]

        def rescore( jobId : JobID, newTaxonomy : DartTaxonomy ) : Future[ JobID ]

        def rescoreResults( jobId : Option[ JobID ] = None ) : Future[ ClusteringApi.ClusterResults ]
    }

    def ClusteringService : ClusteringService

}

trait TestClusteringServiceDI
  extends ClusteringServiceDeps[ UUID ]
    with DartTaxonomyDI
    with ClusteringApiDI
    with RootApiDI
    with DartClusterDI
    with CuratedClusterDI
    with DartClusterConceptBridgeDeps
    with DartSerializationDeps
    with TaxonomyIdDeps
    with TaxonomyIdSerializationDeps {

    override lazy val ClusteringService : ClusteringService = TestClusteringService

    object TestClusteringService extends ClusteringService {
        val r = new Random()
        private val jobs : mutable.Map[ UUID, Seq[ Cluster ] ] = mutable.Map.empty

        import ClusteringApi._

        private lazy val originalClusters : Future[ Some[ Seq[ Cluster ] ] ] = {
            import upickle.default._

            val offlineResultsJson = Resource.getAsString( "offline-cluster-results.json" )
            val offlineResults : ExtClusterResults = read[ ExtClusterResults ]( offlineResultsJson )
            val clusterSeq = offlineResults.clusters.sortBy( - _.score ).map( cl =>
                Cluster( cl.clusterId, cl.score, cl.recommendedName, cl.phrases, cl.similarConcepts.map( sc => NodeSimilarity( sc.concept, sc.score ) ) )
            )
            Future.successful( Some( clusterSeq ) )
        }

        private def createJob : (UUID, Seq[ Cluster ]) = {
            val newJobId = UUID.randomUUID()
            val clustersLength = Math.abs( r.nextInt( 1000 ) )
            val clusters : Seq[ Cluster ] = 0 to clustersLength map { _ =>
                val words = 0 to 10 map ( _ => r.nextString( 5 ) )
                Cluster( UUID.randomUUID().toString, r.nextDouble(), words.head, words, Nil )
            }
            (newJobId, clusters)
        }

        import scala.concurrent.ExecutionContext.Implicits.global

        override def clusterResults(
            jobId : Option[ UUID ],
        ) : Future[ ClusteringApi.ClusterResults ] = {
            jobId match {
                case None =>
                    originalClusters.map( ( oc ) => ClusteringApi.ClusterResults( None, oc.map( _.map( SingleResult.fromCluster )  ) ) )
                case sId@Some( id ) =>
                    Future.successful( ClusteringApi.ClusterResults( sId, jobs.get( id ).map( _.map( SingleResult.fromCluster ) ) ) )
            }
        }

        override def recluster( omittedConcepts : Set[ String ], taxonomy : DartTaxonomy, prevJob : Option[ UUID ] ) : Future[ UUID ] = {
            val (jobId, clusters) = createJob
            Future {
                Thread.sleep( 60000 )
                jobs( jobId ) = clusters
            }
            Future.successful( jobId )
        }

        override def rescore(
            jobId : UUID,
            newTaxonomy : DartTaxonomy ) : Future[ UUID ] = {
            val (jobId, clusters) = createJob
            Future {
                Thread.sleep( 60000 )
                jobs( jobId ) = clusters
            }
            Future.successful( jobId )
        }

        override def rescoreResults(
            jobId : Option[ UUID ],
        ) : Future[ ClusteringApi.ClusterResults ] =  {
            jobId match {
                case None =>
                    originalClusters.map( ( oc ) => ClusteringApi.ClusterResults( None, oc.map( _.map( SingleResult.fromCluster )  ) ) )
                case sId@Some( id ) =>
                    Future.successful( ClusteringApi.ClusterResults( sId, jobs.get( id ).map( _.map( SingleResult.fromCluster ) ) ) )
            }
        }

        override def initialClustering(
            tenant: String
        ): Future[ Unit ] = Future.successful()

    }

}
