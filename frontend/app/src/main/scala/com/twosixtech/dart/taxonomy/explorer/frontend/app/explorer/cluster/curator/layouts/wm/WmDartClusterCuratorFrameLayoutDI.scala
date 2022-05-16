package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts.wm

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{ DartFlex, DartFlexBasic }
import com.twosixtech.dart.scalajs.layout.div.splitscreen.{ SplitScreen, SplitScreenMui }
import com.twosixtech.dart.scalajs.layout.form.select.Select
import com.twosixtech.dart.scalajs.layout.form.select.mui.SelectMui
import com.twosixtech.dart.scalajs.layout.form.textinput.{ TextInput, TextInputMui }
import com.twosixtech.dart.scalajs.layout.icon.Icons.SyncIconMui
import com.twosixtech.dart.scalajs.layout.text.{ Text, TextMui }
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.api.ClusteringApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.layouts.wm.WmDartClusterCuratorClusterLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts.DartClusterCuratorNavigationLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.{ DartClusterCuratorDI, DartClusterCuratorFrameLayoutDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.DartConceptFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm.WmDartConceptFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ DartComponentDI, DartStateDI }
import com.twosixtech.dart.taxonomy.explorer.models.{ CuratedClusterDI, DartConceptDeps, DartTaxonomyDI }
import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CallbackTo.confirm
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.postfixOps
import scala.scalajs.js

