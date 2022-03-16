package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.layouts

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.modal.{DartModal, DartModalBasic}
import com.twosixtech.dart.scalajs.layout.div.panel.{DartPanel, DartPanelMui}
import com.twosixtech.dart.scalajs.layout.form.select.Select
import com.twosixtech.dart.scalajs.layout.form.select.mui.SelectMui.StringSelectMui
import com.twosixtech.dart.scalajs.layout.icon.Icons.{RightIconMui, SyncIconMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.api.OntologyPublicationApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.{TenantOntologyComponentDI, TenantOntologyComponentLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{VdomElement, html_<^}
import org.scalajs.dom.window

trait GenericTenantOntologyComponentLayoutDI
  extends TenantOntologyComponentLayoutDeps {
    this : TenantOntologyComponentDI
      with OntologyPublicationApiDI
      with DartComponentDI
      with DartContextDeps =>

    import GenericTenantOntologyComponentLayout.{Closed, ExportOpen, ImportOpen, ModalState}

    override type TenantOntologyComponentRenderContext = Unit
    override type TenantOntologyComponentLayoutState = ModalState

    override lazy val tenantOntologyComponentLayout : TenantOntologyComponentLayout = new GenericTenantOntologyComponentLayout

    import GenericTenantOntologyComponentLayoutClasses._

    class GenericTenantOntologyComponentLayout
      extends TenantOntologyComponentLayout {

        lazy val SelectComponent : Select[ String, _ ] = StringSelectMui

        import scalacss.DevDefaults._
        object Styles extends StyleSheet.Inline {
            import dsl._

            val root = style( overflow.hidden )
            val majorSection = style( marginBottom( 20 px ) )
            val minorSection = style( marginBottom( 10 px ) )
            val modalWindow : StyleA = style( padding( 30 px ), width( 100 %% ), height( 100 %% ) )
        }
        window.setTimeout( () => Styles.addToDocument(), 500 )

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render(
            scope : Scope,
            state : ModalState,
            props : TenantOntologyComponent.LayoutProps,
        )(
            implicit
            renderProps : Unit,
            context : DartContext,
        ) : VdomElement = {

            def openImportPublishModal( tenantId : String, version : Option[ Int ] = None ) : Callback =
                scope.setState( ImportOpen( tenantId, version, true ) )
            def openImportStageModal( tenantId : String, version : Option[ Int ] = None ) : Callback =
                scope.setState( ImportOpen( tenantId, version, false ) )
            def openPublishModal( tenantId : String ) : Callback =
                scope.setState( ExportOpen( tenantId, true ) )
            def openStageModal( tenantId : String ) : Callback =
                scope.setState( ExportOpen( tenantId, false ) )
            def closeModal : Callback = scope.setState( Closed )

            <.div(
                Styles.root.cName,
                DartFlexBasic(
                    direction = types.Row,
                    align = types.AlignCenter,
                    classes = DartFlex.Classes( container = Styles.majorSection.cName ),
                    items = Vector(
                        DartFlex.FlexItem(
                            TextMui( "Tenant Ontologies", size = types.Large, color = Some( types.Primary ) ),
                        ),
                        DartFlex.FlexItem(
                            IconButtonMui( IconButton.Props(
                                onClick = props.refresh,
                                icon = SyncIconMui(),
                                classes = IconButton.Classes( refreshButtonClass.cName ),
                            ) ),
                        ),
                    ),
                ),
                props.tenantOntologies.map {
                    case (tenantId, TenantVersion(publishedVersion, stagedVersion)) =>
                        <.div(
                            tenantOntologySectionsClass
                            and tenantOntologySectionClass( tenantId )
                            and Styles.majorSection,
                            ^.key := tenantId,
                            <.div(
                                <.div(
                                    Styles.minorSection.cName,
                                    TextMui(
                                        element = <.b( <.u( tenantId ) ),
                                        classes = Text.Classes( tenantNameClass.cName ),
                                    ),
                                ),
                                <.div(
                                    DartFlexBasic(
                                        direction = types.Row,
                                        align = types.AlignCenter,
                                        justify = types.JustifySpacedEvenly,
                                        classes = DartFlex.Classes( Styles.minorSection.cName ),
                                        items = Vector(
                                            DartFlex.FlexItem( ButtonMui(
                                                element = TextMui( "Stage current", size = types.Small ),
                                                onClick = openStageModal( tenantId ),
                                                size = Button.Small,
                                                classes = Button.Classes( stageOntologyButtonClass.cName ),
                                            ) ),
                                            DartFlex.FlexItem( if ( stagedVersion.nonEmpty ) ButtonMui(
                                                element = TextMui( "Publish staged", size = types.Small ),
                                                onClick = openPublishModal( tenantId ),
                                                size = Button.Small,
                                                classes = Button.Classes( publishOntologyButtonClass.cName )
                                            ) else EmptyVdom ),
                                        )
                                    ),
                                )
                            ),
                            <.div(
                                Styles.minorSection.cName,
                                publishedVersion.map( pv => {
                                    <.div(
                                        tenantOntologyPublishedsClass
                                        and tenantOntologyPublishedClass( tenantId ),
                                        DartFlexBasic(
                                            direction = types.Row,
                                            align = types.AlignCenter,
                                            items = Vector(
                                                DartFlex.FlexItem(
                                                    classes = DartFlex.ItemClasses( versionDropdown.cName ),
                                                    element = SelectComponent( Select.Props(
                                                        value = pv.toString,
                                                        items =
                                                            Select.Item[ String ](
                                                                element = TextMui(
                                                                    element = s"Published ontology (v${pv.toString})",
                                                                    classes = Text.Classes( versionItemsClass and versionItemClass( pv ) ),
                                                                ),
                                                                value = pv.toString,
                                                                key = Some( s"published-version-$pv" ),
                                                            ) +:
                                                            ( pv - 1 to 1 by -1 ).toVector.map ( ( i : Int ) => {
                                                                Select.Item[ String ](
                                                                    element = TextMui(
                                                                        element = s"Import version ${i}",
                                                                        size = types.Small,
                                                                        classes = Text.Classes( versionItemsClass and versionItemClass( i ) ),
                                                                    ),
                                                                    value = i.toString,
                                                                    key = Some( s"published-version-$i" ),
                                                                )
                                                            } ),
                                                        onChange = ( selectVersion ) => {
                                                            openImportPublishModal( tenantId, Some( selectVersion.toInt ))
                                                        },
                                                    ) ),
                                                ),
                                                DartFlex.FlexItem(
                                                    element = <.div(),
                                                    flex = DartFlex.Grow( 1 ),
                                                ),
                                                DartFlex.FlexItem(
                                                    element = ButtonMui(
                                                        element = TextMui(
                                                            <.span( "Import Latest", RightIconMui() ),
                                                            size = types.Small,
                                                        ),
                                                        onClick = openImportPublishModal( tenantId, None ),
                                                        size = Button.Small,
                                                        classes = Button.Classes( importLatestButtonClass.cName ),
                                                    ),
                                                )
                                            ),
                                        ),
                                    )
                                } ).getOrElse(
                                    <.span(
                                        TextMui( <.em( "No published versions" ) ),
                                    ),
                                ),
                            ),
                            <.div(
                                Styles.minorSection.cName,
                                stagedVersion.map( sv => {
                                    <.div(
                                        tenantOntologyStagedsClass
                                        and tenantOntologyStagedClass( tenantId ),
                                        DartFlexBasic(
                                            direction = types.Row,
                                            align = types.AlignCenter,
                                            items = Vector(
                                                DartFlex.FlexItem(
                                                    classes = DartFlex.ItemClasses( versionDropdown.cName ),
                                                    element = SelectComponent( Select.Props(
                                                        value = sv.toString,
                                                        items =
                                                            Select.Item[ String ](
                                                                element = TextMui(
                                                                    element = s"Staged ontology (v${sv.toString})",
                                                                    classes = Text.Classes(
                                                                        versionItemsClass
                                                                        and versionItemClass( sv ),
                                                                    ),
                                                                ),
                                                                value = sv.toString,
                                                                key = Some( s"staged-version-$sv" ),
                                                            ) +:
                                                            ( sv - 1 to 1 by -1 ).toVector.map( ( i : Int ) => {
                                                                Select.Item[ String ](
                                                                    element = TextMui(
                                                                        element = s"Version ${i}",
                                                                        size = types.Small,
                                                                        classes = Text.Classes(
                                                                            versionItemsClass
                                                                            and versionItemClass( i ),
                                                                        ),
                                                                    ),
                                                                    value = i.toString,
                                                                    key = Some( s"staged-version-$i" ),
                                                                )
                                                            } ),
                                                        onChange = ( selectVersion ) => {
                                                            openImportStageModal( tenantId, Some( selectVersion.toInt ))
                                                        },
                                                    ) ),
                                                ),
                                                DartFlex.FlexItem(
                                                    element = <.div(),
                                                    flex = DartFlex.Grow( 1 ),
                                                ),
                                                DartFlex.FlexItem(
                                                    element = ButtonMui(
                                                        element = TextMui(
                                                            <.span( "Import Latest", RightIconMui() ),
                                                            size = types.Small,
                                                        ),
                                                        onClick = openImportStageModal( tenantId, None ),
                                                        size = Button.Small,
                                                        classes = Button.Classes( importLatestButtonClass.cName ),
                                                    ),
                                                ),
                                            )
                                        ),
                                    )
                                } ).getOrElse(
                                    <.span(
                                        TextMui( <.em( "No staged versions" ) ),
                                    ),
                                ),
                            ),
                        )
                }.toVdomArray,
                DartModalBasic(
                    overlay = DartModal.GreyBlocking,
                    open = state match {
                        case Closed => false
                        case _ => true
                    },
                    element = DartPanelMui(
                        classes = DartPanel.Classes( Styles.modalWindow.cName ),
                        element = state match {
                            case Closed => EmptyVdom
                            case GenericTenantOntologyComponentLayout.ImportOpen( tenantId, version, isPublish ) =>
                                DartFlexBasic(
                                    classes = DartFlex.Classes( importModalClass.cName ),
                                    direction = types.Column,
                                    align = types.AlignCenter,
                                    justify = types.JustifySpacedEvenly,
                                    items = Vector(
                                        DartFlex.FlexItem(
                                            classes = DartFlex.ItemClasses( Styles.majorSection.cName ),
                                            element = TextMui( "Do you want to keep your current clustering work?" ),
                                        ),
                                        DartFlex.FlexItem(
                                            align = Some( types.AlignStretch ),
                                            element = DartFlexBasic(
                                                direction = types.Row,
                                                align = types.AlignStretch,
                                                justify = types.JustifySpacedEvenly,
                                                items = Vector(
                                                    DartFlex.FlexItem( ButtonMui(
                                                        "Keep Cluster State",
                                                        ( if ( isPublish ) {
                                                            props.importPublishedAndKeepClusterState( tenantId, version )
                                                        } else {
                                                            props.importStagedAndKeepClusterState( tenantId, version )
                                                        } ) >> closeModal,
                                                        classes = Button.Classes( selectKeepClusterStateButtonClass.cName ),
                                                    ) ),
                                                    DartFlex.FlexItem( ButtonMui(
                                                        "Clear Cluster State",
                                                        ( if ( isPublish ) {
                                                            props.importPublishedAndClearClusterState( tenantId, version )
                                                        } else {
                                                            props.importStagedAndClearClusterState( tenantId, version )
                                                        } ) >> closeModal,
                                                        classes = Button.Classes( selectClearClusterStateButtonClass.cName ),
                                                    ) ),
                                                    DartFlex.FlexItem( ButtonMui(
                                                        "Cancel",
                                                        closeModal,
                                                        classes = Button.Classes( importCancelButtonClass.cName ),
                                                    ) ),
                                                ),
                                            )
                                        )
                                    ),
                                )
                            case GenericTenantOntologyComponentLayout.ExportOpen( tenantId, isPublish ) =>
                                DartFlexBasic(
                                    classes = DartFlex.Classes( exportModalClass.cName ),
                                    direction = types.Column,
                                    align = types.AlignCenter,
                                    justify = types.JustifySpacedEvenly,
                                    items = Vector(
                                        DartFlex.FlexItem(
                                            classes = DartFlex.ItemClasses( Styles.majorSection.cName ),
                                            element =
                                                if ( isPublish ) <.div( TextMui( s"WARNING: this operation will trigger regrounding of all documents in $tenantId. Are you sure you want to publish the staged version?" ) )
                                                else <.div( TextMui( s"Are you sure you want to stage your current ontology to $tenantId?" ) ),
                                        ),
                                        DartFlex.FlexItem(
                                            align = Some( types.AlignStretch ),
                                            element = DartFlexBasic(
                                                direction = types.Row,
                                                align = types.AlignStretch,
                                                justify = types.JustifySpacedEvenly,
                                                items = Vector(
                                                    DartFlex.FlexItem( ButtonMui(
                                                        "Proceed",
                                                        ( closeModal >> ( if ( isPublish ) {
                                                            props.publishStagedIn( tenantId )
                                                        } else {
                                                            props.stageTo( tenantId )
                                                        } ) ),
                                                        classes = Button.Classes( exportConfirmationButton.cName ),
                                                    ) ),
                                                    DartFlex.FlexItem( ButtonMui(
                                                        "Cancel",
                                                        closeModal,
                                                        classes = Button.Classes( exportCancelButtonClass.cName ),
                                                    ) ),
                                                ),
                                            ),
                                        ),
                                    ),
                                )
                        },
                    ),
                ),
            )
        }

        override val initialState : ModalState = Closed
    }

    object GenericTenantOntologyComponentLayout {

        sealed trait ModalState
        case object Closed extends ModalState
        case class ImportOpen( tenantId : String, version : Option[ Int ], isPublish : Boolean )
          extends ModalState
        case class ExportOpen( tenantId : String, isPublish : Boolean ) extends ModalState

    }

}

object GenericTenantOntologyComponentLayoutClasses {

    val tenantOntologySectionsClass = "tenant-ontology-section"
    def tenantOntologySectionClass( tenant : String ) = s"tenant-ontology-section-${tenant.replace( ' ', '-' )}"

    val tenantNameClass = "tenant-name"

    val tenantOntologyPublishedsClass = "tenant-ontology-published"
    def tenantOntologyPublishedClass( tenant : String ) = s"tenant-ontology-published-${tenant.replace( ' ', '-' )}"
    val tenantOntologyStagedsClass = "tenant-ontology-staged"
    def tenantOntologyStagedClass( tenant : String ) = s"tenant-ontology-staged-${tenant.replace( ' ', '-' )}"

    val importLatestButtonClass = "import-latest"

    val versionDropdown = "version-dropdown"
    val versionItemsClass = "version-item"
    def versionItemClass( version : Int ) = s"version-item-${version}"

    val importModalClass = "import-modal-class"
    val selectKeepClusterStateButtonClass = "keep-cluster-state-button"
    val selectClearClusterStateButtonClass = "clear-cluster-state-button"
    val importCancelButtonClass = "import-cancel-button"

    val stageOntologyButtonClass = "stage-ontology-button"
    val publishOntologyButtonClass = "publish-ontology-button"

    val exportModalClass = "export-modal-class"
    val exportConfirmationButton = "export-confirmation-button"
    val exportCancelButtonClass = "export-cancel-button"

    val refreshButtonClass = "refresh-button"

}
