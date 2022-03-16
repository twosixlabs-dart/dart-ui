package com.twosixtech.dart.taxonomy.explorer.frontend.base.context

import com.twosixtech.dart.scalajs.layout.loading.mui.LoadingMui
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.{ DartBackendDeps, KeycloakXhrDartBackendDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.js.JsDartContextProviderDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ DartComponentDI, DartRootDeps, DartStateDI }
import japgolly.scalajs.react.component.Scala.{ Component, Unmounted }
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{ Callback, CtorType, ScalaComponent }

import java.util.UUID

trait GenericDartContextDI extends DartContextDeps {
    this : DartStateDI
      with DartCircuitDeps
      with DartRouterDeps
      with ErrorHandlerDI
      with DartLoadingDI
      with KeycloakXhrDartBackendDI
      with DartComponentDI
      with JsDartContextProviderDI
      with DartRootDeps =>

    trait GenericDartContextBuilder extends DartContextBuilder {

        lazy val ContextBuilderComponent = ScalaComponent.builder[ (DartRouter.DartRoute, DartContext) => VdomElement ]
          .noBackend
          .render_P( renderer => {
              DartBackend.ContextBuilder { backendContext =>
                  DartCircuitContext.ContextBuilder { circuitContext =>
                      DartRouter.ContextBuilder { routeContext =>
                          val dispatcher = circuitContext.dispatcher
                          val reporter = new ErrorHandler.PublicReporter( dispatcher )
                          val logger = new ErrorHandler.Logger( dispatcher )

                          val context = DartContext(
                              dispatch = dispatcher,
                              route = routeContext.router,
                              coreState = circuitContext.state.coreState,
                              layoutState = circuitContext.state.layoutState,
                              report = reporter,
                              log = logger,
                              globalLoader = new DartLoading.Loader( globalLoaderId, dispatcher ),
                              backendContext = backendContext,
                          )

                          val rootElement = renderer( routeContext.page, context )

                          val loader = LoadingMui.LoadingCircularMui(
                            color = types.Primary,
                            size = types.Large,
                          )

                          JsDartContextProvider.ContextBuilder( JsDartContextProvider.Props(
                              backendContext,
                              logger,
                              reporter,
                              routeContext.router,
                              loader,
                              child = rootElement,
                          ) )
                      }
                  }
              }
          } )
          .build

        override def ContextBuilder(
            renderer : (DartRouter.DartRoute, DartContext) => VdomElement,
        ) : Unmounted[ (DartRouter.DartRoute, DartContext) => VdomElement, _, _ ] = ContextBuilderComponent( renderer )

    }

    override val DartContextBuilder : GenericDartContextBuilder = new GenericDartContextBuilder {}
}
