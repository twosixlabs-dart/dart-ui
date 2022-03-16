package com.twosixtech.dart.taxonomy.explorer.models

trait DartClusterConceptBridgeDeps {
    this : DartConceptDeps
      with CuratedClusterDI =>

    val clusterConceptBridge : DartClusterConceptBridge

    trait DartClusterConceptBridge {

        def acceptPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept
        def undoAcceptPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept =
            rejectPhraseConceptUpdater( phrase )( concept )
        def rejectPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept
        def undoRejectPhraseConceptUpdater( phrase : String )( concept : DartConcept ) : DartConcept =
            acceptPhraseConceptUpdater( phrase )( concept )
        def conceptHasPhrase( concept : DartConcept, phrase : String ) : Boolean
        def conceptHasAnyPhrases( concept : DartConcept, relativeTo : Option[ CuratedCluster ] = None ) : Boolean
    }

}
