package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy

import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement

trait DartConceptBreadCrumbsDI { this : DartComponentDI with DartConceptBreadCrumbsLayoutDeps with DartContextDeps =>

    val dartConceptBreadCrumbs : DartConceptBreadCrumbs = new DartConceptBreadCrumbs

    class DartConceptBreadCrumbs
      extends SimpleDartComponent[ DartConceptBreadCrumbs.Props, DartConceptBreadCrumbsRenderContext ] {
        override def render( props : DartConceptBreadCrumbs.Props )(
            implicit renderProps : DartConceptBreadCrumbsRenderContext,
            context : DartContext ) : VdomElement = {

            import DartConceptBreadCrumbs._

            dartConceptBreadCrumbsLayout( LayoutProps().toDartProps )
        }
    }

    object DartConceptBreadCrumbs {
        case class Props()
        case class LayoutProps()
    }
}

trait DartConceptBreadCrumbsLayoutDeps { this : DartConceptBreadCrumbsDI with DartComponentDI =>
    type DartConceptBreadCrumbsRenderContext
    type DartConceptBreadCrumbsLayoutState
    val dartConceptBreadCrumbsLayout : DartConceptBreadCrumbsLayout

    trait DartConceptBreadCrumbsLayout
      extends DartLayoutComponent[
        DartConceptBreadCrumbs.LayoutProps,
        DartConceptBreadCrumbsRenderContext,
        DartConceptBreadCrumbsLayoutState
      ]
}