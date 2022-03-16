package com.twosixtech.dart.taxonomy.explorer.models

import utest._

object TaxonomyTest extends TestSuite {

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

    val taxonomy = new Taxonomy[ TestMetadata ] {
        override val rootConcepts : Set[ Concept[ TestMetadata ] ] = Set( concept1, concept2, concept3 )
    }

    override def tests : Tests = Tests {

        test( "Taxonomy.getConcept" ) {
            test( "should return None if path is empty sequence" ) {
                assert( taxonomy.getConcept( Nil ).isEmpty )
            }

            test( "should return final concept in path, if path is non-empty and valid" ) {
                val result = taxonomy.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) )

                assert( result.contains( concept1b1a ) )
            }

            test( "should return None if path is invalid" ) {
                assert( taxonomy.getConcept( Seq( "invalid", "path", "sequence" ) ).isEmpty )
                assert( taxonomy.getConcept( Seq( concept1.name, concept1b.name, "non-existent-concept" ) ).isEmpty )
                assert( taxonomy.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name, "non-existent-final-child" ) ).isEmpty )
            }
        }

        test( "Taxonomy.updateConceptAt" ) {
            val updatedConcept = concept1b1a.copy( name = "new-name", metadata = TestMetadata( "new-string-facet", 101010 ) )
            val result = taxonomy.updateConceptAt(
                Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ),
                updatedConcept,
            )

            assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ).isEmpty )
            assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, updatedConcept.name ) ).contains( updatedConcept ) )
            assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name ) ).exists( _.children.contains( updatedConcept ) ) )
        }

        test( "Taxonomy.addConcept" ) {
            test( "should add concept to rootConcepts if path is empty" ) {
                val newConcept = Concept[ TestMetadata ]( "new-concept", Set.empty, TestMetadata( "new-concept-string-facet", 99999 ) )
                val result = taxonomy.addConcept( newConcept, parentPath = Nil )

                assert( result.get.rootConcepts.exists( _.name == newConcept.name ) )
            }

            test( "should add concept to a parent specified by a non-empty path" ) {
                val newConcept = Concept[ TestMetadata ]( "new-concept", Set.empty, TestMetadata( "new-concept-string-facet", 99999 ) )
                val result = taxonomy.addConcept( newConcept, parentPath = Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) )

                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name, newConcept.name ) ).contains( newConcept ) )
            }
        }

        test( "Taxonomy.removeConcept" ) {
            test( "should remove concept from rootConcepts if path has only one segment" ) {
                val result = taxonomy.removeConcept( Seq( concept1.name ) )

                assert( result.get.getConcept( Seq( concept1.name ) ).isEmpty )
                assert( result.get.getConcept( Seq( concept2.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name ) ).nonEmpty )
            }

            test( "should remove concept from concept specified by path with more than one segment" ) {
                val result = taxonomy.removeConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) )

                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ).isEmpty )
                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept2.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name ) ).nonEmpty )
            }
        }

        test( "Taxonomy.moveConcept" ) {
            test( "should move from root level to nexted level" ) {
                val result = taxonomy.moveConcept( Seq( concept1.name ), Seq( concept3.name ) )

                assert( result.get.getConcept( Seq( concept1.name ) ).isEmpty )
                assert( result.get.getConcept( Seq( concept2.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name, concept1.name ) ).contains( concept1 ) )
                assert( result.get.getConcept( Seq( concept3.name, concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ).nonEmpty )
            }

            test( "should move from nested level to root level" ) {
                val result = taxonomy.moveConcept( Seq( concept1.name, concept1b.name, concept1b1.name ), Nil )

                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name ) ).isEmpty )
                assert( result.get.getConcept( Seq( concept1.name, concept1b.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept1.name, concept1b.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept2.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept1b1.name ) ).contains( concept1b1 ) )
            }

            test( "should move from nested level to nested level" ) {
                val result = taxonomy.moveConcept( Seq( concept1.name, concept1b.name, concept1b1.name ), Seq( concept2.name ) )

                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name ) ).isEmpty )
                assert( result.get.getConcept( Seq( concept1.name, concept1b.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept2.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept2.name, concept1b1.name  ) ).contains( concept1b1 ) )
                assert( result.get.getConcept( Seq( concept2.name, concept2a.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept2.name, concept2b.name ) ).nonEmpty )
                assert( result.get.getConcept( Seq( concept3.name ) ).nonEmpty )
            }

            test( "should not be able to move further down its own branch" ) {
                val result = taxonomy.moveConcept( Seq( concept1.name, concept1b.name ), Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) )

                assert( result.isEmpty )
            }

            test( "should be able to move back up its own branch" ) {
                val result = taxonomy.moveConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ), Seq( concept1.name, concept1b.name ) )

                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1a.name ) ).contains( concept1b1a ) )
                assert( result.get.getConcept( Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ).isEmpty )
            }
        }
    }
}
