package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.{ConceptIndex, mergeIndices}

import scala.annotation.tailrec

trait Taxonomy[ Metadata ] {

    val rootConcepts : Set[ Concept[ Metadata ] ]

    import Taxonomy.{ConceptPath, ConceptPathFormat}

    def getConcept( path : ConceptPath ) : Option[ Concept[ Metadata ] ] = {
        path.toList match {
            case Nil => None
            case rootPathSegment :: nextPath =>
                rootConcepts.find( _.name == rootPathSegment ) match {
                    case None => None
                    case Some( rootConcept ) => Taxonomy.getConcept( rootConcept, nextPath )
                }
        }
    }

    def updateConceptAt( path : ConceptPath, updated: Concept[ Metadata ] ) : Option[ Taxonomy[ Metadata ] ] = {
        path.toList match {
            case Nil => None
            case rootPathSegment :: nextPath =>
                rootConcepts.find( _.name == rootPathSegment ) match {
                    case None => None
                    case Some( rootConcept ) =>
                        val updatedRootConcept =
                            Taxonomy.updateConceptDescendent( rootConcept, nextPath, updated )

                        val outerRootConcepts = rootConcepts

                        updatedRootConcept match {
                            case None => None
                            case Some( urc ) =>
                                Some( new Taxonomy[ Metadata ] {
                                    override val rootConcepts : Set[ Concept[ Metadata ] ] =
                                        outerRootConcepts - rootConcept + urc
                                } )
                        }
                }
        }
    }

    def addConcept( concept : Concept[ Metadata ], parentPath : ConceptPath = Nil ) : Option[ Taxonomy[ Metadata ] ] = {
        parentPath match {
            case Nil =>
                rootConcepts.find( _.name == concept.name ) match {
                    case Some( _ ) => throw new IllegalArgumentException( s"Concept already exists at root level with name ${concept.name}" )
                    case None =>
                        val outerRootConcepts = rootConcepts
                        Some( new Taxonomy[ Metadata ] {
                            override val rootConcepts : Set[ Concept[ Metadata ] ] =
                                outerRootConcepts + concept
                        } )
                }
            case rootConceptName :: nextPath =>
                rootConcepts.find( _.name == rootConceptName ) match {
                    case None => None
                    case Some( rootConcept ) =>
                        val updatedRootConceptOpt = Taxonomy.addConceptDescendent( rootConcept, concept, nextPath )
                        updatedRootConceptOpt match {
                            case None => None
                            case Some( updatedRootConcept ) =>
                                val outerRootConcepts = rootConcepts
                                Some( new Taxonomy[ Metadata ] {
                                    override val rootConcepts : Set[ Concept[ Metadata ] ] =
                                        outerRootConcepts - rootConcept + updatedRootConcept
                                } )
                        }

                }
        }
    }

    def removeConcept( path : ConceptPath ) : Option[ Taxonomy[ Metadata ] ] = {
        path.toList match {
            case Nil => None
            case rootConceptName :: Nil =>
                rootConcepts.find( _.name == rootConceptName ) match {
                    case None => None
                    case Some( rootConcept ) =>
                        val outerRootConcepts = rootConcepts
                        Some( new Taxonomy[ Metadata ] {
                            override val rootConcepts : Set[ Concept[ Metadata ] ] =
                                outerRootConcepts - rootConcept
                        } )
                }
            case rootConceptName :: nextPath =>
                rootConcepts.find( _.name == rootConceptName ) match {
                    case None => None
                    case Some( rootConcept ) =>
                        Taxonomy.removeConceptDescendent( rootConcept, nextPath ) match {
                            case None => None
                            case Some( updatedRootConcept ) =>
                                val outerRootConcepts = rootConcepts
                                Some( new Taxonomy[ Metadata ] {
                                    override val rootConcepts : Set[ Concept[ Metadata ] ] =
                                        outerRootConcepts - rootConcept + updatedRootConcept
                                } )
                        }
                }
        }
    }

    def moveConcept( path : ConceptPath, newParentPath : ConceptPath ) : Option[ Taxonomy[ Metadata ] ] = {
        for {
            movedConcept <- getConcept( path )
            removedTaxonomy <- removeConcept( path )
            result <- removedTaxonomy.addConcept( movedConcept, newParentPath )
        } yield result
    }

    def indexFacet[ Facet ]( getFacet : Concept[ Metadata ] => Facet ) : ConceptIndex[ Facet ] = {
        mergeIndices( rootConcepts.map( concept => Taxonomy.indexConcept( concept, getFacet ) ) )
    }
}

object Taxonomy {

