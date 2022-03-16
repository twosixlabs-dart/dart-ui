package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.{Concept, DartTaxonomyDI, Taxonomy, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.models.wm.{Entity, Event, Negative, Polarity, Positive, Property, SemanticType, WmConceptMetadata}
import upickle.default.{ReadWriter => RW, _}

trait WmDartSerializationDI
  extends DartSerializationDeps {
    this : WmDartConceptDI
        with DartTaxonomyDI
        with UUIDTaxonomyIdDI
        with UUIDTaxonomyIdSerializationDI =>

    private def polarityToString( pol : Polarity ) : String = pol match {
        case Positive => "positive"
        case Negative => "negative"
    }

    private def stringToPolarity( str : String ) : Polarity = str.trim.toLowerCase match {
        case "positive" => Positive
        case "negative" => Negative
        case other => throw new IllegalArgumentException( s"""$other is not a valid polarity value: accepted values are "positive" or "negative"""" )
    }

    private def semanticTypeToString( st : SemanticType ) : String = st match {
        case Entity => "entity"
        case Event => "event"
        case Property => "property"
    }

    private def stringToSemanticType( str : String ) : SemanticType = str.trim.toLowerCase match {
        case "entity" => Entity
        case "event" => Event
        case "property" => Property
        case other => throw new IllegalArgumentException( s"""$other is not a valid semantic_type value: accepted values are "event", "entity", or "property"""" )
    }

    case class WmConceptMetadataDto(
        examples : Seq[ String ],
        patterns : Seq[ String ],
        opposite : Seq[ String ],
        descriptions : Seq[ String ],
        polarity : String,
        @upickle.implicits.key("semantic type") semanticType : String,
    ) {
        val normalizedPolarity = polarity.trim.toLowerCase
        require( normalizedPolarity == "positive" || normalizedPolarity == "negative" )
        def toWmConceptMetadata : WmConceptMetadata = WmConceptMetadata(
            examples.toSet,
            patterns.toSet,
            opposite,
            descriptions,
            stringToPolarity( polarity ),
            stringToSemanticType( semanticType ),
        )
    }

    object WmConceptMetadataDto {
        implicit val rw : RW[ WmConceptMetadataDto ] = macroRW
        def fromWmConceptMetadata( metadata : WmConceptMetadata ) : WmConceptMetadataDto = WmConceptMetadataDto(
            metadata.examples.toSeq.sorted,
            metadata.patterns.toSeq.sorted,
            metadata.opposite,
            metadata.descriptions,
            polarityToString( metadata.polarity ),
            semanticTypeToString( metadata.semanticType ),
        )
    }

    case class ConceptDto(
        name : String,
        children : Set[ ConceptDto ],
        metadata : Option[ WmConceptMetadataDto ],
    ) {
        def toConcept : DartConcept = DartConcept( name, children.map( _.toConcept ), metadata.map( _.toWmConceptMetadata ) )
    }

    object ConceptDto {
        implicit val rw : RW[ ConceptDto ] = macroRW
        def fromConcept( concept : DartConcept ) : ConceptDto = {
            ConceptDto( concept.name, concept.children.map( fromConcept ), concept.metadata.map( WmConceptMetadataDto.fromWmConceptMetadata ) )
        }
    }

    case class TaxonomyEntryDto(
        id : String,
        path : ConceptPath,
    ) {
        def toEntry( taxonomy : Taxonomy[ Option[ WmConceptMetadata ] ] ) : DartTaxonomyEntry = {
            import TaxonomyIdSerialization._
            DartTaxonomyEntry( id.unmarshalTaxonomyId, path, taxonomy.getConcept( path ).get )
        }
    }

    object TaxonomyEntryDto {
        implicit val rw : RW[ TaxonomyEntryDto ] = macroRW
        def fromEntry( entry : DartTaxonomyEntry ) : TaxonomyEntryDto = {
            import TaxonomyIdSerialization._
            TaxonomyEntryDto( entry.id.marshalJson, entry.path )
        }
    }

    case class TaxonomyDto(
        root_concepts : Set[ ConceptDto ],
        entries : Map[ TaxonomyId, TaxonomyEntryDto ],
    ) {
        def toTaxonomy : DartTaxonomy = {
            val concepts = root_concepts.map( _.toConcept )
            val simpleTaxonomy = new Taxonomy[ Option[ WmConceptMetadata ] ] {
                override val rootConcepts : Set[ Concept[ Option[ WmConceptMetadata ] ] ] = concepts
            }
            val allEntries = entries.map( v => v._1 -> v._2.toEntry( simpleTaxonomy ) )
            DartTaxonomy( concepts, allEntries )
        }
    }

    object TaxonomyDto {
        implicit val rw : RW[ TaxonomyDto ] = macroRW
        def fromTaxonomy( taxonomy : DartTaxonomy ) : TaxonomyDto =
            TaxonomyDto(
                taxonomy.rootConcepts.map( ConceptDto.fromConcept ),
                taxonomy.entries.map( v => v._1 -> TaxonomyEntryDto.fromEntry( v._2 ) ),
            )
    }

    override lazy val DartSerialization : DartSerialization = new DartSerialization {
        override def conceptToJson( concept : DartConcept ) : String =
            write( ConceptDto.fromConcept( concept ) )

        override def jsonToConcept( json : String ) : DartConcept =
            read[ ConceptDto ]( json ).toConcept

        override def conceptToBinary( concept : DartConcept ) : Array[ Byte ] =
            writeBinary( ConceptDto.fromConcept( concept ) )

        override def binaryToConcept( binary : Array[ Byte ] ) : DartConcept =
            readBinary[ ConceptDto ]( binary ).toConcept

        override def taxonomyToJson( taxonomy : DartTaxonomy ) : String =
            write( TaxonomyDto.fromTaxonomy( taxonomy ) )

        override def jsonToTaxonomy( json : String ) : DartTaxonomy =
            read[ TaxonomyDto ]( json ).toTaxonomy

        override def taxonomyToBinary( taxonomy : DartTaxonomy ) : Array[ Byte ] =
            writeBinary( TaxonomyDto.fromTaxonomy( taxonomy ) )

        override def binaryToTaxonomy( binary : Array[ Byte ] ) : DartTaxonomy =
            readBinary[ TaxonomyDto ]( binary ).toTaxonomy
    }

}
