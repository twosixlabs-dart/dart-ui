package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.wm.WmConceptMetadata
import utest._

import java.util.UUID

object DartTaxonomyTest
  extends TestSuite
    with DartTaxonomyTestDataDI
    with DartTaxonomyDI
    with WmDartConceptDI
    with UUIDTaxonomyIdDI {

    import DartTaxonomyData._

    override def tests : Tests = Tests {

        test( "DartTaxonomy.entries" ) {
            test( "should match up with taxonomy" ) {
                assert( taxonomy.entries.size == 7 )

                assert( taxonomy.entries.exists( entry => entry._2.concept == concept1 && entry._2.path == Seq( concept1.name ) ) )
                assert( taxonomy.entries.exists( entry => entry._2.concept == concept2 && entry._2.path == Seq( concept2.name ) ) )
                assert( taxonomy.entries.exists( entry => entry._2.concept == concept3 && entry._2.path == Seq( concept3.name ) ) )

                assert( taxonomy.entries.exists( entry => entry._2.concept == concept1a && entry._2.path == Seq( concept1.name, concept1a.name ) ) )
                assert( taxonomy.entries.exists( entry => entry._2.concept == concept1b && entry._2.path == Seq( concept1.name, concept1b.name ) ) )

                assert( taxonomy.entries.exists( entry => entry._2.concept == concept1b1 && entry._2.path == Seq( concept1.name, concept1b.name, concept1b1.name ) ) )

                assert( taxonomy.entries.exists( entry => entry._2.concept == concept1b1a && entry._2.path == Seq( concept1.name, concept1b.name, concept1b1.name, concept1b1a.name ) ) )
            }
        }

        test( "DartTaxonomy.pathEntry" ) {
            test( "should return None for an empty path" ) {
                assert( taxonomy.pathEntry( Nil ).isEmpty )
            }

            test( "should return a correct entry for a valid path" ) {
                val entry = taxonomy.pathEntry( Seq( concept1.name, concept1b.name, concept1b1.name ) ).get
                assert( entry.concept == concept1b1 )
                assert( entry.path == Seq( concept1.name, concept1b.name, concept1b1.name ) )
            }
        }

        test( "DartTaxonomy.idEntry" ) {
            test( "should return None for a non-existent id" ) {
                var id = UUID.randomUUID()

                while( taxonomy.entries.exists( _._2.id == id ) ) id = UUID.randomUUID()

                assert( taxonomy.idEntry( id ).isEmpty )
            }

            test( "should return a correct entry for a valid id" ) {
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val id = taxonomy.entries.find( _._2.path == path  ).get._1

                val entry = taxonomy.idEntry( id ).get
                assert( entry.id == id )
                assert( entry.path == path )
                assert( entry.concept == concept1b1 )
            }
        }

        test( "DartTaxonomy.getParent" ) {
            test( "should retrieve entry of a concept's parent" ) {
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val parentPath = Seq( concept1.name, concept1b.name )
                val id = taxonomy.entries.find( _._2.path == path  ).get._1
                val expectedParentId = taxonomy.entries.find( _._2.path == parentPath ).get._1

                val parentEntry = taxonomy.parentEntry( id ).get
                assert( parentEntry.path == parentPath )
                assert( parentEntry.id == expectedParentId )
            }

            test( "should return None for a root level concept" ) {
                val path = Seq( concept3.name )
                val id = taxonomy.entries.find( _._2.path == path  ).get._1

                val parentEntry = taxonomy.parentEntry( id )
                assert( parentEntry.isEmpty )
            }
        }

        test( "DartTaxonomy.updateConceptAt" ) {
            test( "should update a concept and keep the same id" ) {
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newConcept = DartConcept( "new-concept", Set.empty )
                val newPath = path.dropRight( 1 ) :+ newConcept.name

                val id = taxonomy.pathEntry( path ).get.id

                val result : DartTaxonomy = taxonomy.updateConceptAt( path, newConcept ).get

                assert( result.idEntry( id ).get.path == newPath )
                assert( result.idEntry( id ).get.concept == newConcept )

                val traditionalTaxonomy = new Taxonomy[ ConceptMetadataType ] {
                    override val rootConcepts : Set[ DartConcept ] = result.rootConcepts
                }

                // Make sure it hasn't just changed in the entry
                assert( traditionalTaxonomy.getConcept( newPath ).get == newConcept )
                assert( traditionalTaxonomy.getConcept( path ).isEmpty )
            }

            test( "should update parent entry" ) {
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newConcept = DartConcept( "new-concept", Set.empty )
                val newPath = path.dropRight( 1 ) :+ newConcept.name

                val parentId = taxonomy.pathEntry( path.dropRight( 1 ) ).get.id

                val result : DartTaxonomy = taxonomy.updateConceptAt( path, newConcept ).get

                val newParent = result.idEntry( parentId ).get.concept

                assert( newParent.children.contains( newConcept ) )
            }

            test( "should update name and update children path entry index" ) {
                val result : DartTaxonomy =
                    taxonomy.updateConceptAt( Seq( concept1.name ), concept1.copy( name = "new-name" ) ).get

                val resPath1a = result.pathEntry( Seq( "new-name", concept1a.name ) ).get.path
                val resId1a = result.pathEntry( Seq( "new-name", concept1a.name ) ).get.id
                val origId1a = taxonomy.pathEntry( Seq( concept1.name, concept1a.name ) ).get.id

                val resPath1b = result.pathEntry( Seq( "new-name", concept1b.name ) ).get.path
                val resId1b = result.pathEntry( Seq( "new-name", concept1b.name ) ).get.id
                val origId1b = taxonomy.pathEntry( Seq( concept1.name, concept1b.name ) ).get.id

                assert( resPath1a == Seq( "new-name", concept1a.name ) )
                assert( resId1a == origId1a )
                assert( resPath1b == Seq( "new-name", concept1b.name ) )
                assert( resId1b == origId1b )
            }
        }

        test( "DartTaxonomy.addConcept" ) {
            test( "should add concept and add new entry with id" ) {
                val originalEntries = taxonomy.entries
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newConcept = DartConcept( "new-concept", Set.empty )
                val newPath = path :+ newConcept.name

                val newTaxonomy = taxonomy.addConcept( newConcept, path ).get

                assert( newTaxonomy.getConcept( newPath ).contains( newConcept ) )
                val newEntry = newTaxonomy.pathEntry( newPath ).get
                assert( !originalEntries.exists( _._2.id == newEntry.id ) )
            }

            test( "should update parent concept in entry index as well as in taxonomy" ) {
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newConcept = DartConcept( "new-concept", Set.empty )
                val newPath = path :+ newConcept.name

                val newTaxonomy = taxonomy.addConcept( newConcept, path ).get
                assert( newTaxonomy.getConcept( newPath ).contains( newConcept ) )
                val oldParentEntry = taxonomy.pathEntry( path ).get
                val newParentEntry = newTaxonomy.pathEntry( path ).get
                assert( !oldParentEntry.concept.children.contains( newConcept ) )
                assert( newParentEntry.concept.children.contains( newConcept ) )
            }
        }

        test( "DartTaxonomy.removeConcept" ) {
            test( "should remove a concept and remove its entry" ) {
                val originalEntries = taxonomy.entries
                val path = Seq( concept1.name, concept1b.name, concept1b1.name )

                val newTaxonomy = taxonomy.removeConcept( path ).get

                assert( newTaxonomy.getConcept( path ).isEmpty )
                assert( !newTaxonomy.entries.exists( _._2.concept.name == concept1b1.name ) )
            }

            test( "should remove all descendents from removed concept from index") {
                val originalEntries = taxonomy.entries
                val path = Seq( concept1.name, concept1b.name )

                val newTaxonomy = taxonomy.removeConcept( path ).get

                assert( newTaxonomy.getConcept( path ).isEmpty )
                assert( !newTaxonomy.entries.exists( _._2.concept.name == concept1b.name ) )
                assert( !newTaxonomy.entries.exists( _._2.concept.name == concept1b1.name ) )
                assert( !newTaxonomy.entries.exists( _._2.concept.name == concept1b1a.name ) )
                assert( newTaxonomy.pathEntry( path :+ concept1b1.name ).isEmpty )
                assert( newTaxonomy.pathEntry( path :+ concept1b1.name :+ concept1b1a.name ).isEmpty )
            }
        }

        test( "DartTaxonomy.moveConcept" ) {
            test( "should move a concept and update its entry accordingly (id should remain the same)" ) {
                val originalEntries = taxonomy.entries
                val oldPath = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newParentPath = Seq( concept3.name )
                val newPath = newParentPath :+ concept1b1.name

                val newTaxonomy = taxonomy.moveConcept( oldPath, newParentPath ).get

                // Test if concept is correctly indexed
                assert( newTaxonomy.getConcept( oldPath.dropRight( 1 ) ).nonEmpty )
                assert( newTaxonomy.getConcept( oldPath ).isEmpty )
                assert( newTaxonomy.getConcept( newPath).contains( concept1b1 ) )

                // Test if concept is actually in the right place in the concepts tree
                val reindexedTaxononmy = DartTaxonomy( newTaxonomy.rootConcepts )
                assert( reindexedTaxononmy.getConcept( newPath ).contains( concept1b1) )
                assert( reindexedTaxononmy.getConcept( oldPath ).isEmpty )

                val oldEntry = originalEntries.find( _._2.concept.name == concept1b1.name ).get._2
                val newEntry = newTaxonomy.entries.find( _._2.concept.name == concept1b1.name ).get._2

                assert( oldEntry.concept == newEntry.concept )
                assert( oldEntry.id == newEntry.id )
                assert( oldEntry.path == oldPath )
                assert( newEntry.path == newPath )
            }

            test( "should move a concept to root level and update its entry accordingly (id should remain the same)" ) {
                val originalEntries = taxonomy.entries
                val oldPath = Seq( concept1.name, concept1b.name, concept1b1.name )
                val newParentPath = Nil
                val newPath = newParentPath :+ concept1b1.name

                val newTaxonomy = taxonomy.moveConcept( oldPath, newParentPath ).get

                // Test if concept is correctly indexed
                assert( newTaxonomy.getConcept( oldPath.dropRight( 1 ) ).nonEmpty )
                assert( newTaxonomy.getConcept( oldPath ).isEmpty )
                assert( newTaxonomy.getConcept( newPath).contains( concept1b1 ) )

                // Test if concept is actually in the right place in the concepts tree
                val reindexedTaxononmy = DartTaxonomy( newTaxonomy.rootConcepts )
                assert( reindexedTaxononmy.getConcept( newPath ).contains( concept1b1) )
                assert( reindexedTaxononmy.getConcept( oldPath ).isEmpty )

                val oldEntry = originalEntries.find( _._2.concept.name == concept1b1.name ).get._2
                val newEntry = newTaxonomy.entries.find( _._2.concept.name == concept1b1.name ).get._2

                assert( oldEntry.concept == newEntry.concept )
                assert( oldEntry.id == newEntry.id )
                assert( oldEntry.path == oldPath )
                assert( newEntry.path == newPath )
            }

            test( "should move a concept with children and update all of their entries" ) {
                val oldPath = Seq( concept1.name, concept1b.name )
                val oldChildPath = oldPath :+ concept1b1.name
                val oldGrandChildPath = oldChildPath :+ concept1b1a.name
                val newParentPath = Seq( concept3.name )
                val newPath = newParentPath :+ concept1b.name
                val newChildPath = newPath :+ concept1b1.name
                val newGrandChildPath = newChildPath :+ concept1b1a.name

                val newTaxonomy = taxonomy.moveConcept( oldPath, newParentPath ).get

                // Test if concept is correctly indexed
                assert( newTaxonomy.getConcept( oldPath.dropRight( 1 ) ).nonEmpty )
                assert( newTaxonomy.getConcept( oldPath ).isEmpty )
                assert( newTaxonomy.getConcept( newPath).contains( concept1b ) )

                // Test if concept children are correclty indexed
                assert( newTaxonomy.getConcept( oldChildPath ).isEmpty )
                assert( newTaxonomy.getConcept( oldGrandChildPath ).isEmpty )
                assert( newTaxonomy.getConcept( newChildPath ).contains( concept1b1 ) )
                assert( newTaxonomy.getConcept( newGrandChildPath ).contains( concept1b1a ) )

                // Test if concept is actually in the right place in the concepts tree
                val reindexedTaxononmy = DartTaxonomy( newTaxonomy.rootConcepts )
                assert( reindexedTaxononmy.getConcept( newPath ).contains( concept1b ) )
                assert( reindexedTaxononmy.getConcept( oldPath ).isEmpty )
            }
        }
    }
}
