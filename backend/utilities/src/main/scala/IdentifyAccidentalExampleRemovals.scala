import better.files.File
import com.twosixtech.dart.taxonomy.explorer.api.{RootApiDI, StateAccessApiDI}
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models._
import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI

object IdentifyAccidentalExampleRemovals
  extends StateAccessApiDI
    with DartTaxonomyDI
    with WmDartConceptDI
    with WmDartSerializationDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI
    with RootApiDI
    with CuratedClusterDI
    with DartClusterDI
    with WmDartClusterConceptBridgeDI {

  implicit class Arguments(args: Array[String]) {
    def haveOption(opt: String): Boolean =
      args.exists(_.trim == opt.trim)

    def optionValue(opt: String): Option[String] =
      args.zipWithIndex.find(_._1.trim == opt.trim) flatMap { tup =>
        val (_, i) = tup
        args.lift(i + 1).map(_.trim)
      }
  }

  import StateAccessApi.DeserializableConceptsStateJson

  def main(args: Array[String]): Unit = {
    (for {
      originalStateFile <- args.optionValue("-o")
      updatedStateFile <- args.optionValue("-u")
    } yield (originalStateFile, updatedStateFile)) match {
      case None =>
        println("required arguments: -o [ORIGINAL_STATE_FILE] -u [UPDATED_STATE_FILE]")
      case Some((originalStateFile, updatedStateFile)) =>
        val originalState = File(originalStateFile).contentAsString.unmarshalConceptsState
        val updatedState = File(updatedStateFile).contentAsString.unmarshalConceptsState

        originalState.taxonomy.entries
          .foreach(entry => {
            val id = entry._1
            val originalPath = entry._2.path
            val originalExamples = entry._2.concept.metadata.toList flatMap { metadata =>
              metadata.examples.toList
            }

            updatedState.taxonomy.idEntry(id) match {
              case Some(newEntry) =>
                val newPath = newEntry.path
                val newExamples = newEntry.concept.metadata.toList flatMap { metadata =>
                  metadata.examples.toList
                }

                val removedExamples =
                  originalExamples.filter(ex => !newExamples.contains(ex))

                val removedExamplesCorrelatingWithCluster: List[(String, Int, String)] =
                  removedExamples flatMap { ex =>
                    updatedState.clusterState match {
                      case None =>
                        println("NO CLUSTER STATE!")
                        Nil

                      case Some(StateAccessApi.ClusterState(clusters, _, _)) =>
                        clusters.zipWithIndex.flatMap(tup => {
                          val (cl, i) = tup
                          cl.rejectedPhrases.flatMap(rp => {
                            if (rp._1 == ex) {
                              rp._2 match {
                                case Some(rejectedFromId) if rejectedFromId == id => List((ex, i, cl.cluster.recommendedName))
                                case _ => Nil
                              }
                            } else Nil
                          })
                        })
                    }
                  }

                removedExamplesCorrelatingWithCluster foreach { tup =>
                  val (ex, clusterIndex, clusterName) = tup
                  println(s"${ex} was removed during curation of cluster $clusterIndex ($clusterName)")
                  println(s"\toriginal path: ${originalPath.mkString("/")}")
                  println(s"\tnew path:      ${newPath.mkString("/")}")
                }

              case _ =>
            }
          })
    }
  }

}
