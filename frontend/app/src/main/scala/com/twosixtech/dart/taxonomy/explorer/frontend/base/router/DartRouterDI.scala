package com.twosixtech.dart.taxonomy.explorer.frontend.base.router

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.router.{BaseUrl, ResolutionWithProps, RouterConfigDsl, RouterCtl, RouterWithProps, RouterWithPropsConfig, RouterWithPropsConfigDsl, SetRouteVia}
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^._

trait DartRouterDI extends DartRouterDeps {

    class DartAppRouter extends DartRouter {
        case object ConceptExplorerRoute extends DartRoute

        case object CorpexRoute extends DartRoute

        case object ForkliftRoute extends DartRoute

        case class DocumentRoute( id : String ) extends DartRoute

        case object TestRoute extends DartRoute

        case object NotFoundRoute extends DartRoute

        lazy val routeConfig : RouterWithPropsConfig[ DartRoute, Context => VdomNode ] = {
            RouterWithPropsConfigDsl[ DartRoute, Context => VdomNode ].buildConfig { ( dsl : RouterConfigDsl[ DartRoute, Context => VdomNode ] ) =>
                import dsl._

                val conceptRoute = staticRoute( root / "concepts", ConceptExplorerRoute ) ~> {
                    renderR( _ => <.div() )
                }

                val corpexRoute = staticRoute( root / "corpex", CorpexRoute ) ~> renderR( _ => <.div() )

                val documentRoute = dynamicRoute(
                    ( root / "corpex" / "document" / string( "[a-f0-9]{32}" ) ).caseClass[ DocumentRoute ],
                ) {
                    case p@DocumentRoute( _ ) => p
                } ~> dynRender( _ => <.div() )

                val forkliftRoute = staticRoute( root / "forklift", ForkliftRoute ) ~> renderR( _ => <.div() )

                val testRoute = staticRoute( root / "test", TestRoute ) ~> {
                    renderR( _ => <.div() )
                }

                ( conceptRoute | documentRoute | corpexRoute | forkliftRoute | testRoute )
                  .notFound( redirectToPage( ConceptExplorerRoute )( SetRouteVia.HistoryPush ) )
                  .renderWithP( ( rCtl : RouterCtl[ DartRoute ], rWp : ResolutionWithProps[ DartRoute, Context => VdomNode ] ) => {
                      ( renderer : Context => VdomNode ) => {
                          renderer( Context( rWp.page, rCtl.set ) ).asInstanceOf[ VdomElement ]
                      }
                  } )
            }
        }

        lazy val ContextBuilderComponent = {
            RouterWithProps.componentUnbuilt[ DartRoute, Context => VdomNode ]( BaseUrl.fromWindowOrigin, routeConfig ).build
        }


        def ContextBuilder( renderer : Context => VdomNode ) : Unmounted[ Context => VdomNode, _, _ ] = ContextBuilderComponent( renderer )
    }

    override lazy val DartRouter : DartAppRouter = new DartAppRouter

}
