package com.twosixtech.dart.scalajs.layout.icon

import com.twosixtech.dart.scalajs.layout.facade.mui.MuiIcon
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport( "@material-ui/icons/Remove", JSImport.Default )
@js.native
object RemoveIconMuiRaw extends js.Object

@JSImport( "@material-ui/icons/Add", JSImport.Default )
@js.native
object AddIconMuiRaw extends js.Object

@JSImport( "@material-ui/icons/ChevronRight", JSImport.Default )
@js.native
object RightIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/DoubleArrow", JSImport.Default )
@js.native
object DoubleRightIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/ChevronLeft", JSImport.Default )
@js.native
object LeftIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/FirstPage", JSImport.Default )
@js.native
object FarthestLeftIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/LastPage", JSImport.Default )
@js.native
object FarthestRightIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/SkipNext", JSImport.Default )
@js.native
object SkpNextIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Search", JSImport.Default )
@js.native
object SearchIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/CheckCircleOutline", JSImport.Default )
@js.native
object CheckCircleOutlineIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/HighlightOff", JSImport.Default )
@js.native
object CloseCircleOutlineIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Close", JSImport.Default )
@js.native
object CloseIconMuiRaw extends js.Object

@JSImport( "@material-ui/icons/SettingsBackupRestore", JSImport.Default )
@js.native
object RestoreIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Label", JSImport.Default )
@js.native
object LabelIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Edit", JSImport.Default )
@js.native
object EditIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Save", JSImport.Default )
@js.native
object SaveIconMuiRaw extends js.Object {}

@JSImport( "@material-ui/icons/Sync", JSImport.Default )
@js.native
object SyncIconMuiRaw extends js.Object {}


object Icons {

    val RemoveIconMui = new IconMui( RemoveIconMuiRaw )
    val AddIconMui = new IconMui( AddIconMuiRaw )
    val RightIconMui = new IconMui( RightIconMuiRaw )
    val DoubleRightIconMui = new IconMui( DoubleRightIconMuiRaw )
    val FarthestRightIconMui = new IconMui( FarthestRightIconMuiRaw )
    val LeftIconMui = new IconMui( LeftIconMuiRaw )
    val FarthestLeftIconMui = new IconMui( FarthestLeftIconMuiRaw )
    val SkipNextIconMui = new IconMui( SkpNextIconMuiRaw )
    val SearchIconMui = new IconMui( SearchIconMuiRaw )
    val CheckCircleOutlineIconMui = new IconMui( CheckCircleOutlineIconMuiRaw )
    val CloseCircleOutlineIconMui = new IconMui( CloseCircleOutlineIconMuiRaw )
    val CloseIconMui = new IconMui( CloseIconMuiRaw )
    val RestoreIconMui = new IconMui( RestoreIconMuiRaw )
    val LabelIconMui = new IconMui( LabelIconMuiRaw )
    val EditIconMui = new IconMui( EditIconMuiRaw )
    val SaveIconMui = new IconMui( SaveIconMuiRaw )
    val SyncIconMui = new IconMui( SyncIconMuiRaw )

}

class IconMui( rawIcon : js.Object )
  extends AbstractReactFacade(
      rawIcon,
      IconMui.Translation,
  ) {
    def apply(
        color : types.Color = types.Plain,
        size : Option[ types.BasicSize ] = None,
        classes : IconMui.Classes = IconMui.Classes(),
    ) : Unmounted[ IconMui.Props, Unit, Unit ] = apply( IconMui.Props( color, size, classes ) )
}

object IconMui {

    case class Classes(
        root : Option[ String ] = None,
    )

    case class Props(
        color : types.Color = types.Plain,
        size : Option[ types.BasicSize ] = None,
        classes : Classes = Classes(),
    )

    object Translation extends StoJ[ Props, MuiIcon.JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : MuiIcon.JsProps ) : (MuiIcon.JsProps, VdomNode) = {
            jsProps.color = props.color match {
                case types.Plain => "inherit"
                case types.Primary => "primary"
                case types.Secondary => "secondary"
            }
            jsProps.fontSize = "inherit"
            props.size.foreach( s => jsProps.fontSize = s match {
                case types.Small => "small"
                case types.Large => "large"
                case types.Medium => "default"
            } )
            jsProps.classes = {
                val jc = ( new js.Object ).asInstanceOf[ MuiIcon.JsClasses ]
                props.classes.root.foreach( jc.root = _ )
                jc
            }
            (jsProps, EmptyVdom)
        }
    }

}
