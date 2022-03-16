package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.{Concept, DartTaxonomyDI, EmptyDartConceptDI, Taxonomy, TaxonomyIdSerializationDeps, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI}
import upickle.default.{ReadWriter => RW, _}

trait EmptySerializationDI
  extends DartSerializationDeps {
    this : EmptyDartConceptDI
      with DartTaxonomyDI
      with UUIDTaxonomyIdDI
      with UUIDTaxonomyIdSerializationDI =>

    case class ConceptDto(
        name : String,
        children : Set[ ConceptDto ]
    ) {
        def toConcept : DartConcept = DartConcept( name, children.map( _.toConcept ), () )
    }

    object ConceptDto {
        implicit val rw : RW[ ConceptDto ] = macroRW
        def fromConcept( concept : DartConcept ) : ConceptDto = {
            ConceptDto( concept.name, concept.children.map( fromConcept ) )
        }
    }

    case class TaxonomyEntryDto(
        id : String,
        path : ConceptPath,
    ) {
        def toEntry( taxonomy : Taxonomy[ Unit ] ) : DartTaxonomyEntry = {
            import TaxonomyIdSerialization._
            DartTaxonomyEntry( id.unmarshalTaxonomyId, path, taxonomy.getConcept( path ).get )
        }
    }

    object TaxonomyEntryDto {
        import TaxonomyIdSerialization._

        implicit def rw : RW[ TaxonomyEntryDto ] = macroRW
        def fromTaxonomyEntry( entry : DartTaxonomyEntry ) : TaxonomyEntryDto = {
            TaxonomyEntryDto( entry.id.marshalJson, entry.path )
        }
    }

    case class TaxonomyDto(
        root_concepts : Set[ ConceptDto ],
        entries : Map[ TaxonomyId, TaxonomyEntryDto ],
    ) {
        import TaxonomyIdSerialization._

        def toTaxonomy : DartTaxonomy = {
            val concepts = root_concepts.map( _.toConcept )
            val simpleTaxonomy = new Taxonomy[ Unit ] {
                override val rootConcepts : Set[ Concept[ Unit ] ] = concepts
            }
            val allEntries = entries.map( v => v._1 -> v._2.toEntry( simpleTaxonomy ) )

            DartTaxonomy( concepts, allEntries )
        }
    }

    object TaxonomyDto {
        implicit lazy val rw : RW[ TaxonomyDto ] = macroRW
        def fromTaxonomy( taxonomy : DartTaxonomy ) : TaxonomyDto =
            TaxonomyDto(
                taxonomy.rootConcepts.map( ConceptDto.fromConcept ),
                taxonomy.entries.map( v => v._1 -> TaxonomyEntryDto.fromTaxonomyEntry( v._2 ) ),
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
