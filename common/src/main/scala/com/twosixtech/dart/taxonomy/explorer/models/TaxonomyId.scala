package com.twosixtech.dart.taxonomy.explorer.models

import upickle.default

import java.util.UUID
import upickle.default.{ReadWriter => RW, _}

trait TaxonomyIdDeps {
    type TaxonomyId
    def generateTaxonomyId() : TaxonomyId
}

trait TaxonomyIdSerializationDeps {
    this : TaxonomyIdDeps =>

    val TaxonomyIdSerialization : TaxonomyIdSerialization

    trait TaxonomyIdSerialization {

        def taxonomyIdToJson( id : TaxonomyId ) : String
        def taxonomyIdToBinary( id : TaxonomyId ) : Array[ Byte ]

        def taxonomyIdFromJson( json : String ) : TaxonomyId
        def taxonomyIdFromBinary( binary : Array[ Byte ] ) : TaxonomyId

        implicit class SerializableTaxonomyId( id : TaxonomyId ) {
            def marshalJson : String = taxonomyIdToJson( id )
            def marshalBinary : Array[ Byte ] = taxonomyIdToBinary( id )
        }

        implicit class DeserializableJson( json : String ) {
            def unmarshalTaxonomyId : TaxonomyId = taxonomyIdFromJson( json )
        }

        implicit class DeserializableBinary( binary : Array[ Byte ] ) {
            def unmarshalTaxonomyId : TaxonomyId = taxonomyIdFromBinary( binary )
        }
    }
}

trait UUIDTaxonomyIdDI
  extends TaxonomyIdDeps {
    override type TaxonomyId = UUID
    override def generateTaxonomyId( ) : UUID = UUID.randomUUID()
}

trait UUIDTaxonomyIdSerializationDI
  extends TaxonomyIdSerializationDeps {
    this : UUIDTaxonomyIdDI =>

    override lazy val TaxonomyIdSerialization : TaxonomyIdSerialization = new TaxonomyIdSerialization {

        override def taxonomyIdToJson( id : UUID ) : String = id.toString

        override def taxonomyIdToBinary( id : UUID ) : Array[ Byte ] = id.toString.getBytes()

        override def taxonomyIdFromJson( json : String ) : UUID = UUID.fromString( json )

        override def taxonomyIdFromBinary( binary : Array[ Byte ] ) : UUID =
            UUID.fromString( new String( binary ) )

    }
}