    type ConceptPath = Seq[ String ]
    type ConceptIndex[ Facet ] = Map[ Facet, Set[ ConceptPath ] ]

    implicit class ConceptPathFormat( path : ConceptPath ) {
        def pathString : String = "/" + path.mkString( "/" )
    }

    @tailrec
    def getConcept[ T ]( concept : Concept[ T ], path : ConceptPath ) : Option[ Concept[ T ] ] = {
        path.toList match {
            case Nil => Some( concept )
            case currentPathSegment :: nextPath =>
                concept.children.find( _.name == currentPathSegment ) match {
                    case None => None
                    case Some( child ) => getConcept( child, nextPath )
                }
        }
    }

    def updateConceptDescendent[ T ]( concept : Concept[ T ], path : ConceptPath, updated : Concept[ T ] ) : Option[ Concept[ T ] ] = {
        path.toList match {
            case Nil => Some( updated )
            case currentPathSegment :: nextPath =>
                concept.children.find( _.name == currentPathSegment ) match {
                    case None => None
                    case Some( child ) =>
                        if ( ( concept.children - child ).exists( _.name == updated.name ) ) throw new IllegalArgumentException( s"Updated concept ${updated.name} has same name as a sibling" )
                        else {
                            updateConceptDescendent( child, nextPath, updated ) match {
                                case None => None
                                case Some( updatedChild ) =>
                                    Some( concept.copy(
                                        children = concept.children - child + updatedChild
                                    ) )
                            }

                        }
                }
        }
    }

    def addConceptDescendent[ Metadata ]( concept : Concept[ Metadata ], newConcept : Concept[ Metadata ], parentPath : ConceptPath = Nil ) : Option[ Concept[ Metadata ] ] = {
        parentPath match {
            case Nil =>
                if ( concept.children.exists( _.name == newConcept.name ) ) throw new IllegalArgumentException( s"${concept.name} already has child named ${newConcept.name}" )
                else Some( concept.copy( children = concept.children + newConcept ) )
            case currentPathSegment :: nextPath =>
                concept.children.find( _.name == currentPathSegment ) match {
                    case None => None
                    case Some( child ) =>
                        addConceptDescendent( child, newConcept, nextPath ) match {
                            case None => None
                            case Some( newChild ) =>
                                Some( concept.copy(
                                    children = concept.children - child + newChild
                                ) )
                        }
                }
        }
    }

    def removeConceptDescendent[ Metadata ]( concept : Concept[ Metadata ], path : ConceptPath ) : Option[ Concept[ Metadata ] ] = {
        path.toList match {
            case Nil => None
            case currentPathSegment :: Nil =>
                concept.children.find( _.name == currentPathSegment ) match {
                    case None => None
                    case Some( child ) =>
                        Some( concept.copy(
                            children = concept.children - child
                        ) )
                }
            case currentPathSegment :: nextPath =>
                concept.children.find( _.name == currentPathSegment ) match {
                    case None => None
                    case Some( child ) =>
                        removeConceptDescendent( child, nextPath ) match {
                            case None => None
                            case Some( updatedChild ) =>
                                Some( concept.copy(
                                    children = concept.children - child + updatedChild
                                ) )
                        }
                }
        }
    }

    // CAUTION: Will have bad performance for large taxonomies
    private def mergeIndices[ Facet ]( indices : Iterable[ ConceptIndex[ Facet ] ] ) : ConceptIndex[ Facet ] = {
        indices.foldLeft( Map.empty[ Facet, Set[ ConceptPath ] ] ) { ( currentIndex, nextIndex ) =>
            ( currentIndex.toSeq ++ nextIndex.toSeq )
              .groupBy( _._1 )
              .map( groupedEntry => groupedEntry._1 -> groupedEntry._2.map( _._2 ).toSet.flatten )
        }
    }

    def indexConcept[ Metadata, Facet ]( concept : Concept[ Metadata ], getFacet : Concept[ Metadata ] => Facet ) : ConceptIndex[ Facet ] = {
        val thisMap = Map( getFacet( concept ) -> Set( Seq( concept.name ) ) )

        if ( concept.children.map( _.name ).size < concept.children.size )
            throw new IllegalArgumentException( s"Cannot index concept ${concept.name}: multiple children with the same name" )

        val childrenIndices : Set[ ConceptIndex[ Facet ] ] =
            concept
              .children
              .map( ( c : Concept[ Metadata ] ) => indexConcept( c, getFacet ) )
              .map( _.mapValues( _.map( concept.name +: _ ) ) )

        mergeIndices( childrenIndices + thisMap )
    }

}

