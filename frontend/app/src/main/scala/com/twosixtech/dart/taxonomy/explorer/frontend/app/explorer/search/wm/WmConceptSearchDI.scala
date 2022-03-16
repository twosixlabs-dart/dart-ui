package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.{ConceptSearchDeps, ConceptSearchLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, WmDartConceptDI}
import japgolly.scalajs.react.vdom.VdomElement

trait WmConceptSearchDI extends ConceptSearchDeps {
    this : DartContextDeps
      with ConceptSearchLayoutDeps
      with DartTaxonomyDI
      with DartComponentDI
      with WmDartConceptDI =>

    override type ConceptSearchContextView = Unit
    override type ConceptSearchMetadataProps = WmConceptSearch.MetadataProps
    override type ConceptSearchState = Unit

    override lazy val conceptSearch : ConceptSearch = WmConceptSearch

    object WmConceptSearch extends ConceptSearch {

        case class MetadataProps(
            searchExamples : Set[ String ] => Vector[ ConceptSearch.SearchResult ],
            searchPatterns : Set[ String ] => Vector[ ConceptSearch.SearchResult ],
        )

        override def render( props : ConceptSearch.Props )(
            implicit renderProps : ConceptSearchRenderContext,
            context : DartContext ) : VdomElement = {

            val layoutProps = ConceptSearch.LayoutProps(
                searchName = getSearchName( props ),
                MetadataProps(
                    searchExamples = makeConceptSearch( props ) { ( searchTerm, entry ) =>
                        entry.concept.metadata match {
                            case None => false
                            case Some( metadata ) =>
                                metadata.examples.exists( _.contains( searchTerm ) )
                        }
                    },
                    searchPatterns = makeConceptSearch( props ) { ( searchTerm, entry ) =>
                        entry.concept.metadata match {
                            case None => false
                            case Some( metadata ) =>
                                metadata.patterns.exists( _.contains( searchTerm ) )
                        }
                    },
                ),
                valueControl = props.valueControl,
                classes = props.classes,
            )

            conceptSearchLayout( layoutProps.toDartProps )
        }

    }
}
