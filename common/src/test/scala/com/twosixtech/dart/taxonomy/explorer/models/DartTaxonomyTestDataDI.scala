package com.twosixtech.dart.taxonomy.explorer.models

trait DartTaxonomyTestDataDI {
    this : DartTaxonomyDI
      with WmDartConceptDI
      with UUIDTaxonomyIdDI =>

    object DartTaxonomyData {
        val concept1b1a = DartConcept( "concept-1-b-1-a", Set.empty )
        val concept1b1 = DartConcept( "concept-1-b-1", Set.empty )
          .copy( children = Set( concept1b1a ) )

        val concept1a = DartConcept( "concept-1-a", Set.empty )
        val concept1b = DartConcept( "concept-1-b", Set.empty )
          .copy( children = Set( concept1b1 ) )

        val concept1 = DartConcept( "concept-1", Set.empty )
          .copy( children = Set( concept1a, concept1b ) )

        val concept2 = DartConcept( "concept-2", Set.empty )
        val concept3 = DartConcept( "concept-3", Set.empty )

        val taxonomy : DartTaxonomy = DartTaxonomy( Set( concept1, concept2, concept3 ) )
    }

}
