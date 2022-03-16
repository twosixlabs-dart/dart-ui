package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.form.search.{SearchField, SearchFieldMui}
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.form.toggledtextinput.ToggledTextInput
import com.twosixtech.dart.scalajs.layout.icon.Icons
import com.twosixtech.dart.scalajs.layout.icon.Icons.SearchIconMui
import com.twosixtech.dart.scalajs.layout.text.Text
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.layouts.wm.WmDartClusterCuratorClusterLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.{DartClusterCuratorDI, DartClusterCuratorNavigationLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.CuratedClusterDI
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.postfixOps
import scala.util.Try

trait DartClusterCuratorNavigationLayoutDI
  extends DartClusterCuratorNavigationLayoutDeps {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with DartConceptExplorerDI
      with CuratedClusterDI
      with WmDartClusterCuratorClusterLayoutDI =>

    override type DartClusterCuratorNavigationRenderContext = Unit
    override type DartClusterCuratorNavigationLayoutState = GenericDartClusterCuratorNavigationLayout.State

    override val dartClusterCuratorNavigationLayout : DartClusterCuratorNavigationLayout = new GenericDartClusterCuratorNavigationLayout

    object GenericDartClusterCuratorNavigationLayout {
        sealed trait SearchState
        case object NoSearchState extends SearchState
        case class SetIndex( index : String ) extends SearchState
        case class SearchClusters( searchTerm : String ) extends SearchState

        case class State(
            clusterSearchState : SearchState,
        ) {
            def setIndexValue( newValue : String ) : State =
                this.copy( clusterSearchState = SetIndex( newValue ) )

            def setClusterSearchValue( newValue : String ) : State =
                this.copy( clusterSearchState = SearchClusters( newValue ) )

            def clearSearchState : State = this.copy( clusterSearchState = NoSearchState )
        }
    }

    class GenericDartClusterCuratorNavigationLayout extends DartClusterCuratorNavigationLayout {

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object Styles extends StyleSheet.Inline {

            import dsl._

            // Keep it at the width of the largest version of itself
            val searchSpan = style( width( 221 px ) )

            val centeredInput = style( textAlign.center )

            val paddedIndex = style( paddingLeft( 12 px ), paddingRight( 12 px ) )
        }
        Styles.addToDocument()

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render(
            scope : Scope, state : GenericDartClusterCuratorNavigationLayout.State,
            props : DartClusterCuratorNavigation.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            val indexInput = state.clusterSearchState match {
                case GenericDartClusterCuratorNavigationLayout.SetIndex( index ) => Some( index )
                case _ => None
            }

            val searchInputValue = state.clusterSearchState match {
                case GenericDartClusterCuratorNavigationLayout.SearchClusters( term ) => Some( term )
                case _ => None
            }

            val clusterSearchElement = {
                DartFlexBasic( DartFlex.Props(
                    classes = DartFlex.Classes( Styles.searchSpan.cName ),
                    direction = types.Row,
                    align = types.AlignCenter,
                    justify = types.JustifyCenter,
                    items = Vector(
                        DartFlex.FlexItem(
                            if ( searchInputValue.isEmpty ) {
                                ToggledTextInput(
                                    setAsInput = Some( indexInput.isDefined ),
                                    onClick = Some( scope.modState( _.setIndexValue( "" ) ) ),
                                    element = Left( new Text.Props(
                                        classes = Text.Classes( Styles.paddedIndex.cName ),
                                        element = s"${props.activeCluster + 1} / ${props.totalClusters}",
                                    ) ),
                                    input = TextInput.Props(
                                        classes = TextInput.Classes( input = Styles.centeredInput.cName ),
                                        value = indexInput,
                                        autoFocus = true,
                                        onChange = Some( ( v : String ) =>
                                            if ( v.trim.isEmpty ) scope.modState( _.setIndexValue( "" ) )
                                            else Try( v.toInt ).toOption match {
                                                case None => Callback()
                                                case Some( newIndex ) => scope.setState( state.setIndexValue( newIndex.toString ) )
                                            }
                                        ),
                                        onEnter = Some(
                                            scope.modState( _.clearSearchState ) >> ( indexInput.flatMap( v => Try( v.toInt ).toOption ) match {
                                                case None => Callback()
                                                case Some( newIndex ) => props.setActiveCluster( newIndex - 1 )
                                            } ),
                                        ),
                                        placeholder =
                                            Some( s"${props.activeCluster + 1} / ${props.totalClusters}" ),
                                        onBlur = Some( _ => scope.modState( _.clearSearchState ) ),
                                    ),
                                )
                            } else EmptyVdom,
                        ),
                        DartFlex.FlexItem(
                            if ( searchInputValue.isEmpty && indexInput.isEmpty ) IconButtonMui( new IconButton.Props(
                                icon = SearchIconMui( color = types.Primary ),
                                onClick = scope.modState( _.setClusterSearchValue( "" ) )
                            ) ) else if ( indexInput.isDefined ) EmptyVdom
                            else {
                                SearchFieldMui.apply(
                                    textInput = new TextInput.Props(
                                        value = searchInputValue,
                                        autoFocus = true,
                                        onChange = Some( v => scope.modState( _.setClusterSearchValue( v ) ) ),
                                        variant = TextInput.Outlined,
                                        placeholder = Some( "Search Clusters" ),
                                        size = types.Small,
                                        onBlur = Some( _ => scope.modState( _.clearSearchState ) ),
                                    ),
                                    results = {
                                        val res =
                                            if ( searchInputValue.getOrElse( "" ).trim.isEmpty ) Nil
                                            else props.clusterSearch( searchInputValue.get.trim )
                                        if ( res.isEmpty ) None
                                        else Some( res.map( v => {
                                            SearchField.Result(
                                                Right( v._1 ),
                                                props.setActiveCluster( v._2 )
                                            )
                                        } ).toVector )
                                    },
                                    onSelect = scope.modState( _.clearSearchState )
                                )
                            }
                        ),
                    )
                ) )
            }

            <.div(
                DartFlexBasic( DartFlex.Props(
                    direction = types.Row,
                    align = types.AlignCenter,
                    items = Vector(
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.FarthestLeftIconMui(
                                    color = if ( props.cantGoLeft ) types.Plain else types.Primary,
                                ),
                                onClick = scope.modState( _.clearSearchState ) >> props.firstCluster,
                                disabled = props.cantGoLeft,
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.LeftIconMui(
                                    color = if ( props.cantGoLeft ) types.Plain else types.Primary,
                                ),
                                onClick = scope.modState( _.clearSearchState ) >> props.prevCluster,
                                disabled = props.cantGoLeft,
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            clusterSearchElement,
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.RightIconMui(
                                    color = if ( props.cantGoRight ) types.Plain else types.Primary,
                                ),
                                onClick = scope.modState( _.clearSearchState ) >> props.nextCluster,
                                disabled = props.cantGoRight,
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.RightIconMui( color = types.Secondary ),
                                onClick = scope.modState( _.clearSearchState ) >> props.rejectClusterAndMoveOn,
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.DoubleRightIconMui( color = types.Primary ),
                                onClick = scope.modState( _.clearSearchState ) >> props.nextUncuratedCluster,
                                disabled = props.cantGoRight
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                icon = Icons.FarthestRightIconMui(
                                    color = if ( props.cantGoRight ) types.Plain else types.Primary,
                                ),
                                onClick = scope.modState( _.clearSearchState ) >> props.lastCluster,
                                disabled = props.cantGoRight,
                            ) ),
                        ),
                    ),
                ) ),
            )

        }

        override val initialState : GenericDartClusterCuratorNavigationLayout.State =
            GenericDartClusterCuratorNavigationLayout.State(
                clusterSearchState = GenericDartClusterCuratorNavigationLayout.NoSearchState,
            )
    }

}
