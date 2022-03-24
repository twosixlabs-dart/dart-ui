package com.twosixtech.dart.taxonomy.explorer.frontend.base.context

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ DartComponentDI, DartRootDeps, DartStateDI }
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^.VdomNode
import japgolly.scalajs.react.{ Callback, ScalaComponent }

import java.util.UUID

trait DartContextDeps {
    this : DartStateDI
      with DartCircuitDeps
      with DartRouterDeps
      with ErrorHandlerDI
      with DartLoadingDI
      with DartBackendDeps
      with DartComponentDI =>

    case class DartContext(
        dispatch : DartAction => Callback,
        route : DartRouter.DartRoute => Callback,
        coreState : CoreState,
        layoutState : LayoutState,
        report : ErrorHandler.PublicReporter,
        log : ErrorHandler.Logger,
        globalLoader : DartLoading.Loader,
        backendContext : DartBackend.Cx,
        tenants : Seq[ DartTenant ],
        refreshTenants : Callback,
    )

    trait DartContextBuilder {
        // Necessary to distinguish root loader from subordinate loaders
        lazy val globalLoaderId : UUID = UUID.randomUUID()

        def ContextBuilder( renderer : (DartRouter.DartRoute, DartContext) => VdomNode ) : Unmounted[ (DartRouter.DartRoute, DartContext) => VdomNode, _, _ ]

        lazy val AppComponent : ReactComponent[ DartProps[ DartRouter.DartRoute, Unit ] => VdomNode, Unit ] =
            ReactComponent.functional( rootComponentRenderer  => {
                ContextBuilder { ( page, dartContext ) =>
                    val dartProps = DartProps[ DartRouter.DartRoute, Unit ](
                        mainProps = page,
                        renderProps = Unit,
                        context = dartContext,
                    )

                    rootComponentRenderer( dartProps )
                }
            } )
    }

    val DartContextBuilder : DartContextBuilder
}
