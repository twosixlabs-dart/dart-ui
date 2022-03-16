package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Event, Negative, Positive, Property, WmConceptMetadata}
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.WmOntologyYmlJson.{WmOntologyData, WmOntologyNode}
import io.circe.Json
import io.circe.yaml.parser

import scala.util.Try

trait OntologyReaderDeps {
    this : DartTaxonomyDI =>

    trait OntologyReader {
        def ymlToOntology( yml : String ) : Try[ DartTaxonomy ]

        object Implicits {

            implicit class ReadableTaxonomyYml( yml : String ) {
                def readTaxonomy : Try[ DartTaxonomy ] = ymlToOntology( yml )
            }

        }
    }

    def OntologyReader : OntologyReader

}

trait OntologyReaderDI
  extends OntologyReaderDeps {
    this : WmDartConceptDI
      with DartTaxonomyDI
      with UUIDTaxonomyIdDI =>

    override def OntologyReader : OntologyReader = WmOntologyReader

    object WmOntologyReader extends OntologyReader {

        private def elementToConcept( ele : WmOntologyNode ) : DartConcept = ele match {
            case WmOntologyNode( WmOntologyData( name, examples, descriptions, opposite, polarityOpt, patternOpt, semanticType, children ) ) =>
                DartConcept(
                    name,
                    children.toList.flatten.toSet.map( elementToConcept ),
                    metadata =
                        if ( examples.isEmpty && patternOpt.isEmpty && opposite.isEmpty && descriptions.isEmpty && polarityOpt.isEmpty && semanticType.isEmpty )
                            None
                        else Some( WmConceptMetadata(
                            examples.toList.flatten.toSet,
                            patternOpt.toList.flatten.toSet,
                            opposite.toList.flatMap( _.split( "/" ).map( _.trim ) ),
                            descriptions.toSeq.flatten,
                            polarityOpt match {
                                case None => Positive
                                case Some( i ) if i < 0 => Negative
                                case _ => Positive
                            },
                            semanticType match {
                                case Some( "entity" ) => Entity
                                case Some( "event" ) => Event
                                case Some( "property" ) => Property
                                case _ => Entity
                            },
                        ) ),
                )
        }

        private def toJson( ontologyString : String ) : Try[ Json ] = parser.parse( ontologyString ).toTry
        private def toOntology( json : Try[ Json ] ) : Try[ List[ WmOntologyNode ] ] = json.flatMap( _.as[ List[ WmOntologyNode ] ].toTry )

        override def ymlToOntology( yml : String ) : Try[ DartTaxonomy ] =
            toOntology( toJson( yml ) ).map( ( ont : List[ WmOntologyNode ] ) => {
                DartTaxonomy(
                    ont.toSet.map( elementToConcept )
                )
            } )
    }
}


