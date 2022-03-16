package com.twosixtech.dart.taxonomy.explorer.models.wm

import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterConceptBridgeDeps, WmDartConceptDI}

trait WmDartClusterConceptBridgeDI
  extends DartClusterConceptBridgeDeps {
    this : WmDartConceptDI
      with CuratedClusterDI =>

    override lazy val clusterConceptBridge : DartClusterConceptBridge = WmDartClusterConceptBridge

    object WmDartClusterConceptBridge extends DartClusterConceptBridge {

        def sanitizeExample( ex : String ) : String = {
            ex.replaceAll( "[-_ ]+", " " ).trim.toLowerCase
        }

        def acceptPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept = {
            val sanitizedPhrase = sanitizeExample( phrase )
            val newMetadata = concept.metadata match {
                case None =>
                    WmConceptMetadata( Set( sanitizedPhrase ), Set.empty, Seq.empty, Seq.empty, Positive, Entity )
                case Some( metadata ) =>
                    metadata.copy( examples = metadata.examples + sanitizedPhrase )
            }

            concept.copy( metadata = Some( newMetadata ) )
        }

        def rejectPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept = {
            val sanitizedPhrase = sanitizeExample( phrase )
            val newMetadata = concept.metadata match {
                case None => None
                case Some( metadata ) =>
                    Some( metadata.copy( examples = metadata.examples - sanitizedPhrase ) )
            }

            concept.copy( metadata = newMetadata )
        }

        def conceptHasPhrase( concept : DartConcept, phrase : String ) : Boolean = {
            val sanitizedPhrase = sanitizeExample( phrase )
            concept.metadata match {
                case None => false
                case Some( metadata ) => metadata.examples.contains( sanitizedPhrase )
            }
        }

        override def conceptHasAnyPhrases(
            concept : DartConcept, relativeTo : Option[ CuratedCluster ] = None ) : Boolean = {
            relativeTo match {
                case None => concept.metadata.exists( _.examples.nonEmpty )
                case Some( cluster ) =>
                    cluster.cluster.rankedWords.exists( phrase => {
                        concept.metadata.exists( _.examples.contains( phrase ) )
                    } )
            }
        }
    }

}
