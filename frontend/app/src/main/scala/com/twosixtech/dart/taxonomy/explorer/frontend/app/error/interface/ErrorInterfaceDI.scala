package com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface

import com.twosixtech.dart.scalajs.react.{AppProps, LayoutComponent}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.{ErrorHandlerDI, JsLogEntry, LogEntry}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.{Callback, CallbackTo}
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.VdomElement

import java.util.UUID
import scala.scalajs.js

trait ErrorInterfaceDI {
    this : ErrorHandlerDI
      with DartComponentDI
      with ErrorInterfaceLayoutDeps
      with DartStateDI
      with DartContextDeps
      with DartConfigDeps
      with DartCircuitDeps =>

    val errorInterface : ErrorInterface = new ErrorInterface

    class ErrorInterface extends ViewedDartComponent[ ErrorInterface.Props, ErrorInterfaceRenderContext, ErrorHandler.State ] {
        override def stateView( coreState : CoreState ) : ErrorHandler.State = coreState.errorState

        override protected def shouldComponentUpdate(
            props : ErrorInterface.Props,
            nextProps : ErrorInterface.Props,
            state : Unit,
            nextState : Unit,
            stateView : ErrorHandler.State,
            nextStateView : ErrorHandler.State,
            nextContext : DartContext )(
            implicit
            context : DartContext ) : CallbackTo[ Boolean ] = CallbackTo(
            stateView.unhandledReports != nextStateView.unhandledReports
        )

        override def render(
            props : ErrorInterface.Props,
            stateView : ErrorHandler.State,
        )(
            implicit renderContext : ErrorInterfaceRenderContext,
            stateContext : DartContext
        ) : VdomElement = {

            if ( dartConfig.enableLogging ) {
                js.Dynamic.global.getLogs = ( ( ) => {
                    stateView.logs.toJs
                } ) : js.Function0[ js.Array[ JsLogEntry ] ]
            }

            errorInterfaceLayout( ErrorInterface.LayoutProps(
                stateView.unhandledReports,
                id => stateContext.dispatch( ErrorHandler.ReportHandled( id ) )
            ).toDartProps )
        }
    }

    object ErrorInterface {
        case class Props()

        case class LayoutProps(
            unhandledReports : Map[ UUID, ErrorHandler.PublicReport ],
            handleError : UUID => Callback,
        )
    }
}

trait ErrorInterfaceLayoutDeps {
    this : ErrorInterfaceDI with DartComponentDI =>

    type ErrorInterfaceRenderContext
    type ErrorInterfaceLayoutState

    val errorInterfaceLayout : ErrorInterfaceLayout

    trait ErrorInterfaceLayout
      extends DartLayoutComponent[
        ErrorInterface.LayoutProps,
        ErrorInterfaceRenderContext,
        ErrorInterfaceLayoutState,
      ]
}
