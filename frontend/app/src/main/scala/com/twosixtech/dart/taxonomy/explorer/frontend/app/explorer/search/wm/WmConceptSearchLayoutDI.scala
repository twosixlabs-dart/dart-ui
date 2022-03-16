package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm

import com.twosixtech.dart.scalajs.layout.form.search.{SearchField, SearchFieldMui}
import com.twosixtech.dart.scalajs.layout.form.textinput.TextInput
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.ConceptSearchLayoutDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement

trait WmConceptSearchLayoutDI
  extends ConceptSearchLayoutDeps {
    this : WmConceptSearchDI
      with DartComponentDI
      with DartContextDeps =>

    override type ConceptSearchRenderContext = Unit
    override type ConceptSearchLayoutState = WmConceptSearchLayout.State

    override lazy val conceptSearchLayout : ConceptSearchLayout = new WmConceptSearchLayout

    class WmConceptSearchLayout extends ConceptSearchLayout {

        import WmConceptSearchLayout._

        override def render(
            scope : ScopeType,
            state : WmConceptSearchLayout.State, props : ConceptSearch.LayoutProps )(
            implicit renderProps : SnapshotType,
            context : DartContext ) : VdomElement = {

            val inputValue = props.valueControl match {
                case Some( (v, _) ) => Some( v )
                case None => state.searchTerm
            }

            val searchResults : Option[ Vector[ ConceptSearch.SearchResult ] ] = inputValue match {
                case None => None
                case Some( "" ) => None
                case Some( term ) =>
                    val terms = term.split( """\s+""" ).toSet
                    Some( ( props.searchName( terms )
                            ++ props.metadataProps.searchExamples( terms )
                            ++ props.metadataProps.searchPatterns( terms ) )
                      .groupBy( _.phrase )
                      .map( _._2.head )
                      .toVector
                      .sortBy( _.phrase )
                    )
            }

            SearchFieldMui(
                onSelect = scope.modState( _.copy( searchTerm = None ) ),
                textInput = TextInput.Props(
                    value = Some( inputValue.getOrElse( "" ) ),
                    onChange = props.valueControl match {
                        case Some( (_, cb) ) => Some( v => cb( v ) )
                        case None => Some( newVal => scope.modState( _.copy( searchTerm = Some( newVal ) ) ) )
                    },
                    variant = TextInput.Outlined,
                    size = types.Small,
//                    onBlur = Some( _ =>
//                        props.valueControl match {
//                            case Some( (_, cb) ) => cb( "" )
//                            case None => scope.modState( _.copy( searchTerm = None ) )
//                        }
//                    ),
//                    onEscape = Some(
//                        props.valueControl match {
//                            case Some( (_, cb) ) => cb( "" )
//                            case None => scope.modState( _.copy( searchTerm = None ) )
//                        }
//                    ),
                    classes = TextInput.Classes( input = props.classes.input ),
                ),
                results = searchResults.map( _.distinct.sortBy( _.phrase )
                  .zipWithIndex.map { case (rs, i) => {
                    SearchField.Result(
                        value = Right( rs.phrase ),
                        onSelect = rs.onSelect,
                        key = Some( rs.phrase ),
                        classes = SearchField.ResultClasses(
                            props.classes.searchResult.byIndex.map( _ ( i ) )
                        )
                    )
                } } ),
                classes = SearchField.Classes(
                    menu = props.classes.searchResults,
                    results = SearchField.ResultClasses( props.classes.searchResult.all )
                ),
            )
        }

        override val initialState : State = State( None )
    }

    object WmConceptSearchLayout {
        case class State(
            searchTerm : Option[ String ],
        )
    }

}
