package com.twosixtech.dart.taxonomy.explorer.publication

import com.twosixtech.dart.taxonomy.explorer.models.DartTaxonomyDI

import scala.concurrent.Future

trait OntologyPublicationServiceDeps {
    this : DartTaxonomyDI =>

    val OntologyPublicationService : OntologyPublicationService

    case class TV( publishedVersions : Option[ Int ], stagedVersions : Option[ Int ] )

    trait OntologyPublicationService {

        def stage( tenant : String, ontology : DartTaxonomy ) : Future[ Int ]

        def publishStaged( tenant : String ) : Future[ Option[ Int ] ]

        def retrieve( tenant : String, version : Option[ Int ] = None ) : Future[ Option[ DartTaxonomy ] ]

        def retrieveStaged( tenant : String, version : Option[ Int ] = None ) : Future[ Option[ DartTaxonomy ] ]

        def allTenants : Future[ Map[ String, TV ] ]

    }

}
