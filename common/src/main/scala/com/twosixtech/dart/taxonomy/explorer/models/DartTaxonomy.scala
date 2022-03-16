package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.{ConceptIndex, ConceptPath, indexConcept}

import java.util.UUID
import scala.language.higherKinds

trait DartTaxonomyDI
  extends DartConceptDeps
    with TaxonomyIdDeps {

    case class DartTaxonomyEntry(
        id : TaxonomyId,
        path : ConceptPath,
        concept : DartConcept,
    )

    type DartTaxonomyIndex[ Facet ] = Map[ Facet, Set[ DartTaxonomyEntry ] ]

    trait DartTaxonomy {
        val rootConcepts : Set[ DartConcept]

        val entries : Map[ TaxonomyId, DartTaxonomyEntry ]
        def generateIndex[ Facet ]( getFacet : DartTaxonomyEntry => Facet ) : DartTaxonomyIndex[ Facet ]
        def idEntry( id : TaxonomyId ) : Option[ DartTaxonomyEntry ]
        def pathEntry( path : ConceptPath ) : Option[ DartTaxonomyEntry ]
        def parentEntry( id : TaxonomyId ) : Option[ DartTaxonomyEntry ] = {
            idEntry( id ).flatMap( entry => {
                val parentPath = entry.path.dropRight( 1 )
                pathEntry( parentPath )
            } )
        }

        def getConcept( path : ConceptPath ) : Option[ DartConcept ]
        def updateConceptAt(
            path : ConceptPath,
            updated : Concept[ ConceptMetadataType ],
        ) : Option[ DartTaxonomy ]
        def addConcept(
            concept : Concept[ ConceptMetadataType ],
            parentPath : ConceptPath,
            id : Option[ TaxonomyId ] = None,
        ) : Option[ DartTaxonomy ]
        def removeConcept( path : ConceptPath ) : Option[ DartTaxonomy ]
        def moveConcept(
            path : ConceptPath,
            newParentPath : ConceptPath
        ) : Option[ DartTaxonomy ]
    }

    private def indexConcepts( concepts : Set[ DartConcept ] ) : Map[ TaxonomyId, DartTaxonomyEntry ] = {
        require( concepts.map( _.name ).size == concepts.size )

        val index = new Taxonomy[ ConceptMetadataType ] {
            override val rootConcepts : Set[ Concept[ ConceptMetadataType ] ] = concepts
        }

        val simpleIndex = index.indexFacet( v => v )

         simpleIndex.flatMap( ( coreTuple : (Concept[ ConceptMetadataType ], Set[ ConceptPath ]) ) => {
            val paths = coreTuple._2
            val concept = coreTuple._1

            paths.map( path => {
                val newId = generateTaxonomyId()
                newId -> DartTaxonomyEntry( newId, path, concept )
            } )
        } ).toMap
    }

    sealed case class IndexedDartTaxonomy(
        rootConcepts : Set[ DartConcept ],
        override val entries : Map[ TaxonomyId, DartTaxonomyEntry ],
    ) extends DartTaxonomy {

        private val outerRootConcepts = rootConcepts

        val simpleTaxonomy = new Taxonomy[ ConceptMetadataType ] {
            override val rootConcepts : Set[ Concept[ ConceptMetadataType ] ] = outerRootConcepts
        }

        override def generateIndex[ Facet ]( getFacet : DartTaxonomyEntry => Facet ) : DartTaxonomyIndex[ Facet ] = {
            entries.foldLeft( Map.empty[ Facet, Set[ DartTaxonomyEntry ] ] ) { ( currentIndex, nextEntry ) =>
                val facet = getFacet( nextEntry._2 )

                if ( currentIndex.contains( facet ) ) currentIndex.updated( facet, currentIndex( facet ) + nextEntry._2 )
                else currentIndex + ( facet -> Set( nextEntry._2 ) )
            }
        }

        lazy val pathIndex : Map[ ConceptPath, DartTaxonomyEntry ] = generateIndex( _.path ).mapValues( _.head )

        override def idEntry( id : TaxonomyId ) : Option[ DartTaxonomyEntry ] = entries.get( id )

        override def pathEntry( path : ConceptPath ) : Option[ DartTaxonomyEntry ] = pathIndex.get( path )

        def getConcept( path : ConceptPath ) : Option[ DartConcept ] = {
            pathEntry( path ).map( _.concept )
        }

        def updateConceptAt(
            path : ConceptPath,
            updated : Concept[ ConceptMetadataType ],
        ) : Option[ DartTaxonomy ] = {
            val originalEntry = pathEntry( path )
            val updatedTaxonomyOpt = simpleTaxonomy.updateConceptAt( path, updated )
            updatedTaxonomyOpt match {
                case None => None
                case Some( updatedTaxonomy ) =>
                    import Taxonomy.ConceptPathFormat
                    pathEntry( path ) match {
                        case None => throw new IllegalStateException( s"No entry exists for ${path.pathString}" )
                        case Some( oldEntry ) =>
                            val newEntry = oldEntry.copy(
                                concept = updated,
                                path = oldEntry.path.dropRight( 1 ) :+ updated.name,
                            )
                            val updatedEntries =
                                entries - oldEntry.id + (oldEntry.id -> newEntry)

                            val entriesWithNameUpdate = originalEntry match {
                                case None => updatedEntries
                                case Some( DartTaxonomyEntry( _, _, oldConcept ) ) =>
                                    if ( oldConcept.name != updated.name ) {
                                        val len = path.length
                                        val newPath = path.dropRight( 1 ) :+ updated.name
                                        updatedEntries.mapValues( entry => {
                                            if ( entry.path.startsWith( path ) ) {
                                                entry.copy( path = newPath ++ entry.path.drop( len ) )
                                            } else entry
                                        } )
                                    } else updatedEntries
                            }

                            val parentEntryOpt =
                                if ( newEntry.path.dropRight( 1 ).isEmpty ) None
                                else pathEntry( newEntry.path.dropRight( 1 ) )

                            val finalEntries = parentEntryOpt match {
                                case None => entriesWithNameUpdate
                                case Some( parentEntry ) =>
                                    val updatedParent =
                                        updatedTaxonomy.getConcept( newEntry.path.dropRight( 1 ) ).get
                                    val newParentEntry =
                                        parentEntry.copy( concept = updatedParent )

                                    entriesWithNameUpdate - parentEntry.id + (parentEntry.id -> newParentEntry)
                            }

                            Some( IndexedDartTaxonomy( updatedTaxonomy.rootConcepts, finalEntries ) )
                    }
            }
        }

        def getUpdatedEntries( newTaxonomy : Taxonomy[ ConceptMetadataType ], paths : Iterable[ ConceptPath ] ) : Map[ TaxonomyId, DartTaxonomyEntry ] = {
            paths.foldLeft( entries ) { ( currentEntries, nextPath ) =>
                val oldEntryOpt = pathEntry( nextPath )
                oldEntryOpt match {
                    case None => currentEntries
                    case Some( oldEntry ) =>
                        val newConcept = newTaxonomy.getConcept( nextPath ).get
                        val newEntry = oldEntry.copy( concept = newConcept )
                        currentEntries
                          .updated( oldEntry.id, newEntry )
                }

            }
        }

        override def addConcept(
            concept : Concept[ ConceptMetadataType ],
            parentPath : ConceptPath,
            id : Option[ TaxonomyId ] = None,
        ) : Option[ DartTaxonomy ] = {
            val updatedTaxonomyOpt = simpleTaxonomy.addConcept( concept, parentPath )
            updatedTaxonomyOpt match {
                case None => None
                case Some( updatedTaxonomy ) =>
                    val updatedEntries = {
                        val newId = id.getOrElse( generateTaxonomyId() )
                        getUpdatedEntries( updatedTaxonomy, Some( parentPath ) ) +
                        (newId -> DartTaxonomyEntry( newId, parentPath :+ concept.name, concept ) )
                    }

                    Some( IndexedDartTaxonomy( updatedTaxonomy.rootConcepts, updatedEntries ) )
            }
        }

        def removeConcept( path : ConceptPath ) : Option[ DartTaxonomy ] = {
            val updatedTaxonomyOpt = simpleTaxonomy.removeConcept( path )
            updatedTaxonomyOpt match {
                case None => None
                case Some( updatedTaxonomy ) =>
                    import Taxonomy.ConceptPathFormat

                    def removeChildrenFromEntries( currentConcept : TaxonomyId, currentEntries : Map[ TaxonomyId, DartTaxonomyEntry ] ) : Map[ TaxonomyId, DartTaxonomyEntry ] = {
                        val currentEntry = idEntry( currentConcept ).get
                        val children = currentEntry.concept.children
                        val entriesWithoutChildren = children.foldLeft( currentEntries ) { ( lastEntries, nextChild ) =>
                            val nextPath = currentEntry.path :+ nextChild.name
                            val nextEntry = pathEntry( nextPath ).get
                            removeChildrenFromEntries( nextEntry.id, lastEntries )
                        }
                        entriesWithoutChildren - currentConcept
                    }

                    pathEntry( path ) match {
                        case None => throw new IllegalStateException( s"No entry exists for ${path.pathString}" )
                        case Some( oldEntry ) =>
                            val parentPath = path.dropRight( 1 )
                            val entriesWithUpdatedParent =
                                getUpdatedEntries( updatedTaxonomy, Some( parentPath ) ) - oldEntry.id
                            val entriesWithoutChildren =
                                removeChildrenFromEntries( oldEntry.id, entriesWithUpdatedParent )

                            Some( IndexedDartTaxonomy( updatedTaxonomy.rootConcepts, entriesWithoutChildren ) )
                    }
            }
        }

        def moveConcept(
            path : ConceptPath,
            newParentPath : ConceptPath
        ) : Option[ DartTaxonomy ] = {
            val updatedTaxonomyOpt = simpleTaxonomy.moveConcept( path, newParentPath )
            updatedTaxonomyOpt match {
                case None => None
                case Some( updatedTaxonomy ) =>
                    import Taxonomy.ConceptPathFormat
                    pathEntry( path ) match {
                        case None => throw new IllegalStateException( s"No entry exists for ${path.pathString}" )
                        case Some( oldEntry ) =>
                            val oldParentPath = path.dropRight( 1 )
                            val updatedEntries =
                                getUpdatedEntries( updatedTaxonomy, Seq( oldParentPath, newParentPath ) )

                            val oldParentPathSize = path.dropRight( 1 ).length
                            def fixChildren( currentConcept : TaxonomyId, currentEntries : Map[ TaxonomyId, DartTaxonomyEntry ] ) : Map[ TaxonomyId, DartTaxonomyEntry ] = {
                                val currentEntry : DartTaxonomyEntry = currentEntries( currentConcept )
                                val updatedPath = newParentPath ++ currentEntry.path.drop( oldParentPathSize )
                                val newEntries = currentEntries.updated( currentConcept, currentEntry.copy( path = updatedPath ) )
                                currentEntry.concept.children.foldLeft( newEntries ) { (entrs, child) =>
                                    fixChildren( pathIndex( currentEntry.path :+ child.name ).id, entrs )
                                }
                            }

                            val fixedChildEntries = fixChildren( oldEntry.id, updatedEntries )

                            Some( IndexedDartTaxonomy( updatedTaxonomy.rootConcepts, fixedChildEntries ) )
                    }
            }
        }
    }

    object DartTaxonomy {

        def apply( rootConcepts : Set[ DartConcept ] ) : DartTaxonomy = {
            val entries = indexConcepts( rootConcepts )

            IndexedDartTaxonomy( rootConcepts, entries ) : DartTaxonomy
        }

        // Allows providing concept ids -- necessary for lossless ser/de
        def apply(
            rootConcepts : Set[ DartConcept ],
            entries : Map[ TaxonomyId, DartTaxonomyEntry ],
        ) : DartTaxonomy = IndexedDartTaxonomy( rootConcepts, entries )

    }
}

