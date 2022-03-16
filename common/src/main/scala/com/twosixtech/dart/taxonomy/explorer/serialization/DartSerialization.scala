package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}

trait DartSerializationDeps {
    this : DartConceptDeps
      with DartTaxonomyDI =>

    val DartSerialization : DartSerialization

    trait DartSerialization {
        def conceptToJson( concept : DartConcept ) : String
        def jsonToConcept( json : String ) : DartConcept

        def conceptToBinary( concept : DartConcept ) : Array[ Byte ]
        def binaryToConcept( binary : Array[ Byte ] ) : DartConcept

        def taxonomyToJson( taxonomy : DartTaxonomy ) : String
        def jsonToTaxonomy( json : String ) : DartTaxonomy

        def taxonomyToBinary( taxonomy : DartTaxonomy ) : Array[ Byte ]
        def binaryToTaxonomy( binary : Array[ Byte ] ) : DartTaxonomy

        implicit class SerializableConcept( concept : DartConcept ) {
            def marshalJson : String = conceptToJson( concept )
            def marshalBinary : Array[ Byte ] = conceptToBinary( concept )
        }

        implicit class SerializableTaxonomy( taxonomy : DartTaxonomy ) {
            def marshalJson : String = taxonomyToJson( taxonomy )
            def marshalBinary : Array[ Byte ] = taxonomyToBinary( taxonomy )
        }

        implicit class DeserializableJson( json : String ) {
            def unmarshalConcept : DartConcept = jsonToConcept( json )
            def unmarshalTaxonomy : DartTaxonomy = jsonToTaxonomy( json )
        }

        implicit class DeserializableBinary( binary : Array[ Byte ] ) {
            def unmarshalConcept : DartConcept = binaryToConcept( binary )
            def unmarshalTaxonomy : DartTaxonomy = binaryToTaxonomy( binary )
        }
    }

}

