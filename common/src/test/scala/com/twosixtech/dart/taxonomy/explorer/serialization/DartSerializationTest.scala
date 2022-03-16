package com.twosixtech.dart.taxonomy.explorer.serialization

import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, EmptyDartConceptDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import utest._

trait DartSerializationTest extends TestSuite {
    this : DartSerializationDeps
      with DartTaxonomyDI =>

    override def tests : Tests = Tests {

        import DartSerialization._

        val testChild = DartConcept( "test-child", Set.empty )
        val testConcept = DartConcept( "test-concept", Set( testChild ) )

        val concept1b1 = DartConcept( "concept-1-b-1", Set.empty )

        val concept1a = DartConcept( "concept-1-a", Set.empty )
        val concept1b = DartConcept( "concept-1-b", Set( concept1b1 ) )

        val concept2a = DartConcept( "concept-2-a", Set.empty )

        val concept1 = DartConcept( "concept-1", Set( concept1b ) )
        val concept2 = DartConcept( "concept-2", Set( concept2a ) )

        val testTaxonomy = DartTaxonomy( Set( concept1, concept2 ) )

        test( "Concept ser/de" ) {
            test( "should be able to serialize concept and deserialize to equal object" ) {
                assert( testConcept.marshalJson.unmarshalConcept == testConcept )
                assert( testConcept.marshalBinary.unmarshalConcept == testConcept )
            }
        }

        test( "Taxonomy ser/de" ) {
            test( "should be able to serialize taxonomy and deserialize to equal object" ) {
                assert( testTaxonomy.marshalJson.unmarshalTaxonomy == testTaxonomy )
                assert( testTaxonomy.marshalBinary.unmarshalTaxonomy == testTaxonomy )
            }
        }

    }

}

object EmptySerializationDITest
  extends DartSerializationTest
    with EmptyDartConceptDI
    with DartTaxonomyDI
    with EmptySerializationDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI

object WmDartSerializationDITest
  extends DartSerializationTest
    with WmDartConceptDI
    with DartTaxonomyDI
    with WmDartSerializationDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI

