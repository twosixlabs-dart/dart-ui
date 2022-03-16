package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm

import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.div.panel.{DartPanel, DartPanelMui}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.{DartConceptChildrenLayoutDI, DartConceptNameLayoutDI, DartConceptParentLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.metadata.wm.{WmDartConceptMetadataViewDI, WmDartConceptMetadataViewLayoutDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.{DartConceptChildrenDI, DartConceptFrameDI, DartConceptFrameLayoutDeps, DartConceptNameDI, DartConceptParentDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.postfixOps
import scala.scalajs.js

trait WmDartConceptFrameLayoutDI
  extends DartConceptFrameLayoutDeps {
    this : DartContextDeps
      with DartComponentDI
      with DartConceptFrameDI
      with DartConceptNameDI
      with DartConceptParentDI
      with DartConceptChildrenDI
      with DartConceptNameLayoutDI
      with DartConceptParentLayoutDI
      with DartConceptChildrenLayoutDI
      with WmDartConceptMetadataViewLayoutDI
      with WmDartConceptMetadataViewDI =>

    override type DartConceptFrameLayoutState = Unit
    override type DartConceptFrameRenderContext = Unit
    override lazy val dartConceptFrameLayout : DartConceptFrameLayout = new DartConceptFrameLayoutBasic

    class DartConceptFrameLayoutBasic extends DartConceptFrameLayout {

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._

        object Styles extends StyleSheet.Inline {

            import dsl._

            val fullHeight = style( height( 100 %% ), width( 100 %% ) )
            val fixedHeight = style( minHeight.auto, paddingBottom( 15 px ) )
            val scrollable = style( overflow.auto, width( 100 %% ) )
            val spacedField = style( marginBottom( 5 px ) )
            val panel = style( padding( 20 px ), height( 100 %% ), overflowY.auto, overflowX.hidden )
        }

        Styles.addToDocument()

        import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

        override def render(
            scope : Scope, state : Unit,
            props : DartConceptFrame.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import props._

            DartPanelMui( DartPanel.Props(
                classes = DartPanel.Classes( Styles.panel.cName ),
                element = DartFlexBasic( DartFlex.Props(
                    classes = DartFlex.Classes(
                        Styles.fullHeight.cName
                    ),
                    direction = types.Column,
                    align = types.AlignStretch,
                    items = Vector(
                        DartFlex.FlexItem(
                            TextMui( Text.Props(
                                nameProps.taxonomyEntry.concept.name.split( "_" ).map( _.capitalize ).mkString( " " ),
                                size = types.Large,
                                color = Some( types.Primary ),
                            ) ),
                            classes = DartFlex.ItemClasses(
                                Styles.fixedHeight.cName,
                            ),
                        ),
                        DartFlex.FlexItem(
                            flex = DartFlex.Grow( 1 ),
                            element = <.div(
                                <.div(
                                    Styles.spacedField,
                                    dartConceptName( nameProps.toDartPropsRC( () ) ),
                                ),
                                <.div(
                                    Styles.spacedField,
                                    dartConceptParent( parentProps.toDartPropsRC( () ) ),
                                ),
                                <.div(
                                    Styles.spacedField,
                                    dartConceptChildren( childrenProps.toDartPropsRC( () ) ),
                                ),
                                wmDartConceptMetadataView( metadataProps.toDartPropsRC( () ) ),
                            ),
                            classes = DartFlex.ItemClasses(
                                Styles.scrollable.cName,
                            )
                        )
                    )
                ) ),
            ) )

        }

        override val initialState : Unit = ()
    }

    object DartConceptFrameLayoutBasic

}
