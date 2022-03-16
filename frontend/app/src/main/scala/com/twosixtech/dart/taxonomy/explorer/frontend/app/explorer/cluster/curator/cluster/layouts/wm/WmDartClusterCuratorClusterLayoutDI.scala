package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.layouts.wm

import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.panel.{DartPanel, DartPanelMui}
import com.twosixtech.dart.scalajs.layout.form.checkbox.CheckBox
import com.twosixtech.dart.scalajs.layout.form.checkbox.mui.CheckBoxMui
import com.twosixtech.dart.scalajs.layout.form.label.mui.InputLabelMui
import com.twosixtech.dart.scalajs.layout.form.radio.Radio
import com.twosixtech.dart.scalajs.layout.form.radio.mui.RadioMui
import com.twosixtech.dart.scalajs.layout.form.select.multiselect.MultiSelect.SelectEntry
import com.twosixtech.dart.scalajs.layout.form.select.multiselect.{BasicMultiSelect, MultiSelect}
import com.twosixtech.dart.scalajs.layout.form.textinput.TextInput
import com.twosixtech.dart.scalajs.layout.form.toggledtextinput.ToggledTextInput
import com.twosixtech.dart.scalajs.layout.icon.Icons.CloseCircleOutlineIconMui
import com.twosixtech.dart.scalajs.layout.icon.{CheckCircleOutlineIconMuiRaw, CloseCircleOutlineIconMuiRaw, Icons}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.DartClusterCuratorClustorLayoutDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.wm.{WmConceptSearchDI, WmConceptSearchLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterDI, DartTaxonomyDI, UUIDTaxonomyIdDI, WmDartConceptDI}
import japgolly.scalajs.react.{Callback, ReactMouseEvent}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^.{<, _}
import org.scalajs.dom.window

import scala.collection.SortedMap
import scala.collection.immutable.ListMap
import scala.language.postfixOps
import scala.scalajs.js