trait DartClusterDI {
    this : DartTaxonomyDI =>

    case class DartNodeSimilarity(
        conceptId : TaxonomyId,
        score : Double,
    )

    object DartNodeSimilarity {
        def fromNodeSimilarity( nodeSimilarity : NodeSimilarity, taxonomy : DartTaxonomy ) : Option[ DartNodeSimilarity ] = {
            taxonomy.pathEntry( nodeSimilarity.path ).map( entry => {
                DartNodeSimilarity(
                    conceptId = entry.id,
                    score = nodeSimilarity.score,
                )
            } )
        }
    }

    case class DartCluster(
        id : String,
        score : Double,
        recommendedName : String,
        rankedWords : Seq[ String ],
        similarNodes : Seq[ DartNodeSimilarity ]
    )

    object DartCluster {
        def fromCluster( cluster : Cluster, taxonomy: DartTaxonomy ) : DartCluster = {
            DartCluster(
                id = cluster.id,
                score = cluster.score,
                recommendedName = cluster.recommendedName,
                rankedWords = cluster.rankedWords,
                similarNodes = cluster.similarNodes.flatMap( sn => {
                    DartNodeSimilarity
                      .fromNodeSimilarity( sn, taxonomy )
                      .toList
                } ),
            )
        }
    }
}
