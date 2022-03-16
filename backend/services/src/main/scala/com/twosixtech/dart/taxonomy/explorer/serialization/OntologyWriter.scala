package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Event, Negative, Positive, Property}
import com.twosixtech.dart.taxonomy.explorer.serialization.WmOntologyYmlJson.{WmOntologyData, WmOntologyNode}
import io.circe.Json
import io.circe.syntax.EncoderOps
import io.circe.yaml.syntax._

import scala.collection.mutable.ListBuffer

trait OntologyWriterDeps
  extends DartTaxonomyDI {

    trait OntologyWriter {
        def taxonomyYaml( taxonomy : DartTaxonomy ) : String

        object Implicits {

            implicit class WritableOntology( taxonomy : DartTaxonomy ) {
                def toYml : String = taxonomyYaml( taxonomy )
            }

        }
    }

    def OntologyWriter : OntologyWriter

}


trait WmOntologyWriterDI
  extends OntologyWriterDeps {
    this : WmDartConceptDI
      with DartTaxonomyDI
      with UUIDTaxonomyIdDI =>

    override lazy val OntologyWriter : OntologyWriter = WmOntologyWriter

    object WmOntologyWriter extends OntologyWriter  {

        def taxonomyYaml( taxonomy : DartTaxonomy ) : String = taxonomyToYmlJson( taxonomy ).asYaml.spaces2

        private def taxonomyToYmlJson( taxonomy : DartTaxonomy ) : Json = {
            Json.arr(
                taxonomy.rootConcepts.map( conceptToJson ).toSeq : _*
            )
        }

        private def conceptToOntologyNode( concept : DartConcept ) : WmOntologyNode = {
            WmOntologyNode(
                WmOntologyData(
                    name = concept.name,
                    examples = concept.metadata.flatMap( _.examples.toList match {
                        case Nil => None
                        case nonEmpty => Some( nonEmpty )
                    } ),
                    descriptions = concept.metadata.flatMap( _.descriptions.toList match {
                        case Nil => None
                        case nonEmpty => Some( nonEmpty )
                    } ),
                    opposite = concept.metadata.flatMap( _.opposite.toList match {
                        case Nil => None
                        case path => Some( path.map( _.trim ).mkString( "/" ) )
                    } ),
                    polarity = concept.metadata.map( _.polarity match {
                        case Positive => 1
                        case Negative => -1
                    } ),
                    patterns = concept.metadata.flatMap( _.patterns.toList match {
                        case Nil => None
                        case nonEmpty => Some( nonEmpty )
                    } ),
                    semanticType = concept.metadata.map( _.semanticType match {
                        case Entity => "entity"
                        case Event => "event"
                        case Property => "property"
                    } ),
                    children = concept.children.toList.map( conceptToOntologyNode ) match {
                        case Nil => None
                        case nonEmpty => Some( nonEmpty )
                    },
                )
            )
        }

        private def conceptToJson( concept : DartConcept ) : Json = {
            val ontologyNode = conceptToOntologyNode( concept )
            ontologyNode.asJson.deepDropNullValues
        }

    }

}

trait OldWmOntologyWriterDI
  extends OntologyWriterDeps {
    this : WmDartConceptDI
      with DartTaxonomyDI
      with UUIDTaxonomyIdDI =>

    override lazy val OntologyWriter : OntologyWriter = WmOntologyWriter

    object WmOntologyWriter extends OntologyWriter  {

        def taxonomyYaml( taxonomy : DartTaxonomy ) : String = taxonomyToYmlJson( taxonomy ).asYaml.spaces2

        private def taxonomyToYmlJson( taxonomy : DartTaxonomy ) : Json = {
            Json.arr(
                taxonomy.rootConcepts.map( conceptToJson ).toSeq : _*
            )
        }

        private def conceptToJson( concept : DartConcept ) : Json = {

            if ( concept.children.isEmpty && concept.metadata.isDefined ) {
                val nodeFields : ListBuffer[ (String, Json) ] = ListBuffer(
                    "OntologyNode" -> Json.Null,
                )
                concept.metadata.foreach( metadata => {
                    if ( metadata.patterns.nonEmpty ) {
                        nodeFields += "pattern" -> Json.arr( metadata.patterns.toSeq.sorted.map( v => Json.fromString( v ) ) : _* )
                    }
                    nodeFields += "examples" -> Json.arr( metadata.examples.toSeq.sorted.map( v => Json.fromString( v ) ) : _* )
                    ( metadata.descriptions.toList match {
                        case Nil => None
                        case nonEmptyList => Some( nonEmptyList.mkString( "\n"  ))
                    } ).foreach( dfn => nodeFields += "definition" -> Json.fromString( dfn ) )
                    nodeFields += "name" -> Json.fromString( concept.name )
                    nodeFields += "polarity" -> ( metadata.polarity match {
                        case Positive => Json.fromInt( 1 )
                        case Negative => Json.fromInt( -1 )
                    } )
                    nodeFields += "semantic type" -> ( metadata.semanticType match {
                        case Entity => Json.fromString( "entity" )
                        case Event => Json.fromString( "event" )
                        case Property => Json.fromString( "property" )
                    } )
                } )
                Json.obj( nodeFields : _* )
            } else {
                Json.obj(
                    concept.name -> (
                        Json.arr( concept.children.toSeq.sortBy( _.name ).map( v => conceptToJson( v ) ) : _* )
                    )
                )
            }
        }

    }

}




