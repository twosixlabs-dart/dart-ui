import better.files.File
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.{OldWmOntologyWriterDI, OntologyReaderDI, WmDartSerializationDI}

object TranslateOntology
  extends OntologyReaderDI
    with OldWmOntologyWriterDI
    with DartTaxonomyDI
    with WmDartConceptDI
    with WmDartSerializationDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI {

  implicit class Arguments(args: Array[String]) {
    def haveOption(opt: String): Boolean =
      args.exists(_.trim == opt.trim)

    def optionValue(opt: String): Option[String] =
      args.zipWithIndex.find(_._1.trim == opt.trim) flatMap { tup =>
        val (_, i) = tup
        args.lift(i + 1).map(_.trim)
      }
  }

  def main(args: Array[String]): Unit = {
    (for {
      inputFile <- args.optionValue("-i")
      outputFile <- args.optionValue("-o")
    } yield (inputFile, outputFile)) match {
      case None =>
        println("required arguments: -i [NEW_ONTOLOGY_FILE_INPUT] -o [OLD_ONTOLOGY_FILE_OUTPUT]")
      case Some((inputFile, outputFile)) =>

        val inputTaxonomy = OntologyReader.ymlToOntology(File(inputFile).contentAsString).get
        val outputTaxonomy = OntologyWriter.taxonomyYaml(inputTaxonomy)
        File(outputFile).write(outputTaxonomy)
    }
  }

}
