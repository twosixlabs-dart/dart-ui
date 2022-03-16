package com.twosixtech.dart.taxonomy.explorer.frontend.app.error

import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.Callback

import java.util.UUID
import scala.collection.immutable.Queue

trait ErrorHandlerDI {
    this : DartCircuitDeps
      with DartStateDI
      with DartContextDeps
      with DartConfigDeps =>

    object ErrorHandler {

        sealed trait ErrorAction extends CoreAction

        case class Log( entry : LogEntry ) extends ErrorAction

        sealed trait PublicReport extends ErrorAction {
            val log : Option[ LogEntry ]
        }

        case class ReportMessage( message : String, log : Option[ LogEntry ] = None ) extends PublicReport
        case class ReportAlert( message : String, log : Option[ LogEntry ] = None ) extends PublicReport

        sealed trait ErrorManagement extends ErrorAction

        case class ReportHandled( reportId : UUID ) extends ErrorManagement

        class PublicReporter( dispatch : DartAction => Callback ) {
            def alert( message : String ) : Callback = dispatch( ErrorHandler.ReportAlert( message ) )
            def message( message : String ) : Callback = dispatch( ErrorHandler.ReportMessage( message ) )

            def logAlert( message : String ) : Callback =
                dispatch( ErrorHandler.ReportAlert( message, Some( MessageLog( message ) ) ) )
            def logAlert( message : String, logMessage : String ) : Callback =
                dispatch( ErrorHandler.ReportAlert( message, Some( MessageLog( logMessage ) ) ) )
            def logAlert( message : String, logException : Throwable ) : Callback =
                dispatch( ErrorHandler.ReportAlert( message, Some( ExceptionLog( logException ) ) ) )
            def logAlert( message : String, logException : Throwable, logMessage : String ) : Callback =
                dispatch( ErrorHandler.ReportAlert( message, Some( ExceptionLog( logException, Some( logMessage ) ) ) ) )

            def logMessage( message : String ) : Callback =
                dispatch( ErrorHandler.ReportMessage( message, Some( MessageLog( message ) ) ) )
            def logMessage( message : String, logMessage : String ) : Callback =
                dispatch( ErrorHandler.ReportMessage( message, Some( MessageLog( logMessage ) ) ) )
            def logMessage( message : String, logException : Throwable ) : Callback =
                dispatch( ErrorHandler.ReportMessage( message, Some( ExceptionLog( logException ) ) ) )
            def logMessage( message : String, logException : Throwable, logMessage : String ) : Callback =
                dispatch( ErrorHandler.ReportMessage( message, Some( ExceptionLog( logException, Some( logMessage ) ) ) ) )
        }

        class Logger( dispatch : DartAction => Callback ) {
            def apply( message : String ) : Callback =
                dispatch( ErrorHandler.Log( MessageLog( message ) ) )
            def apply( exception : Throwable ) : Callback =
                dispatch( ErrorHandler.Log( ExceptionLog( exception ) ) )
            def apply( exception : Throwable, message : String ) : Callback =
                dispatch( ErrorHandler.Log( ExceptionLog( exception, Some( message ) ) ) )
        }

        case class State(
            logs : LogQueue = LogQueue( dartConfig.logMaxLength ),
            unhandledReports : Map[ UUID, PublicReport ] = Map.empty,
        )

        val errorHandler : CoreHandler[ State ] =
            DartCircuitContext.coreHandler[ State ]( _.zoomTo( _.errorState ) ) {
                prevState : State =>
                    val unhandledErrors = prevState.unhandledReports

                    {
                        case Log( entry ) =>
                            prevState.copy( logs = prevState.logs.log( entry ) )

                        case report : PublicReport =>
                            ( report.log match {
                                case None => prevState
                                case Some( entry ) =>
                                    prevState.copy( logs = prevState.logs.log( entry ) )
                            } ).copy(
                                unhandledReports =
                                    prevState.unhandledReports + (UUID.randomUUID() -> report)
                            )

                        case ReportHandled( id ) =>
                            prevState.copy(
                                unhandledReports = unhandledErrors - id,
                            )
                    }
            }

    }
}
