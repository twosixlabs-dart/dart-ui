package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.form.textinput.{TextInput, TextInputMui}
import com.twosixtech.dart.scalajs.layout.icon.Icons.{FarthestLeftIconMui, FarthestRightIconMui, LeftIconMui, RightIconMui, SaveIconMui, SyncIconMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.{StateAccessComponentDI, StateAccessComponentLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.{Callback, CallbackTo}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import scala.language.postfixOps
import scala.scalajs.js

trait GenericStateAccessComponentLayoutDI
  extends StateAccessComponentLayoutDeps {
    this : StateAccessComponentDI
      with DartComponentDI
      with DartContextDeps =>

    override type StateAccessComponentRenderContext = Unit
    override type StateAccessComponentLayoutState = GenericStateAccessComponentLayout.State
    override val stateAccessComponentLayout : StateAccessComponentLayout = new GenericStateAccessComponentLayout

    import scalacss.DevDefaults._
    object Styles extends StyleSheet.Inline {
        import dsl._

        val keyDiv : StyleA = style( marginBottom( 10 px ) )
        val keyText : StyleA = style( textDecorationLine.underline )
        val navigationButton : StyleA = style( padding( 0 px ) )
    }
    window.setTimeout( () => Styles.addToDocument(), 500 )

    import GenericStateAccessComponentLayoutClasses._
    import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

    class GenericStateAccessComponentLayout
      extends StateAccessComponentLayout {

        override protected def componentDidMount(
            modState : ( GenericStateAccessComponentLayout.State => GenericStateAccessComponentLayout.State ) => Callback,
            props : StateAccessComponent.LayoutProps,
        )( implicit context : DartContext ) : Callback = props.refresh

        override def render(
            scope : Scope,
            state : GenericStateAccessComponentLayout.State,
            props : StateAccessComponent.LayoutProps,
        )(
            implicit
            renderProps : Unit,
            context : DartContext,
        ) : VdomElement = {
            <.div(
                DartFlexBasic(
                    direction = types.Row,
                    align = types.AlignCenter,
                    items = Vector(
                        DartFlex.FlexItem( TextMui(
                            element = "User Data",
                            size = types.Large,
                            color = Some( types.Primary ),
                        )  ),
                        DartFlex.FlexItem( IconButtonMui( IconButton.Props(
                            onClick = props.refresh,
                            icon = SyncIconMui(),
                            classes = IconButton.Classes( refreshButtonClass.cName ),
                        ) ) ),
                        DartFlex.FlexItem( ButtonMui(
                            element = TextMui(
                                element = <.span( "Save", SaveIconMui() ),
                            ),
                            onClick = props.saveCurrentState,
                            disabled = props.currentState.isEmpty,
                            classes = Button.Classes( saveButtonClass.cName ),
                        ) ),
                    ),
                ),
                <.div(
                    TextInputMui( TextInput.Props(
                        classes = TextInput.Classes( input = newKeyInputClass.cName ),
                        value = Some( state.newKeyInputValue ),
                        placeholder = Some( "Add new key" ),
                        onChange = Some( newValue => scope.modState( _.copy( newKeyInputValue = newValue ) ) ),
                        onEnter =
                            if ( state.newKeyInputValue.trim.isEmpty ) None
                            else Some( props.saveCurrentStateAs( state.newKeyInputValue.trim )
                                       >> scope.modState( _.copy( newKeyInputValue = "" ) ) )
                    ) ),
                    IconButtonMui( IconButton.Props(
                        icon = SaveIconMui(),
                        disabled = state.newKeyInputValue.trim.isEmpty,
                        onClick = props.saveCurrentStateAs( state.newKeyInputValue.trim ),
                        classes = IconButton.Classes( saveAsButtonClass.cName ),
                    ) )
                ),
                ( props.savedStateKeys.map {
                    case (key, numVersions) =>
                        val isSelectedKey = props.currentState.exists( _.key == key )
                        <.div(
                            ^.key := key,
                            keyComponentClass and keyIdentifierClass( key ) and Styles.keyDiv and
                            ( if ( isSelectedKey ) Some( selectedKeyComponentClass ) else None ),
                            if ( isSelectedKey ) {
                                IconButtonMui( IconButton.Props(
                                    classes = IconButton.Classes( oldestVersionButtonClass and Styles.navigationButton ),
                                    icon = FarthestLeftIconMui(),
                                    disabled = props.currentState.exists( _.version <= 1 ),
                                    onClick = props.getStateVersion( props.currentState.map( v => v.copy( version = 1 ) ).get ),
                                ) )
                            } else EmptyVdom,
                            if ( isSelectedKey ) {
                                IconButtonMui( IconButton.Props(
                                    classes = IconButton.Classes( prevVersionButtonClass and Styles.navigationButton ),
                                    icon = LeftIconMui(),
                                    disabled = props.currentState.exists( _.version <= 1 ),
                                    onClick = CallbackTo( props.currentState.get )
                                      .flatMap( sId => props.getStateVersion( sId.copy( version = sId.version - 1 ) ) ),
                                ) )
                            } else EmptyVdom,
                            TextMui(
                                element =
                                    if ( isSelectedKey ) <.b( key )
                                    else <.span( ^.cursor := "pointer", key ),
                                onClick =
                                    if ( isSelectedKey ) None
                                    else Some( props.getStateVersion( StateAccessComponent.StateId( key, numVersions ) ) ),
                                classes = Text.Classes( keyTextClass and Styles.keyText )
                            ),
                            if ( isSelectedKey ) {
                                IconButtonMui( IconButton.Props(
                                    classes = IconButton.Classes( nextVersionButtonClass and Styles.navigationButton ),
                                    icon = RightIconMui(),
                                    disabled = props.currentState.exists( _.version >= numVersions ),
                                    onClick = props.getStateVersion( props.currentState.map( v => v.copy( version = v.version + 1 ) ).get ),
                                ) )
                            } else EmptyVdom,
                            if ( isSelectedKey ) {
                                IconButtonMui( IconButton.Props(
                                    classes = IconButton.Classes( latestVersionButtonClass and Styles.navigationButton ),
                                    icon = FarthestRightIconMui(),
                                    disabled = props.currentState.exists( _.version >= numVersions ),
                                    onClick = props.getStateVersion( StateAccessComponent.StateId( key, numVersions ) ),
                                ) )
                            } else EmptyVdom,
                            ( numVersions to 1 by -1 ).map( version => {
                                val isSelectedVersion = isSelectedKey && props.currentState.exists( _.version == version )
                                <.div(
                                    if ( isSelectedVersion ) EmptyVdom else ^.cursor := "pointer",
                                    ^.key := s"$key-$version",
                                    versionComponentClass and versionIdentifierClass( version ) and
                                    ( if ( isSelectedVersion ) Some( selectedVersionClass ) else None ),
                                    TextMui(
                                        element = if ( isSelectedVersion ) <.b( s"version ${version}" ) else s"version ${version}",
                                        onClick =
                                            if ( isSelectedVersion ) None
                                            else Some( props.getStateVersion( StateAccessComponent.StateId( key, version ) ) ),
                                        classes = Text.Classes( versionTextClass.cName ),
                                    )
                                )
                            } ).toVdomArray
                        )
                } ).toVdomArray
            )
        }

        override val initialState : GenericStateAccessComponentLayout.State =
            GenericStateAccessComponentLayout.State()
    }

    object GenericStateAccessComponentLayout {
        case class State(
            newKeyInputValue : String = ""
        )
    }

}

object GenericStateAccessComponentLayoutClasses {
    val keyComponentClass : String = "key-component"
    val keyTextClass : String = "key-text"
    val selectedKeyComponentClass : String = "selected-key-component"
    val expandedKeyComponentClass : String = "expanded-key-component"
    val collapsedKeyComponentClass : String = "collapsed-key-component"
    def keyIdentifierClass( key : String ) : String = s"key-component-${key.hashCode}"

    val selectedVersionClass : String = "selected-key-version-component"
    val versionTextClass : String = "version-text"
    def versionComponentClass : String = "key-version-component"
    def versionIdentifierClass( version : Int ) : String = s"key-version-component-$version"

    val newKeyInputClass : String = "new-key-input"

    val refreshButtonClass : String = "refresh-button"
    val prevVersionButtonClass : String = "previous-version-button"
    val nextVersionButtonClass : String = "next-version-button"
    val latestVersionButtonClass : String = "latest-version-button"
    val oldestVersionButtonClass : String = "oldest-version-button"
    val saveButtonClass : String = "save-button"
    val saveAsButtonClass : String = "save-as-button"
}

