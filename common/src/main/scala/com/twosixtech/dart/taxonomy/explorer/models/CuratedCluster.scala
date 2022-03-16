package com.twosixtech.dart.taxonomy.explorer.models

import Taxonomy.ConceptPath

trait CuratedClusterDI {
    this : DartConceptDeps
      with DartTaxonomyDI
      with DartClusterDI
      with DartClusterConceptBridgeDeps =>

    case class CuratedCluster(
        cluster : DartCluster,
        acceptedPhrases : Map[ String, TaxonomyId ],
        rejectedPhrases : Map[ String, Option[ TaxonomyId ] ],
        selectedPhrases : Set[ String ],
        currentTarget : Option[ TaxonomyId ],
        selectedName : Option[ String ],
    )

    implicit class ClusterCuration( clusters : Seq[ CuratedCluster ] ) {
        def reclusterablePhrases( taxonomy : DartTaxonomy ) : Set[ String ] = {
            clusters.toSet.flatMap { ( cluster : CuratedCluster ) =>
                val phrases = cluster.cluster.rankedWords.toSet
                val acceptedPhrases = ( cluster.acceptedPhrases filter { case (phrase, id) =>
                    taxonomy.idEntry( id ) match {
                        case None => false
                        case Some( entry ) =>
                            clusterConceptBridge.conceptHasPhrase( entry.concept, phrase )
                    }
                } ).keySet
                val rejectedPhrases = ( cluster.rejectedPhrases filter {
                    case (_, None) => true
                    case (phrase, Some( id )) =>
                        taxonomy.idEntry( id ) match {
                            case None => false
                            case Some( entry ) =>
                                !clusterConceptBridge.conceptHasPhrase( entry.concept, phrase )
                        }
                } ).keySet
                phrases -- acceptedPhrases -- rejectedPhrases
            }
        }
    }

}



