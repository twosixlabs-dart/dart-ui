package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context

import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.DartTenantsContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ DartComponentDI, DartRootDeps, DartStateDI }
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait TestDartContextDI
  extends DartContextDeps {
    this : DartStateDI
      with DartCircuitDeps
      with DartRouterDeps
      with ErrorHandlerDI
      with DartTenantsContextDeps
      with DartLoadingDI
      with DartBackendDeps
      with DartComponentDI
      with DartRootDeps =>

    trait TestDartContextBuilder extends DartContextBuilder {

        lazy val TestContextBuilderComponent = ReactComponent.functional[ (DartRouter.DartRoute, DartContext) => VdomNode ] {
            renderer =>
                DartCircuitContext.ContextBuilder { circuitContext =>
                    val dispatcher = circuitContext.dispatcher
                    val reporter = new ErrorHandler.PublicReporter( dispatcher )
                    val logger = new ErrorHandler.Logger( dispatcher )

                    DartBackend.ContextBuilder { backendContext =>
                      DartTenantsContext.ContextBuilder( backendContext, reporter ) { ( tenants, refreshTenants ) =>
                          DartRouter.ContextBuilder { routeContext =>
                              val context = DartContext(
                                  dispatch = dispatcher,
                                  route = routeContext.router,
                                  coreState = circuitContext.state.coreState,
                                  layoutState = circuitContext.state.layoutState,
                                  report = reporter,
                                  log = logger,
                                  globalLoader = new DartLoading.Loader( globalLoaderId, dispatcher ),
                                  backendContext = backendContext,
                                  tenants = tenants,
                                  refreshTenants = refreshTenants,
                              )

                              renderer( routeContext.page, context )
                          }
                      }
                    }
                }
        }

        override def ContextBuilder(
            renderer : (DartRouter.DartRoute, DartContext) => VdomNode
        ) : Unmounted[ (DartRouter.DartRoute, DartContext) => VdomNode, _, _ ] = TestContextBuilderComponent( renderer )

        override lazy val AppComponent : ReactComponent[ DartProps[ DartRouter.DartRoute, Unit ] => VdomNode, Unit ] =
            ReactComponent.functional( _ => <.div() )
    }

    override val DartContextBuilder : TestDartContextBuilder = new TestDartContextBuilder {}

}
