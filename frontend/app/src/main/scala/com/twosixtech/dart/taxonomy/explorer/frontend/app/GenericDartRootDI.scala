package com.twosixtech.dart.taxonomy.explorer.frontend.app

import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.DartFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartRootDeps}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait GenericDartRootDI
  extends DartRootDeps {
    this : DartRouterDI
      with DartComponentDI
      with DartFrameDI
      with DartContextDeps =>

    lazy val dartRoot : SimpleDartComponent[ DartRouter.DartRoute, Unit ] = new DartRoot

    class DartRoot extends SimpleDartComponent[ DartRouter.DartRoute, Unit ] {
        override def render( props : DartRouter.DartRoute )
          ( implicit renderProps : Unit, context : DartContext ) : VdomElement = {

            props match {
                case DartRouter.ConceptExplorerRoute =>
                    dartFrame( DartFrame.Props( DartFrame.ConceptExplorer ).toDartProps )
                case DartRouter.CorpexRoute =>
                    dartFrame( DartFrame.Props( DartFrame.Corpex ).toDartProps )
                case DartRouter.DocumentRoute( id ) =>
                    dartFrame( DartFrame.Props( DartFrame.CorpexDocument( id ) ).toDartProps )
                case DartRouter.ForkliftRoute =>
                    dartFrame( DartFrame.Props( DartFrame.Forklift ).toDartProps )
                case DartRouter.TenantsRoute =>
                    dartFrame( DartFrame.Props( DartFrame.Tenants ).toDartProps )
                case DartRouter.TestRoute =>
                    dartFrame( DartFrame.Props( DartFrame.Test ).toDartProps )
                case DartRouter.NotFoundRoute =>
                    <.div( "NOT FOUND" )
            }
        }
    }
}
