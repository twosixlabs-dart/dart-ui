package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search

import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import japgolly.scalajs.react.Callback

trait ConceptSearchDeps {
    this : DartContextDeps
      with DartConceptDeps
      with DartComponentDI
      with ConceptSearchLayoutDeps
      with DartTaxonomyDI =>

    type ConceptSearchMetadataProps
    type ConceptSearchContextView
    type ConceptSearchState

    val conceptSearch : ConceptSearch

    trait ConceptSearch
      extends SimpleDartComponent[ ConceptSearch.Props, ConceptSearchRenderContext ] {

        def makeConceptSearch( props : ConceptSearch.Props )
          ( filter : (String, DartTaxonomyEntry) => Boolean ) : Set[ String ] => Vector[ ConceptSearch.SearchResult ] = {
            searchTerms => {
                import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPathFormat
                props.taxonomy.entries
                  .filter( entry => searchTerms.forall( searchTerm => filter( searchTerm, entry._2 ) ) )
                  .map( entry => ConceptSearch.SearchResult( entry._2.path.pathString, props.onSelectConcept( entry._1 ) ) )
                  .toVector
            }
        }

        def getSearchName( props : ConceptSearch.Props ) : Set[ String ] => Vector[ ConceptSearch.SearchResult ] = {
            searchTerms => {
                import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPathFormat
                props.taxonomy.entries
                  .filter( v => searchTerms.forall( searchTerm => v._2.concept.name.contains( searchTerm ) ) )
                  .map( entry => ConceptSearch.SearchResult( entry._2.path.pathString, props.onSelectConcept( entry._1 ) ) )
                  .toVector
            }
        }

    }

    object ConceptSearch {

        case class Classes(
            root : Option[ String ] = None,
            input : Option[ String ] = None,
            searchResults : Option[ String ] = None,
            searchResult : ResultClasses = ResultClasses(),
        )

        case class Props(
            taxonomy : DartTaxonomy,
            onSelectConcept : TaxonomyId => Callback,
            valueControl : Option[ (String, String => Callback) ] = None,
            classes : Classes = Classes(),
        )

        case class ResultClasses(
            all : Option[ String ] = None,
            byIndex : Option[ Int => String ] = None,
        )

        case class SearchResult(
            phrase : String,
            onSelect : Callback,
            classes : ResultClasses = ResultClasses(),
        )

        case class LayoutProps(
            searchName : Set[ String ] => Vector[ SearchResult ],
            metadataProps : ConceptSearchMetadataProps,
            valueControl : Option[ (String, String => Callback) ] = None,
            classes : Classes = Classes(),
        )
    }

}

trait ConceptSearchLayoutDeps {
    this : DartComponentDI with ConceptSearchDeps =>

    type ConceptSearchRenderContext
    type ConceptSearchLayoutState

    val conceptSearchLayout : ConceptSearchLayout

    trait ConceptSearchLayout
      extends DartLayoutComponent[
        ConceptSearch.LayoutProps,
        ConceptSearchRenderContext,
        ConceptSearchLayoutState,
      ]

}