trait WmDartClusterCuratorFrameLayoutDI
  extends DartClusterCuratorFrameLayoutDeps {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartCircuitDeps
      with DartContextDeps
      with DartConceptDeps
      with DartConceptExplorerDI
      with DartClusterCuratorNavigationLayoutDI
      with DartConceptFrameDI
      with CuratedClusterDI
      with ClusteringApiDI
      with WmDartSerializationDI
      with DartTaxonomyDI
      with DartLoadingDI
      with DartLoadingInterfaceDI
      with DartLoadingInterfaceLayoutDI
      with WmDartConceptFrameLayoutDI
      with WmDartClusterCuratorClusterLayoutDI =>

    override type DartClusterCuratorFrameRenderContext = Unit
    override type DartClusterCuratorFrameLayoutState = DartClusterCuratorFrameLayoutBasic.State

    override val dartClusterCuratorFrameLayout : DartClusterCuratorFrameLayout = new DartClusterCuratorFrameLayoutBasic

    object DartClusterCuratorFrameLayoutBasic {
        case class State(
            jobTextValue : String = "",
            searchValue : String = "",
        )
    }

    class DartClusterCuratorFrameLayoutBasic extends DartClusterCuratorFrameLayout {

        import DartClusterCuratorFrameLayoutBasic.State

        override def render(
            scope : Scope, state : State,
            props : DartClusterCuratorFrame.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import scalacss.DevDefaults._
            object Styles extends StyleSheet.Inline {

                import dsl._

                val horizSpaced = style(
                    marginRight( 10 px )
                )

                val vertSpaced : StyleA = style(
                    marginBottom( 10 px )
                )

                val rescoreButton = style( marginLeft( 10 px ) )

                private val fullHeight = height( 100 %% )
                val container = style( fullHeight, overflow.hidden )
                val leftOuter = style( fullHeight, overflow.hidden )
                val leftInner = style( fullHeight, overflow.hidden, paddingRight( 10 px ), paddingBottom( 1 px ) )
                val right = style( fullHeight, overflow.hidden )

                val dangerousDropdown = style(
                    paddingLeft( 5.px ),
                    backgroundColor( red )
                )
            }
            Styles.addToDocument()

            import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

            val clustersEle : VdomElement = props.clusters match {
                case None => <.div()
                case Some( clusters ) =>
                    props.activeCluster match {
                        case None => <.div()
                        case Some( activeCluster ) =>
                            SplitScreenMui( new SplitScreen.Props(
                                divisionType = SplitScreen.Right,
                                independentScroll = false,
                                classes = SplitScreen.Classes(
                                    container = Styles.container.cName,
                                    left = Styles.leftOuter.cName,
                                    right = Styles.right.cName,
                                ),
                                childLeft = {
                                    if ( clusters.isEmpty ) EmptyVdom
                                    else <.div(
                                        Styles.leftInner.cName,
                                        dartClusterCuratorCluster( DartClusterCuratorCluster.Props(
                                            clusters( activeCluster ),
                                            activeCluster,
                                        ).toDartPropsRC( () ) ),
                                    )
                                },
                                childRight =
                                    props.conceptProps match {
                                        case None => ""
                                        case Some( props ) =>
                                            dartConceptFrame( props.toDartPropsRC( () ) )
                                    }
                            ) )
                    }

            }

            <.div(
                ^.style := js.Dictionary(
                    "display" -> "flex",
                    "flexFlow" -> "column",
                    "height" -> "100%",
                    "paddingTop" -> "10px",
                ),
                <.div(
                    props.jobId match {
                        case None => ""
                        case Some( id ) =>
                            <.div(
                                Styles.vertSpaced.cName,
                                TextMui( Text.Props( <.b( s"Current job: $id" ), size = types.Medium ) ),
                            )
                    },
                    DartFlexBasic( DartFlex.Props(
                        direction = types.Row,
                        align = types.AlignCenter,
                        classes = DartFlex.Classes(
                            container = Styles.vertSpaced.cName,
                            items = Styles.horizSpaced.cName,
                        ),
                        items = Vector(
                            DartFlex.FlexItem(
                                SelectMui.StringSelectMui(
                                    value = "* * * * *",
                                    items = ( Select.Item[ String ](
                                        "Discover Concepts",
                                        "* * * * *",
                                        Some( "* * * * *" ),
                                    ) +: props.tenants.map( tenantId => {
                                        Select.Item[ String ](
                                            tenantId,
                                            tenantId,
                                            Some( tenantId ),
                                        )
                                    } ) ).toVector,
                                    onChange = ( tenantId : String ) =>
                                      for {
                                          confirmed <- confirm(
                                              s"WARNING: this will reseed the clustering service with concepts pulled from documents in $tenantId, erasing all clustering data. Are you sure you wish to proceed?"
                                          )
                                          _ <- if ( confirmed ) props.startDiscovery( tenantId )
                                               else Callback()
                                      } yield (),
                                    classes = Select.Classes( Styles.dangerousDropdown.cName ),
                                ),
                            ),
                            DartFlex.FlexItem(
                                ButtonMui( Button.Props(
                                    "Recluster",
                                    props.recluster(),
                                    disabled = props.reclusterButtonDisabled || state.jobTextValue.nonEmpty,
                                ) ),
                            ),
                            DartFlex.FlexItem(
                                TextInputMui( TextInput.Props(
                                    Some( state.jobTextValue ),
                                    Some( v => scope.modState( s => s.copy( jobTextValue = v ) ) ),
                                    variant = TextInput.Outlined,
                                    placeholder = Some( "Cluster Job Id" ),
                                    size = types.Small,
                                ) ),
                            ),
                            DartFlex.FlexItem(
                                ButtonMui( Button.Props(
                                    "Retrieve Clusters",
                                    props.getJobResults( state.jobTextValue ) >> scope.modState( _.copy( jobTextValue = "" ) ),
                                    disabled = state.jobTextValue.isEmpty,
                                ) ),
                            ),
                            DartFlex.FlexItem(
                                IconButtonMui( IconButton.Props(
                                    icon = SyncIconMui(),
                                    onClick = props.rescore(),
                                    disabled = props.rescoreButtonDisabled,
                                ) ),
                                classes = DartFlex.ItemClasses( Styles.rescoreButton.cName )
                            ),
                            DartFlex.FlexItem(
                                <.div(),
                                DartFlex.Grow( 1 )
                            ),
                            DartFlex.FlexItem(
                                classes = DartFlex.ItemClasses(),
                                element = props.activeCluster match {
                                    case None => EmptyVdom
                                    case Some( activeCluster ) =>
                                        props.clusters match {
                                            case None => EmptyVdom
                                            case Some( clusters ) =>
                                                dartClusterCuratorNavigation(
                                                    DartClusterCuratorNavigation.Props(
                                                        clusters,
                                                        activeCluster,
                                                    ).toDartPropsRC( () )
                                                )
                                        }
                                }
                            ),
                            DartFlex.FlexItem(
                                <.div(),
                                DartFlex.Grow( 4 )
                            ),
                            DartFlex.FlexItem(
                                ButtonMui( Button.Props(
                                    "Clear",
                                    props.clear(),
                                    disabled = props.clearDisabled,
                                ) ),
                            ),
                        )
                    ) )
                ),
                <.div(
                    ^.style := js.Dictionary(
                        "position" -> "relative",
                        "flex" -> "1",
                        "overflow" -> "auto",
                    ),
                    ( if ( props.clusterPending ) EmptyVdom
                    else clustersEle ),
                    dartLoadingInterface( DartLoadingInterface.Props(
                        props.loadingState,
                        DartLoadingInterface.LightOverlay,
                    ).toDartPropsRC() )
                ),
            )
        }

        override val initialState : DartClusterCuratorFrameLayoutBasic.State = DartClusterCuratorFrameLayoutBasic.State()
    }

}
