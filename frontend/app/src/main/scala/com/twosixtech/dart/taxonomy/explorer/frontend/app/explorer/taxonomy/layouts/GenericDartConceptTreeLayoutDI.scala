package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.layouts

import com.twosixtech.dart.scalajs.layout.div.grid.DartGrid
import com.twosixtech.dart.scalajs.layout.div.grid.mui.DartGridMui
import com.twosixtech.dart.scalajs.layout.types.Row
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy.{DartConceptBranchDI, DartConceptTreeDI, DartConceptTreeLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, TaxonomyIdSerializationDeps, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js


trait GenericDartConceptTreeLayoutDI
  extends DartConceptTreeLayoutDeps {
    this : DartConceptTreeDI
      with DartComponentDI
      with DartContextDeps
      with DartConceptBranchDI
      with GenericDartConceptBranchLayoutDI
      with DartConceptDeps
      with TaxonomyIdSerializationDeps =>

    override type DartConceptTreeRenderContext = Unit
    override type DartConceptTreeLayoutState = Unit
    override lazy val dartConceptTreeLayout : DartConceptTreeLayout = new GenericDartConceptTreeLayout

    import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

    class GenericDartConceptTreeLayout extends DartConceptTreeLayout {

        import scalacss.DevDefaults._
        import scalacss.ScalaCssReact._
        object FullHeightStyles extends StyleSheet.Inline {
            import dsl._

            val fullHeight : StyleA = style( height( 100 %% ) )
        }
        FullHeightStyles.addToDocument()

        override def render( scope : Scope, state : Unit, props : DartConceptTree.LayoutProps )(
            implicit renderProps : Unit,
            context : DartContext ) : VdomElement = {

            import TaxonomyIdSerialization.SerializableTaxonomyId

            <.div(
                FullHeightStyles.fullHeight,
                DartGridMui( DartGrid.Props(
                    direction = Row,
                    items = props.branchProps.takeRight( 4 ).map( ( bp : DartConceptBranch.Props ) => {
                        DartGrid.GridItem(
                            breakPoints = DartGrid.BreakPoints( xs = Some( 3 ) ),
                            element = <.div(
                                ^.style := ( js.Dictionary( "height" -> "100%" ) ),
                                dartConceptBranch( bp.toDartPropsRC() ),
                            ),
                            key = Some( bp.parent.map( _.marshalJson ).getOrElse( "root-level" ) ),
                        )
                    } ).toVector,
                    classes = DartGrid.Classes(
                        FullHeightStyles.fullHeight.cName,
                        FullHeightStyles.fullHeight.cName,
                    ),
                ) ),
            )

        }

        override val initialState : Unit = ()
    }
}