trait WmDartClusterCuratorClusterLayoutDI
  extends DartClusterCuratorClustorLayoutDeps {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with WmDartConceptDI
      with DartTaxonomyDI
      with DartClusterDI
      with UUIDTaxonomyIdDI
      with WmDartClusterConceptBridgeDI
      with DartConceptExplorerDI
      with WmConceptSearchDI
      with WmConceptSearchLayoutDI
      with CuratedClusterDI =>

    override type DartClusterCuratorClusterRenderContext = Unit
    override type DartClusterCuratorClusterLayoutState = Option[ String ]

    override val dartClusterCuratorClusterLayout : DartClusterCuratorClusterLayout = new WmDartClusterCuratorClusterLayoutClass

    class WmDartClusterCuratorClusterLayoutClass extends DartClusterCuratorClusterLayout {

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._

        object Styles extends StyleSheet.Inline {

            import dsl._

            val root = style( height( 100 %% ) )
            val rootPanel = style( height( 100 %% ), padding( 20 px ) )
            val clusterSection = style( height( 100 %% ), padding( 10 px ) )
            val recommendedPhrases = style( height( 100 %% ), width( 100 %% ), border( 1 px, solid, darkgray ), overflowY.auto, overflowX.hidden )
            val conceptSection = style( height( 100 %% ), width( 400 px ), marginLeft( 20 px ), marginRight( 50 px ), overflow.auto )

            val relativeFullheight = style( position.relative, height( 100 %% ), overflow.hidden )
            val selectionCurationButtons = style( position.absolute, left( 0 px ), marginTop( -12 px ), top( 50 %% ) )
            val visibleOverflow = style( overflow.visible )
            val curationButtons = style( height( 24 px ), backgroundColor.white, marginLeft( 10 px ), borderRadius( 12 px ) )
            val targetDiv = style( height( 50 px ), marginBottom( 10 px ) )
            val fullHeight = style( height( 100 %% ) )
            val fullWidth = style( width( 100 %% ) )
            val noOverflow = style( overflow.hidden )
            val verticalOverflowAuto = style( overflowY.auto )
            val buttonStyle = style( marginRight( 5 px ) )
            val bufferBottom = style( marginBottom( 10 px ) )
            val header = style( paddingBottom( 15 px ) )
            val radioGroup = style( display.inlineBlock )
            val radio = style( display.inlineBlock, marginRight( 10 px ) )
            val recommendedConcept = style( width( 100 %% ), overflow.auto )
            val invisible = style( visibility.hidden )
            val spaceOnLeft = style( marginLeft( 10 px ) )
            val pointerCursor = style( &.hover( cursor.pointer ) )
            val hoverablePhrase =
                style(
                    paddingTop( 1.px ),
                    paddingBottom( 1.px ),
                    &.hover(
                        borderTop( 1.px, dotted, lightgray ),
                        borderBottom( 1.px, dotted, lightgray ),
                        padding( 0.px ),
                    ),
                )
        }

        window.setTimeout( ( ) => Styles.addToDocument(), 1000 )

        override def render(
            scope : Scope, state : Option[ String ],
            props : DartClusterCuratorCluster.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import DartClusterCuratorClusterLayoutClasses._

            import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

            val acceptedPhrases = props.acceptedPhrases.keySet
            val rejectedPhrases = props.rejectedPhrases.keySet

            val phrasesSet = props.phrases.toSet
            val uncuratedPhrases = phrasesSet -- acceptedPhrases -- rejectedPhrases

            val allSelected = phrasesSet == props.selectedPhrases
            val acceptedSelected = acceptedPhrases.nonEmpty && acceptedPhrases.subsetOf( props.selectedPhrases )
            val rejectedSelected = rejectedPhrases.nonEmpty && rejectedPhrases.subsetOf( props.selectedPhrases )
            val uncuratedSelected = uncuratedPhrases.nonEmpty && uncuratedPhrases.subsetOf( props.selectedPhrases )

            val nameSection = <.div(
                ToggledTextInput(
                    element = Left( Text.Props( <.b( clusterNameClass.cName, props.name ), size = types.Large ) ),
                    input = TextInput.Props(
                        value = state,
                        onChange = Some( newVal => scope.setState( Some( newVal ) ) ),
                        autoFocus = true,
                        size = types.Small,
                        variant = TextInput.Outlined,
                    ),
                    onClick = Some( scope.setState( Some( props.name ) ) ),
                    onBlur = Some( scope.setState( None ) ),
                    onEnter = {
                        val trimmedName = state.getOrElse( "" ).trim
                        Some {
                            if ( trimmedName.isEmpty || trimmedName == props.name ) scope.setState( None )
                            else props.setName( trimmedName ) >> scope.setState( None )
                        }
                    },
                ),
            )

            val curateOrRejectSection = <.span(
                RadioMui(
                    value = if ( phrasesSet == rejectedPhrases ) "reject" else "restore",
                    horizontal = true,
                    color = types.Primary,
                    onChange = {
                        case "reject" => props.rejectNode()
                        case "restore" => props.restoreNode()
                    },
                    classes = Radio.Classes(
                        root = Styles.radioGroup.cName,
                        items = Radio.ItemClasses( root = Styles.radio.cName ),
                    ),
                    items = Vector(
                        Radio.Item(
                            value = "restore",
                            label = TextMui( Text.Props( "Curate Cluster" ) ),
                            classes = Radio.ItemClasses( button = curateClusterButtonClass.cName )
                        ),
                        Radio.Item(
                            value = "reject",
                            label = TextMui( Text.Props( "Reject Cluster" ) ),
                            classes = Radio.ItemClasses( button = rejectClusterButtonClass.cName )
                        ),
                    ),
                ),
            )

            val recommendedPhrasesHeader = <.div(
                <.div(
                    InputLabelMui(
                        label = TextMui( "Select All", size = types.ExtraSmall ),
                        inputElement = CheckBoxMui(
                            checked = allSelected,
                            disabled = phrasesSet.isEmpty,
                            onClick =
                                if ( allSelected ) props.clearSelection
                                else props.selectAll,
                            classes = CheckBox.Classes(
                                root = Some( selectAllCheckBoxClass )
                            ),
                        ),
                    ),
                    InputLabelMui(
                        label = TextMui( "Select Uncurated", size = types.ExtraSmall ),
                        inputElement = CheckBoxMui(
                            checked = uncuratedSelected,
                            disabled = uncuratedPhrases.isEmpty,
                            onClick =
                                if ( uncuratedSelected ) props.clearUncuratedSelection
                                else props.selectUncurated,
                            classes = CheckBox.Classes( root = Some( selectUncuratedCheckBoxClass ) )
                        ),
                    ),
                    InputLabelMui(
                        label = TextMui( "Select Accepted", size = types.ExtraSmall ),
                        inputElement = CheckBoxMui(
                            checked = acceptedSelected,
                            disabled = acceptedPhrases.isEmpty,
                            onClick =
                                if ( acceptedSelected ) props.clearAcceptedSelection
                                else props.selectAccepted,
                            classes = CheckBox.Classes( root = Some( selectAcceptedCheckBoxClass ) )
                        ),
                    ),
                    InputLabelMui(
                        label = TextMui( "Select Rejected", size = types.ExtraSmall ),
                        inputElement = CheckBoxMui(
                            checked = rejectedSelected,
                            disabled = rejectedPhrases.isEmpty,
                            onClick =
                                if ( rejectedSelected ) props.clearRejectedSelection
                                else props.selectRejected,
                            classes = CheckBox.Classes( root = Some( selectRejectedCheckBoxClass ) )
                        ),
                    ),
                    InputLabelMui(
                        label = TextMui( "Clear", size = types.ExtraSmall ),
                        inputElement = IconButtonMui( IconButton.Props(
                            icon = CloseCircleOutlineIconMui( color = types.Plain ),
                            onClick = props.clearSelection,
                            color = types.Plain,
                            classes = IconButton.Classes(
                                Some( clearSelectionButtonClass ),
                            ),
                            disabled = props.selectedPhrases.isEmpty,
                        ) ),
                    ),
                ),
            )

            val headerSection = <.div(
                Styles.header.cName,
                nameSection,
                curateOrRejectSection,
                recommendedPhrasesHeader,
            )

            def curateSelectedPhrasesButtons(
                acceptCallback : Callback,
                rejectCallback : Callback,
                restoreCallback : Callback,
                useNameCallback : Callback,
                acceptClassName : String,
                rejectClassName : String,
                restoreClassName : String,
                useNameClassName : String,
                isInvisible : Boolean,
                isAccepted : Boolean,
                isRejected : Boolean,
                isUncurated : Boolean,
                useNameDisabled : Option[ Boolean ],
            ) : VdomElement = {

                <.div(
                    Styles.curationButtons and
                    ( if ( isInvisible ) Styles.invisible.cName
                    else None.cName ),
                    ^.onMouseDown ==> ( _.stopPropagationCB ),
                    DartFlexBasic( DartFlex.Props(
                        align = types.AlignCenter,
                        classes = DartFlex.Classes( container = Styles.fullHeight.cName and Styles.visibleOverflow ),
                        items = Vector(
                            DartFlex.FlexItem(
                                IconButtonMui( IconButton.Props(
                                    size = types.Medium,
                                    classes = IconButton.Classes( Styles.buttonStyle and acceptClassName ),
                                    icon = Icons.CheckCircleOutlineIconMui(),
                                    style = IconButton.Solid,
                                    disabled = isAccepted,
                                    onClick = acceptCallback,
                                ) ),
                            ),
                            DartFlex.FlexItem(
                                IconButtonMui( IconButton.Props(
                                    size = types.Medium,
                                    classes = IconButton.Classes( Styles.buttonStyle.htmlClass and rejectClassName ),
                                    icon = Icons.CloseCircleOutlineIconMui(),
                                    style = IconButton.Solid,
                                    disabled = isRejected,
                                    onClick = rejectCallback,
                                ) ),
                            ),
                            DartFlex.FlexItem(
                                IconButtonMui( IconButton.Props(
                                    size = types.Medium,
                                    classes = IconButton.Classes( Styles.buttonStyle.htmlClass and restoreClassName ),
                                    icon = Icons.RestoreIconMui(),
                                    style = IconButton.Solid,
                                    disabled = isUncurated,
                                    onClick = restoreCallback,
                                ) ),
                            ),
                            useNameDisabled match {
                                case None => DartFlex.FlexItem( EmptyVdom )
                                case Some( disabled ) =>
                                    DartFlex.FlexItem(
                                        IconButtonMui( IconButton.Props(
                                            size = types.Medium,
                                            classes = IconButton.Classes( Styles.buttonStyle and useNameClassName ),
                                            icon = Icons.LabelIconMui(),
                                            style = IconButton.Solid,
                                            disabled = disabled,
                                            onClick = useNameCallback,
                                        ) ),
                                    )
                            },
                        ),
                    ) ),
                )
            }

            val recommendedPhrasesSection = <.div(
                Styles.relativeFullheight.cName,
                if ( props.selectedPhrases.size > 1 ) {
                    <.div(
                        Styles.selectionCurationButtons.cName,
                        curateSelectedPhrasesButtons(
                            acceptCallback = props.acceptSelection,
                            rejectCallback = props.rejectSelection,
                            restoreCallback = props.restoreSelection,
                            useNameCallback = Callback(),
                            acceptClassName = selectedPhrasesAcceptButtonClass,
                            rejectClassName = selectedPhrasesRejectButtonClass,
                            restoreClassName = selectedPhrasesRestoreButtonClass,
                            useNameClassName = "",
                            isInvisible = props.selectedPhrases.size < 2,
                            isAccepted = acceptedPhrases.nonEmpty && props.selectedPhrases.subsetOf( acceptedPhrases ),
                            isRejected = rejectedPhrases.nonEmpty && props.selectedPhrases.subsetOf( rejectedPhrases ),
                            isUncurated =
                                props.selectedPhrases.intersect( acceptedPhrases ++ rejectedPhrases ).isEmpty,
                            useNameDisabled = None,
                        )
                    )
                } else EmptyVdom,
                <.div(
                    Styles.recommendedPhrases.cName,
                    BasicMultiSelect.StringMultiSelect(
                        onChange = ( newValues : Map[ String, Boolean ] ) =>
                            props.selectPhrases( newValues.filter( _._2 ).keys.toList )
                            >> props.unselectPhrases( newValues.filter( !_._2 ).keys.toList ),
                        mode = MultiSelect.MultiDefault,
                        classes = MultiSelect.Classes( Styles.fullWidth.cName ),
                        entries = ListMap.apply( props.phrases.map( phrase => phrase -> {
                            val isAccepted = props.acceptedPhrases.contains( phrase )
                            val isRejected = props.rejectedPhrases.contains( phrase )
                            val isSelected = props.selectedPhrases.contains( phrase )
                            val phraseTag = <.span {
                                if ( isAccepted ) <.b( phrase )
                                else if ( isRejected ) <.em( phrase )
                                else phrase
                            }
                            SelectEntry(
                                selected = props.selectedPhrases.contains( phrase ),
                                key = Some( phrase ),
                                element = <.div(
                                    Styles.hoverablePhrase and
                                    recommendedPhrasesClass and
                                    ( if ( isAccepted ) acceptedPhrasesClass else "" ) and
                                    ( if ( isRejected ) rejectedPhrasesClass else "" ) and
                                    ( if ( isSelected ) selectedPhrasesClass else "" ),
                                    HoverButton(
                                        left = true,
                                        persist = props.selectedPhrases.size == 1,
                                        element = <.span(
                                            Styles.spaceOnLeft.cName,
                                            TextMui( Text.Props(
                                                element = phraseTag,
                                                color =
                                                    if ( isAccepted ) Some( types.Primary )
                                                    else if ( isRejected ) Some( types.Secondary )
                                                    else None,
                                                classes = Text.Classes( Some( recommendedPhrasePhraseTextClass ) )
                                            ) ),
                                            if ( isAccepted )
                                                TextMui( Text.Props(
                                                    element = {
                                                        <.span(
                                                            ^.onMouseDown ==> ( _.stopPropagationCB ),
                                                            ^.onClick --> props.acceptedPhrases( phrase ).target,
                                                            s"/${props.acceptedPhrases( phrase ).path.mkString( "/" )}"
                                                        )
                                                    },
                                                    color = Some( types.Primary ),
                                                    size = types.Small,
                                                    classes = Text.Classes(
                                                        Some(
                                                            acceptedPhraseTargetClass
                                                            and acceptedPhraseTargetTargetButton
                                                            and Styles.spaceOnLeft
                                                            and Styles.pointerCursor,
                                                        ),
                                                    ),
                                                ) )
                                            else EmptyVdom
                                        ),
                                        button =
                                            if ( isSelected && props.selectedPhrases.size == 1 ) Some( curateSelectedPhrasesButtons(
                                                acceptCallback = props.acceptSelection,
                                                rejectCallback = props.rejectSelection,
                                                restoreCallback = props.restoreSelection,
                                                useNameCallback = props.setName( phrase ),
                                                acceptClassName = selectedPhrasesAcceptButtonClass,
                                                rejectClassName = selectedPhrasesRejectButtonClass,
                                                restoreClassName = selectedPhrasesRestoreButtonClass,
                                                useNameClassName = recommendedPhraseUseNameButtonClass,
                                                isInvisible =
                                                    props.selectedPhrases.size > 1 ||
                                                    ( props.selectedPhrases.size == 1 && !props.selectedPhrases.contains( phrase ) ),
                                                isAccepted = isAccepted,
                                                isRejected = isRejected,
                                                isUncurated = !isAccepted && !isRejected,
                                                useNameDisabled = Some( props.name == phrase ),
                                            ) )
                                            else Some( curateSelectedPhrasesButtons(
                                                acceptCallback = props.acceptPhrases( Seq( phrase ) ),
                                                rejectCallback = props.rejectPhrases( Seq( phrase ) ),
                                                restoreCallback = props.restorePhrases( Seq( phrase ) ),
                                                useNameCallback = props.setName( phrase ),
                                                acceptClassName = recommendedPhraseAcceptButtonClass,
                                                rejectClassName = recommendedPhraseRejectButtonClass,
                                                restoreClassName = recommendedPhraseRestoreButtonClass,
                                                useNameClassName = recommendedPhraseUseNameButtonClass,
                                                isInvisible =
                                                    props.selectedPhrases.size > 1 ||
                                                    ( props.selectedPhrases.size == 1 && !props.selectedPhrases.contains( phrase ) ),
                                                isAccepted = isAccepted,
                                                isRejected = isRejected,
                                                isUncurated = !isAccepted && !isRejected,
                                                useNameDisabled = Some( props.name == phrase ),
                                            ) ),
                                    ),
                                )
                            )
                        } ) : _*,
                    ) ),
                ),
            )

            val clusterSection = DartFlexBasic( DartFlex.Props(
                direction = types.Column,
                classes = DartFlex.Classes( Styles.clusterSection.cName ),
                items = Vector(
                    DartFlex.FlexItem(
                        element = headerSection,
                        classes = DartFlex.ItemClasses( Styles.noOverflow.cName ),
                    ),
                    DartFlex.FlexItem(
                        element = recommendedPhrasesSection,
                        classes = DartFlex.ItemClasses( Styles.fullHeight and Styles.fullWidth ),
                        flex = DartFlex.Grow( 1 ),
                    )
                ),
            ) )

            val conceptSection = DartFlexBasic( DartFlex.Props(
                direction = types.Column,
                classes = DartFlex.Classes( container = Styles.fullHeight.cName ),
                items = Vector(
                    DartFlex.FlexItem(
                        element = <.div(
                            <.div(
                                Styles.targetDiv.cName,
                                props.target.map( ( path : ConceptPath ) => {
                                    <.div(
                                        <.div( TextMui( Text.Props(
                                            element = <.b( "Target" ),
                                        ) ) ),
                                        <.div( TextMui( Text.Props(
                                            element = <.b( s"/${path.mkString( "/" )}" ),
                                            color = Some( types.Primary ),
                                            classes = Text.Classes( conceptTargetClass.cName ),
                                        ) ) ),
                                    )
                                } ).getOrElse(
                                    DartFlexBasic( DartFlex.Props(
                                        align = types.AlignCenter,
                                        classes = DartFlex.Classes( Styles.fullHeight.cName ),
                                        items = Vector( DartFlex.FlexItem(
                                            TextMui( Text.Props(
                                                element = <.em( "No Target" ),
                                            ) ),
                                        ) ),
                                    ) )
                                ),
                            ),
                            <.div(
                                ButtonMui( Button.Props(
                                    element = "Add Default Target",
                                    onClick = props.createDefaultTarget,
                                    classes = Button.Classes( addDefaultTargetButtonClass and Styles.bufferBottom ),
                                ) ),
                            ),
                            <.div(
                                Styles.bufferBottom.cName,
                                conceptSearch( props.conceptSearch.copy(
                                    classes = ConceptSearch.Classes(
                                        input = conceptSearchInputClass.cName,
                                        searchResults = conceptSearchResultsWrapperClass.cName,
                                        searchResult = ConceptSearch.ResultClasses(
                                            byIndex = Some( i => conceptSearchResultClass( i ) and conceptSearchResultsClass and conceptSearchResultTargetButtonClass ),
                                        ),
                                    ),
                                ).toDartPropsRC() ),
                            ),
                            <.div(
                                TextMui( Text.Props(
                                    element = <.b( "Similar Concepts" ),
                                    size = types.Medium,
                                ) ),
                            )
                        ),
                    ),
                    DartFlex.FlexItem(
                        flex = DartFlex.Grow( 1 ),
                        element = <.div(
                            Styles.fullHeight.cName and Styles.verticalOverflowAuto,
                            props.recommendedConcepts.sortBy( -_._2 ).map( tup => {
                                val (path, score, cb) = tup
                                <.div(
                                    Styles.recommendedConcept,
                                    <.span(
                                        ButtonMui( Button.Props(
                                            element = path.mkString( "/" ),
                                            onClick = cb,
                                            style = Button.Text,
                                            classes = Button.Classes( Some( recommendedConceptsClass and recommendedConceptTargetButton  ) )
                                        ) ),
                                    ),
                                    ^.key := path.mkString( "-" ),
                                )
                            } ).toVdomArray
                        )
                    )
                )
            ) )

            DartPanelMui( DartPanel.Props(
                classes = DartPanel.Classes( Styles.rootPanel.cName ),
                element = DartFlexBasic( DartFlex.Props(
                    direction = types.Row,
                    justify = types.JustifySpacedEvenly,
                    classes = DartFlex.Classes( Styles.root.cName ),
                    items = Vector(
                        DartFlex.FlexItem(
                            element = clusterSection,
                            classes = DartFlex.ItemClasses( Styles.fullHeight.cName ),
                            flex = DartFlex.Grow( 1 ),
                        ),
                        DartFlex.FlexItem(
                            element = conceptSection,
                            flex = DartFlex.NoFlex,
                            classes = DartFlex.ItemClasses(
                                Styles.conceptSection.cName,
                            )
                        ),
                    )
                ) )
            ) )
        }

        override val initialState : Option[ String ] = None
    }

}

