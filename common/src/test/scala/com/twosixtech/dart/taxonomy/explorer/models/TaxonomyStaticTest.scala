package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptIndex
import utest._

object TaxonomyStaticTest extends TestSuite {

    case class TestMetadata(
        stringFacet : String,
        intFacet : Int,
    )

    type TestConcept = Concept[ TestMetadata ]

    def mkConc( name : String, strFacet : String, intFacet : Int ) : TestConcept =
        Concept[ TestMetadata ]( name, Set.empty, TestMetadata( strFacet, intFacet ) )

    // Same intFacet as 2a and 2b (below)
    val concept1b1a = mkConc( "concept-1-b-1-a", "concept-1-b-1-a-string-facet", 100 )
    val concept1b1 = mkConc( "concept-1-b-1", "concept-1-b-1-string-facet", 121 )
      .copy( children = Set( concept1b1a ) )

    val concept1a = mkConc( "concept-1-a", "concept-1-a-string-facet", 11 )
    val concept1b = mkConc( "concept-1-b", "concept-1-b-string-facet", 12 )
      .copy( children = Set( concept1b1 ) )

    // Concept 2a and 2b have same intFacet
    val concept2a = mkConc( "concept-2-a", "concept-2-a-string-facet", 100 )
    val concept2b = mkConc( "concept-2-b", "concept-2-b-string-facet", 100 )

    val concept1 = mkConc( "concept-1", "concept-1-string-facet", 1 )
      .copy( children = Set( concept1a, concept1b ) )

    val concept2 = mkConc( "concept-2", "concept-2-string-facet", 2 )
      .copy( children = Set( concept2a, concept2b ) )

    val concept3 = mkConc( "concept-3", "concept-3-string-facet", 3 )

    val rootConcept = mkConc( "root-concept", "root-concept-string-facet", 0 )
      .copy( children = Set( concept1, concept2, concept3 ) )

    override def tests : Tests = Tests {

        test( "Taxonomy.indexConcept" ) {

            test( "should index names" ) {
                val index : ConceptIndex[ String ] =
                    Taxonomy.indexConcept( rootConcept, ( concept : TestConcept ) => concept.name )

                assert( index( rootConcept.name ) == Set( Seq( rootConcept.name ) ) )
                assert( index( concept1.name ) == Set( Seq( rootConcept.name, concept1.name ) ) )
                assert( index( concept2.name ) == Set( Seq( rootConcept.name, concept2.name ) ) )
                assert( index( concept3.name ) == Set( Seq( rootConcept.name, concept3.name ) ) )
                assert( index( concept1a.name ) == Set( Seq( rootConcept.name, concept1.name, concept1a.name ) ) )
                assert( index( concept1b.name ) == Set( Seq( rootConcept.name, concept1.name, concept1b.name ) ) )
                assert( index( concept2a.name ) == Set( Seq( rootConcept.name, concept2.name, concept2a.name ) ) )
                assert( index( concept2b.name ) == Set( Seq( rootConcept.name, concept2.name, concept2b.name ) ) )
                assert( index( concept1b1.name ) == Set( Seq( rootConcept.name, concept1.name, concept1b.name, concept1b1.name ) ) )
                assert( index( concept1b1a.name ) == Set( Seq( rootConcept.name, concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ) )
            }

            test( "should index non-unique facets" ) {
                val index : ConceptIndex[ Int ] =
                    Taxonomy.indexConcept( rootConcept, ( concept : TestConcept ) => concept.metadata.intFacet )

                assert( index( 0 ) == Set( Seq( rootConcept.name ) ) )
                assert( index( 1 ) == Set( Seq( rootConcept.name, concept1.name ) ) )
                assert( index( 2 ) == Set( Seq( rootConcept.name, concept2.name ) ) )
                assert( index( 3 ) == Set( Seq( rootConcept.name, concept3.name ) ) )
                assert( index( 11 ) == Set( Seq( rootConcept.name, concept1.name, concept1a.name ) ) )
                assert( index( 12 ) == Set( Seq( rootConcept.name, concept1.name, concept1b.name ) ) )
                assert( index( 100 ) == Set(
                    Seq( rootConcept.name, concept2.name, concept2a.name ),
                    Seq( rootConcept.name, concept2.name, concept2b.name ),
                    Seq( rootConcept.name, concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ),
                ) )
                assert( index( 121 ) == Set( Seq( rootConcept.name, concept1.name, concept1b.name, concept1b1.name ) ) )
            }
        }

        test( "Taxonomy.getConcept" ) {
            test( "should return original concept if path is empty sequence" ) {
                val result = Taxonomy.getConcept( concept2b, Nil )

                assert( result.contains( concept2b ) )
            }

            test( "should return final concept in path, if path is non-empty" ) {
                val result = Taxonomy.getConcept( rootConcept, Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) )

                assert( result.contains( concept1b1a ) )
            }

            test( "should return None if path is wrong" ) {
                assert( Taxonomy.getConcept( rootConcept, Seq( "blah", "blurg", "blammo" ) ).isEmpty )
                assert( Taxonomy.getConcept( rootConcept, Seq( concept1.name, concept1b.name, "non-existing-child" ) ).isEmpty )
                assert( Taxonomy.getConcept( rootConcept, Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name, "another-one" ) ).isEmpty )
            }
        }
    }
}
