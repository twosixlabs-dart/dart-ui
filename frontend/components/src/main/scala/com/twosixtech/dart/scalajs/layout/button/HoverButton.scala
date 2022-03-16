package com.twosixtech.dart.scalajs.layout.button

import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.icon.IconMui
import com.twosixtech.dart.scalajs.layout.icon.Icons.{AddIconMui, CloseCircleOutlineIconMui, CloseIconMui, RemoveIconMui}
import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.SimpleReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._

object HoverButton {

    case class Classes(
        root : Option[ String ] = None,
        elementWrapper : Option[ String ] = None,
        buttonWrapper : Option[ String ] = None,
        button : Option[ String ] = None,
        icon : Option[ String ] = None,
        firstItem : Option[ String ] = None,
    )

    case class Props(
        element : VdomNode,
        button : Option[ VdomNode ] = None,
        onClick : Option[ Callback ] = None,
        remove : Boolean = false,
        left : Boolean = false,
        space : Boolean = true,
        persist : Boolean = false,
        buttonFlex : DartFlex.FlexSetting = DartFlex.NoFlex,
        elementFlex : DartFlex.FlexSetting = DartFlex.NoFlex,
        classes : Classes = Classes(),
    )

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    private object Styles extends StyleSheet.Inline {
        import dsl._

        val firstItem = style( marginRight( 10 px ) )

        val button = style(
            fontSize.inherit,
        )

        val noHoverCondition = style()

        val buttonHoverCondition = style(
            unsafeChild( "." + this.button.htmlClass )(
                visibility.hidden,
            ),
            &.hover(
                unsafeChild( "." + this.button.htmlClass )(
                    visibility.visible,
                ),
            ),
            &.focus(
                unsafeChild( "." + this.button.htmlClass )(
                    visibility.visible,
                ),
            )
        )

        val buttonHoverConditionNoSpace = style(
            unsafeChild( "." + this.button.htmlClass )(
                display.none,
            ),
            &.hover(
                unsafeChild( "." + this.button.htmlClass )(
                    display.inherit,
                ),
            ),
            &.focus(
                unsafeChild( "." + this.button.htmlClass )(
                    display.inherit,
                ),
            )
        )

    }
    Styles.addToDocument()


    val component = ScalaComponent.builder[ Props ]
      .noBackend
      .render_P( props => {

          val buttonEle : VdomNode = props.button getOrElse {
              if ( props.remove ) IconButtonMui( IconButton.Props(
                  CloseIconMui( classes = IconMui.Classes( props.classes.icon ) ),
                  onClick = props.onClick.getOrElse( Callback() ),
                  classes = IconButton.Classes( props.classes.button ),
                  size = types.Small,
              ) )
              else IconButtonMui( IconButton.Props(
                  AddIconMui( classes = IconMui.Classes( props.classes.icon ) ),
                  onClick = props.onClick.getOrElse( Callback() ),
                  classes = IconButton.Classes( props.classes.button ),
                  size = types.Small,
              ) )
          }

          val item1 : VdomNode = if ( props.left ) buttonEle else props.element
          val item2 : VdomNode = if ( props.left ) props.element else buttonEle

          val outerClassName =
              if ( props.persist ) Styles.noHoverCondition
              else {
                  if ( props.space ) Styles.buttonHoverCondition.htmlClass
                  else Styles.buttonHoverConditionNoSpace.htmlClass
              }

          DartFlexBasic( DartFlex.Props(
              direction = types.Row,
              align = types.AlignCenter,
              classes = DartFlex.Classes(
                  container = Some {
                      outerClassName + props.classes.root.map( " " + _ ).getOrElse( "" )
                  }
              ),
              items = Vector(
                  DartFlex.FlexItem(
                      item1,
                      flex = if ( props.left ) props.buttonFlex else props.elementFlex,
                      classes = DartFlex.ItemClasses(
                          root = Styles.firstItem overriddenBy props.classes.firstItem and {
                            if ( props.left ) Styles.button and props.classes.buttonWrapper
                            else props.classes.elementWrapper
                          },
                      )
                  ),
                  DartFlex.FlexItem(
                      item2,
                      flex = if ( props.left ) props.elementFlex else props.buttonFlex,
                      classes = DartFlex.ItemClasses(
                          Some {
                              if ( props.left ) props.classes.elementWrapper.cName
                              else Styles.button and props.classes.buttonWrapper
                          }
                      )
                  )
              )
          ) )
      } )
      .build

    def apply( props : Props ) : Unmounted[ Props, Unit, Unit ] = component( props )

    def apply(
        element : VdomNode,
        button : Option[ VdomNode ] = None,
        onClick : Option[ Callback ] = None,
        remove : Boolean = false,
        left : Boolean = false,
        space : Boolean = true,
        persist : Boolean = false,
        buttonFlex : DartFlex.FlexSetting = DartFlex.NoFlex,
        elementFlex : DartFlex.FlexSetting = DartFlex.NoFlex,
        classes : Classes = Classes(),
    ) : Unmounted[ Props, Unit, Unit ] = apply(
        Props( element, button, onClick, remove, left, space, persist, buttonFlex, elementFlex, classes )
    )
}
