package com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.layouts

import com.twosixtech.dart.scalajs.layout.div.flex.{DartFlex, DartFlexBasic}
import com.twosixtech.dart.scalajs.layout.text.{Text, TextMui}
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.interface.{ErrorInterfaceDI, ErrorInterfaceLayoutDeps}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.{DartConfigDeps, GenericDartConfigDI}
import japgolly.scalajs.react.{Callback, CallbackTo}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import java.util.UUID
import scala.collection.immutable.Queue

trait ErrorInterfaceLayoutDI extends ErrorInterfaceLayoutDeps {
    this : ErrorInterfaceDI
      with DartComponentDI
      with DartContextDeps
      with ErrorHandlerDI
      with DartConfigDeps =>

    override type ErrorInterfaceRenderContext = Unit
    override type ErrorInterfaceLayoutState = GenericErrorInterfaceLayout.State
    override val errorInterfaceLayout : ErrorInterfaceLayout = new GenericErrorInterfaceLayout

    class GenericErrorInterfaceLayout extends ErrorInterfaceLayout {

        def createErrorAlert( report : ErrorHandler.PublicReport ) : Unit =
            report match {
                case ErrorHandler.ReportAlert( message, _ ) =>
                    window.alert( message )
                case _ =>
            }

        override def componentDidUpdate(
            modState : ( GenericErrorInterfaceLayout.State => GenericErrorInterfaceLayout.State ) => Callback,
            props : ErrorInterface.LayoutProps,
            prevProps : ErrorInterface.LayoutProps,
            state : GenericErrorInterfaceLayout.State,
            prevState : GenericErrorInterfaceLayout.State,
            prevContext : DartContext,
        )(
            implicit context : DartContext,
        ) : Callback = {

            if ( props.unhandledReports == prevProps.unhandledReports ) Callback()
            else {
                val newReports = props.unhandledReports -- prevProps.unhandledReports.keySet
                val removedReports = prevProps.unhandledReports -- props.unhandledReports.keySet

                val newAlertReports = ( newReports collect {
                    case (id, ErrorHandler.ReportAlert( _, _ )) => id
                } ).toSet
                val removedAlertReports = ( removedReports collect {
                    case (id, ErrorHandler.ReportAlert( _, _ )) => id
                } ).toSet
                val newMessageReports = ( newReports collect {
                    case (id, ErrorHandler.ReportMessage( _, _ )) => id
                } ).toSet
                val removedErrorReports = ( removedReports collect {
                    case (id, ErrorHandler.ReportMessage( _, _ )) => id
                } ).toSet

                val stateWithUpdatedReports = state.copy(
                    alertsQueue = state.alertsQueue
                      .filter( v => !removedAlertReports.contains( v ) )
                      .++( newAlertReports ),
                    exceptionQueue = state.exceptionQueue
                      .filter( v => !removedErrorReports.contains( v ) )
                      .++( newMessageReports ),
                )

                ( if ( stateWithUpdatedReports != state )
                    modState( _ => stateWithUpdatedReports )
                else Callback() )
                  .>>( Callback {
                      newReports.foreach( r => {
                          createErrorAlert( r._2 )
                      } )
                  } )
                  .>>( Callback {
                      newAlertReports.foreach( v => props.handleError( v ).runNow() )
                      newMessageReports.foreach( v => {
                          window.setTimeout(
                              () => props.handleError( v ).runNow(),
                              dartConfig.publicReportDuration,
                          )
                      } )
                  } )
            }

        }

        override def render(
            scope : Scope, state : ErrorInterfaceLayoutState,
            props : ErrorInterface.LayoutProps )(
            implicit renderProps : SnapshotType,
            context : DartContext ) : VdomElement = {
            <.div(
                ^.position := "absolute",
                ^.pointerEvents := "none",
                ^.bottom := "20px",
                ^.left := "20px",
                ^.maxWidth := "400px",
                ^.backgroundColor := "transparent",
                ^.zIndex := "9999",
                DartFlexBasic( DartFlex.Props(
                    direction = types.Column,
                    align = types.AlignCenter,
                    justify = types.JustifyEnd,
                    items = state.exceptionQueue
                      .map( reportId => DartFlex.FlexItem(
                          element = <.div(
                              ^.marginTop := "5px",
                              TextMui(
                                  color = Some( types.Secondary ),
                                  size = types.Large,
                                  element = <.b(
                                      props.unhandledReports.get( reportId ) match {
                                          case None => EmptyVdom
                                          case Some( ErrorHandler.ReportMessage( msg, _ ) ) =>
                                              msg
                                          case _ => EmptyVdom
                                      }
                                  ),
                              )
                          ),
                      ) )
                      .toVector,
                ) )
            )
        }

        override val initialState : ErrorInterfaceLayoutState =
            GenericErrorInterfaceLayout.State(
                Queue.empty,
                Queue.empty,
            )
    }

    object GenericErrorInterfaceLayout {
        case class State(
            alertsQueue : Queue[ UUID ],
            exceptionQueue : Queue[ UUID ],
        )
    }
}
