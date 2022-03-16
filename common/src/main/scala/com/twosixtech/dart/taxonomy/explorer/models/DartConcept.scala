package com.twosixtech.dart.taxonomy.explorer.models

import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.{ConceptIndex, ConceptPath, indexConcept}
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmConceptMetadata

trait ConceptDeps {
    type ConceptMetadataType
}

trait DartConceptDeps extends ConceptDeps {

    type DartConcept = Concept[ ConceptMetadataType ]
    val DefaultConceptMetadata : ConceptMetadataType

    /**
     * Filters out forbidden characters, which can change depending on use case/program
     * @param name
     * @return
     */
    def fixConceptName( name : String )  : String

    object DartConcept {
        def apply( name : String, children : Set[ DartConcept ], metadata : ConceptMetadataType = DefaultConceptMetadata ) : DartConcept = {
            Concept[ ConceptMetadataType ]( name, children, metadata )
        }

        def unapply( concept : DartConcept ) : Option[ (String, Set[ DartConcept ], ConceptMetadataType) ] = {
            Some( (concept.name, concept.children, concept.metadata) )
        }
    }
}

trait EmptyDartConceptDI extends DartConceptDeps {
    override type ConceptMetadataType = Unit
    override val DefaultConceptMetadata : Unit = ()

    override def fixConceptName( name : String ) : String = name
}

trait WmDartConceptDI extends DartConceptDeps {
    override type ConceptMetadataType = Option[ WmConceptMetadata ]
    override val DefaultConceptMetadata : Option[ WmConceptMetadata ] = None

    override def fixConceptName( name : String ) : String = {
        name.trim.toLowerCase
          .replaceAll( "[^a-z_\\s]+", "" )
          .replaceAll( "[\\s_]+", "_" )
    }
}