object DartClusterCuratorClusterLayoutClasses {
    val clusterNameClass = s"cluster-name"
    val curateClusterButtonClass = "curate-cluster-button"
    val rejectClusterButtonClass = "reject-cluster-button"

    val selectAllCheckBoxClass = "select-all-checkbox"
    val selectAcceptedCheckBoxClass = "select-accepted-checkbox"
    val selectRejectedCheckBoxClass = "select-rejected-checkbox"
    val selectUncuratedCheckBoxClass = "select-uncurated-checkbox"
    val clearSelectionButtonClass = "clear-selection-button"

    val recommendedPhrasesClass = "recommended-phrase"

    val recommendedPhrasePhraseTextClass = "recommended-phrase-phrase-text"
    val recommendedPhraseAcceptButtonClass = "recommended-phrase-accept-button"
    val recommendedPhraseRejectButtonClass = "recommended-phrase-reject-button"
    val recommendedPhraseRestoreButtonClass = "recommended-phrase-restore-button"
    val recommendedPhraseUseNameButtonClass = "recommended-phrase-use-name-button"
    val recommendedPhraseUseForConceptSearchButtonClass = "recommended-phrase-use-search-button"

    val selectedPhrasesAcceptButtonClass = "selected-phrases-accept-button"
    val selectedPhrasesRejectButtonClass = "selected-phrases-reject-button"
    val selectedPhrasesRestoreButtonClass = "selected-phrases-restore-button"

    val acceptedPhrasesClass = "accepted-phrase"
    val rejectedPhrasesClass = "rejected-phrase"
    val selectedPhrasesClass = "selected-phrase"

    val acceptedPhraseTargetClass = "accepted-phrase-target"
    val acceptedPhraseTargetTargetButton = "accepted-phrase-target-target-button"
    val acceptedPhraseTargetViewButton = "accepted-phrase-target-view-button"

    val recommendedConceptsClass = "recommended-concept"
    val recommendedConceptTargetButton = "recommended-concept-target-button"
    val recommendedConceptViewButton = "recommended-concept-view-button"

    val conceptTargetClass = "concept-target"

    val addDefaultTargetButtonClass = "add-default-target-button"

    val conceptSearchInputClass = "concept-search-input"
    val conceptSearchResultsWrapperClass = "concept-search-results-wrapper"
    val conceptSearchResultsClass = "concept-search-results"

    def conceptSearchResultClass( i : Int ) = s"concept-search-result-$i"

    val conceptSearchResultViewButtonClass = "concept-search-result-view-button"
    val conceptSearchResultTargetButtonClass = "concept-search-result-target-button"

    def chosenConceptClass = "chosen-concept"
}
