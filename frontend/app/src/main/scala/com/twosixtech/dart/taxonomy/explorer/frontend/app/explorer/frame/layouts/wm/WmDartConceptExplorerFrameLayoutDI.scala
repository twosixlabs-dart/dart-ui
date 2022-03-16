package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.layouts.wm

import com.twosixtech.dart.scalajs.layout.button.filebutton.FileButton
import com.twosixtech.dart.scalajs.layout.button.filebutton.mui.FileButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.panel.{DartPanel, DartPanelMui}
import com.twosixtech.dart.scalajs.layout.div.splitscreen.{SplitScreen, SplitScreenMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.layout.types.Column
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.layouts.DartLoadingInterfaceLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.DartConceptFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm.WmDartConceptFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.{DartConceptExplorerFrameDI, DartConceptExplorerFrameLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm.{WmConceptSearchDI, WmConceptSearchLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts.GenericStateAccessComponentLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.DartConceptTreeDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.layouts.GenericDartConceptTreeLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.layouts.GenericTenantOntologyComponentLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, CallbackTo, ReactEventFromInput}
import org.scalajs.dom.{File, FileReader}

import scala.language.postfixOps
import scala.scalajs.js


trait WmDartConceptExplorerFrameLayoutDI
  extends DartConceptExplorerFrameLayoutDeps {
    this : DartConceptExplorerFrameDI
      with DartConceptTreeDI
      with DartComponentDI
      with DartContextDeps
      with DartComponentDI
      with DartConceptFrameDI
      with WmDartConceptFrameLayoutDI
      with GenericDartConceptTreeLayoutDI
      with DartLoadingInterfaceDI
      with DartLoadingInterfaceLayoutDI
      with WmConceptSearchDI
      with TenantOntologyComponentDI
      with GenericTenantOntologyComponentLayoutDI
      with StateAccessComponentDI
      with GenericStateAccessComponentLayoutDI
      with WmConceptSearchLayoutDI =>

    override type DartConceptExplorerFrameRenderContext = Unit
    override type DartConceptExplorerFrameLayoutState = Option[ File ]
    override lazy val dartConceptExplorerFrameLayout : DartConceptExplorerFrameLayout = new DartConceptExploreFrameLayoutBasic

    class DartConceptExploreFrameLayoutBasic
      extends DartConceptExplorerFrameLayout {

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object Styles extends StyleSheet.Inline {
            import dsl._

            val fullHeight = height( 100 %% )
            val fullHeightStyle = style( fullHeight )
            val splitScreenContainer = style( fullHeight, paddingTop( 15 px ), position.relative )

            val fullWidth = style( width( 100 %% ) )

            val container = style( fullHeight )
            val left = style( fullHeight, overflowY.auto )
            val right = style( fullHeight )

            val panel = style(
                fullHeight,
                padding( 20 px ),
            )

            val statePanel = style( padding( 10 px ), marginBottom( 10 px ) )

            val paddedRight = style( fullHeight, paddingRight( 10 px ) )

            val spacedButton = style(
                marginRight( 10 px )
            )
        }
        Styles.addToDocument()

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render( scope : Scope, state : Option[ File ], props : DartConceptExplorerFrame.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import props._

            val controlMenu = {
                DartFlexBasic( DartFlex.Props(
                    align = types.AlignCenter,
                    items = Vector(
                        DartFlex.FlexItem(
                            ButtonMui( Button.Props(
                                onClick = downloadTaxonomy(),
                                element = "Download Taxonomy",
                                classes = Button.Classes( Styles.spacedButton.cName )
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            FileButtonMui( FileButton.Props(
                                state match {
                                    case None => "Choose Taxonomy File"
                                    case Some( file ) => file.name.take( 20 )
                                },
                                ( ( e : ReactEventFromInput ) => {
                                    val files = e.target.files
                                    if ( files.length == 0 ) scope.setState( None )
                                    else {
                                        val file = files.item( 0 )
                                        if ( file.name.endsWith( ".yml" ) || file.name.endsWith( ".yaml" ) ) {
                                            scope.setState( Some( file ) )
                                        } else Callback()
                                    }
                                } ),
                                classes = FileButton.Classes(
                                    Styles.spacedButton.cName,
                                )
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            ButtonMui( Button.Props(
                                onClick = ( {
                                    if ( state.isDefined ) {
                                        Callback {
                                            val reader = new FileReader()
                                            reader.readAsText( state.get )
                                            reader.onload = _ => {
                                                val text = reader.result.asInstanceOf[ String ]
                                                props.uploadTaxonomy( text ).runNow()
                                            }
                                        } >> scope.setState( None )
                                    } else Callback()
                                } ),
                                element = "Upload Taxonomy",
                                disabled = state.isEmpty,
                                classes = Button.Classes( Styles.spacedButton.cName )
                            ) ),
                        ),
                        DartFlex.FlexItem(
                            <.span(),
                            flex = DartFlex.Grow( 1 ),
                        ),
                        DartFlex.FlexItem(
                            conceptSearch( props.searchProps.toDartPropsRC() ),
                        )
                    )
                ) )
            }

            <.div(
                Styles.splitScreenContainer.cName,
                SplitScreenMui( SplitScreen.Props(
                    divisionType = SplitScreen.NarrowRight,
                    independentScroll = false,
                    classes = SplitScreen.Classes(
                        container = Styles.container.cName,
                        left = Styles.left.cName,
                        right = Styles.right.cName,
                    ),
                    childLeft = <.div(
                        Styles.paddedRight.cName,
                        SplitScreenMui( SplitScreen.Props(
                            divisionType = SplitScreen.NarrowLeft,
                            independentScroll = false,
                            classes = SplitScreen.Classes(
                                container = Styles.container.cName,
                                left = Styles.left.cName,
                                right = Styles.right.cName,
                            ),
                            childLeft = <.div(
                                DartPanelMui(
                                    element = tenantOntologyComponent( TenantOntologyComponent.Props().toDartPropsRC() ),
                                    classes = DartPanel.Classes( Styles.statePanel.cName )
                                ),
                                DartPanelMui(
                                    element = stateAccessComponent( StateAccessComponent.Props().toDartPropsRC() ),
                                    classes = DartPanel.Classes( Styles.statePanel.cName )
                                ),
                            ),
                            childRight = DartPanelMui( new DartPanel.Props(
                                classes = DartPanel.Classes(
                                    Styles.panel.cName,
                                ),
                                element =
                                    DartFlexBasic( new DartFlex.Props(
                                        classes = DartFlex.Classes(
                                            container = s"${Styles.container.htmlClass} ${Styles.fullWidth.htmlClass}".cName,
                                            items = Styles.fullWidth.htmlClass.cName,
                                        ),
                                        direction = Column,
                                        items = Vector(
                                            DartFlex.FlexItem(
                                                flex = DartFlex.NoFlex,
                                                element = controlMenu,
                                            ),
                                            DartFlex.FlexItem(
                                                element = dartConceptTree( DartConceptTree.Props().toDartPropsRC( () ) ),
                                                flex = DartFlex.Grow( 1 ),
                                            ),
                                        ),
                                    ) ),
                            ) ),
                        ) ),
                    ),
                    childRight =
                      <.div(
                          ^.style := ( js.Dictionary( "height" -> "100%" ) ),
                          conceptFrameProps match {
                              case None => ""
                              case Some( cfp ) => dartConceptFrame( cfp.toDartPropsRC( () ) )
                          },
                      ),
                ) ),
                dartLoadingInterface( DartLoadingInterface.Props(
                    props.loadingProps,
                    DartLoadingInterface.LightOverlay,
                ).toDartPropsRC() ),
            )

        }

        override val initialState : Option[ File ] = None
    }
}
