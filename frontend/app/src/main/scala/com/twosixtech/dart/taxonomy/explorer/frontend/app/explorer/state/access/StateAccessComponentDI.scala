package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access

import com.twosixtech.dart.scalajs.backend.HttpBody.{BinaryBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{HttpRequest, HttpResponse}
import com.twosixtech.dart.taxonomy.explorer.api.StateAccessApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.{DartCircuitDeps, GenericDartCircuitDI}
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.util.{Failure, Success, Try}
import upickle.default._

trait StateAccessComponentDI {
    this : DartComponentDI
      with DartBackendDeps
      with StateAccessComponentLayoutDeps
      with StateAccessApiDI
      with DartContextDeps
      with DartStateDI
      with DartConceptExplorerDI
      with DartClusterCuratorDI
      with DartCircuitDeps
      with DartConceptExplorerDI =>

    val stateAccessComponent : StateAccessComponent = new StateAccessComponent

    class StateAccessComponent extends ViewedDartComponent[
        StateAccessComponent.Props,
        StateAccessComponentRenderContext,
        StateAccessComponent.State,
    ] {
        override def stateView( coreState : CoreState ) : StateAccessComponent.State =
            coreState.conceptState.stateAccessState

        override protected def componentDidMount(
            contextView : StateAccessComponent.State,
            props : StateAccessComponent.Props,
            modState : ( SnapshotType => SnapshotType ) => react.Callback )
          ( implicit context : DartContext ) : Callback = {
            Callback()
        }

        override def render(
            props : StateAccessComponent.Props,
            stateView : StateAccessComponent.State,
        )(
            implicit renderContext : StateAccessComponentRenderContext,
            stateContext : StateAccessComponentDI.this.DartContext
        ) : VdomElement = {

            import StateAccessComponent.StateId
            import scalajs.concurrent.JSExecutionContext.Implicits.queue

            val refresh : Callback = Callback {
                stateContext
                  .backendContext
                  .authClient
                  .get( HttpRequest( StateAccessApi.PATH ) )
                  .onComplete {
                      case Success( HttpResponse( _, 200, TextBody( text ) ) ) =>
                          Try( read[ Map[ String, Int ] ]( text ) ) match {
                              case Success( km ) =>
                                  stateContext
                                    .dispatch( StateAccessComponent.RefreshKeyMap( km ) )
                                    .runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse saved data", e ).runNow()
                          }
                      case Success( HttpResponse( _, 200, BinaryBody( bytes ) ) ) =>
                          Try( readBinary[ Map[ String, Int ] ]( bytes ) ) match {
                              case Success( km ) =>
                                  stateContext
                                    .dispatch( StateAccessComponent.RefreshKeyMap( km ) )
                                    .runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse saved data", e ).runNow()
                          }
                      case Success( HttpResponse( _, status, body ) ) =>
                          stateContext.report.logMessage(
                              "Unable to retrieve saved data",
                              s"Response from GET ${StateAccessApi.PATH}:\nStatus: ${status}\nBody: ${body}"
                          ).runNow()
                      case Failure( e ) =>
                          stateContext.report.logMessage(
                              "Unable to retrieve saved data",
                              e,
                              s"Exception thrown during GET ${StateAccessApi.PATH}"
                          ).runNow()
                  }
            }

            def getStateVersion( stateId : StateId ) : Callback = Callback {
                import StateAccessApi.DeserializableConceptsStateJson
                import StateAccessApi.DeserializableConceptsStateBinary
                stateContext
                  .backendContext
                  .authClient
                  .get( HttpRequest( s"${StateAccessApi.PATH}/${stateId.key}/${stateId.version}" ) )
                  .onComplete {
                      case Success( HttpResponse( _, 200, TextBody( text ) ) ) =>
                          Try( text.unmarshalConceptsState ) match {
                              case Success( cs ) => ( for {
                                  _ <- stateContext.dispatch( DartConceptExplorer.LoadTaxonomy( cs.taxonomy ) )
                                  _ <- cs.clusterState match {
                                      case None =>
                                          stateContext.dispatch( DartClusterCurator.ClearClusterState )
                                      case Some( StateAccessApi.ClusterState( clusters, jobId, activeCluster ) ) =>
                                          stateContext.dispatch( DartClusterCurator.LoadClusterState( clusters, jobId ) )
                                  }
                                  _ <- stateContext.dispatch( StateAccessComponent.SwitchToKey( stateId.key, stateId.version ) )
                              } yield () ).runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse saved data", e ).runNow()
                          }
                      case Success( HttpResponse( _, 200, BinaryBody( bytes ) ) ) =>
                          Try( bytes.unmarshalConceptsState ) match {
                              case Success( cs ) => ( for {
                                  _ <- stateContext.dispatch( DartConceptExplorer.LoadTaxonomy( cs.taxonomy ) )
                                  _ <- cs.clusterState match {
                                      case None =>
                                          stateContext.dispatch( DartClusterCurator.ClearClusterState )
                                      case Some( StateAccessApi.ClusterState( clusters, jobId, activeCluster ) ) =>
                                          stateContext.dispatch( DartClusterCurator.LoadClusterState( clusters, jobId, Some( activeCluster ) ) )
                                  }
                                  _ <- stateContext.dispatch( StateAccessComponent.SwitchToKey( stateId.key, stateId.version ) )
                              } yield () ).runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse saved data", e ).runNow()
                          }
                      case Success( HttpResponse( _, status, body ) ) =>
                          stateContext.report.logMessage(
                              "Unable to retrieve saved data",
                              s"Response from GET ${StateAccessApi.PATH}:\nStatus: ${status}\nBody: ${body}"
                          ).runNow()
                      case Failure( e ) =>
                          stateContext.report.logMessage(
                              "Unable to retrieve saved data",
                              e,
                              s"Exception thrown during GET ${StateAccessApi.PATH}"
                          ).runNow()
                  }
            }

            def buildState : StateAccessApi.ConceptsState = StateAccessApi.ConceptsState(
                stateContext.coreState.conceptState.taxonomy,
                stateContext.coreState.conceptState.cluster.clusterState match {
                    case DartClusterCurator.ClusterComplete( clusters, jobId, _, _ ) =>
                        Some( StateAccessApi.ClusterState( clusters, jobId ) )
                    case _ => None
                }
            )

            val saveCurrentState : Callback = stateView.currentState match {
                case None => Callback()
                case Some( StateId( key, _ ) ) => Callback {
                    val textBody = buildState.marshalJson
                    stateContext
                      .backendContext
                      .authClient
                      .post( HttpRequest(
                          url = s"${StateAccessApi.PATH}/${key}",
                          body = TextBody( textBody )
                      ) )
                      .onComplete {
                          case Success( HttpResponse( _, 200, TextBody( text ) ) ) =>
                              Try( text.toInt ) match {
                                  case Success( i )  =>
                                      stateContext.dispatch( StateAccessComponent.UpdateAndSwitchToStateId( key, i, i ) ).runNow()
                                  case Failure( e ) =>
                                      stateContext.report.logMessage( "Unable to parse save response", e ).runNow()
                              }

                          case Success( HttpResponse( _, 200, BinaryBody( bytes ) ) ) =>
                              Try( readBinary[ Int ]( bytes ) ) match {
                                  case Success( i )  =>
                                      stateContext.dispatch( StateAccessComponent.UpdateAndSwitchToStateId( key, i, i ) ).runNow()
                                  case Failure( e ) =>
                                      stateContext.report.logMessage( "Unable to parse save response", e ).runNow()
                              }

                          case Success( HttpResponse( _, status, body ) ) =>
                              stateContext.report.logMessage(
                                  "Unable to save data",
                                  s"POST ${StateAccessApi.PATH}/${key} returned status ${status}\nBody: ${body}",
                              )

                          case Failure( e ) =>
                              stateContext.report.logMessage(
                                  "Unable to save data",
                                  e,
                                  s"Exception thrown during POST/${key}\nBody: ${textBody}"
                              ).runNow()

                      }
                }
            }

            def saveCurrentStateAs( key : String ) : Callback = Callback {
                val textBody = buildState.marshalJson
                stateContext
                  .backendContext
                  .authClient
                  .post( HttpRequest(
                      url = s"${StateAccessApi.PATH}/${key}",
                      body = TextBody( textBody )
                  ) )
                  .onComplete {
                      case Success( HttpResponse( _, 200, TextBody( text ) ) ) =>
                          Try( text.toInt ) match {
                              case Success( i ) =>
                                  stateContext.dispatch( StateAccessComponent.UpdateAndSwitchToStateId( key, i, i ) ).runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse save response", e ).runNow()
                          }

                      case Success( HttpResponse( _, 200, BinaryBody( bytes ) ) ) =>
                          Try( readBinary[ Int ]( bytes ) ) match {
                              case Success( i ) =>
                                  stateContext.dispatch( StateAccessComponent.UpdateAndSwitchToStateId( key, i, i ) ).runNow()
                              case Failure( e ) =>
                                  stateContext.report.logMessage( "Unable to parse save response", e ).runNow()
                          }

                      case Success( HttpResponse( _, status, body ) ) =>
                          stateContext.report.logMessage(
                              "Unable to save data",
                              s"POST ${StateAccessApi.PATH}/${key} returned status ${status}\nBody: ${body}",
                          )

                      case Failure( e ) =>
                          stateContext.report.logMessage(
                              "Unable to save data",
                              e,
                              s"Exception thrown during POST/${key}\nBody: ${textBody}"
                          ).runNow()
                  }
            }

            def copyState( state : StateId, newKey : String ) : Callback = Callback {}

            stateAccessComponentLayout( StateAccessComponent.LayoutProps(
                savedStateKeys = stateView.savedStateKeys,
                refresh = refresh,
                getStateVersion = getStateVersion,
                saveCurrentState = saveCurrentState,
                saveCurrentStateAs = saveCurrentStateAs,
                copyState = copyState,
                currentState = stateView.currentState,
            ) .toDartProps )
        }
    }

    object StateAccessComponent {

        case class StateId( key : String, version : Int ) {
            require( version != 0 )
        }

        case class State(
            savedStateKeys : Map[ String, Int ] = Map.empty,
            currentState : Option[ StateId ] = None,
        )

        case class Props()

        case class LayoutProps(
            savedStateKeys : Map[ String, Int ],
            refresh : Callback,
            getStateVersion :  StateId => Callback,
            saveCurrentState : Callback,
            saveCurrentStateAs : String => Callback, // String is key
            copyState : (StateId, String) => Callback, // String is new key
            currentState : Option[ StateId ],
        )

        sealed trait StateAccessAction extends CoreAction

        case class RefreshKeyMap( newKeys : Map[ String, Int ] ) extends StateAccessAction
        case class UpdateKey( key : String, numVersions : Int ) extends StateAccessAction
        case class UpdateAndSwitchToStateId( key : String, numVersions : Int, version : Int ) extends StateAccessAction
        case class SwitchToKey( key : String, version : Int ) extends StateAccessAction
        case object ClearStateId extends StateAccessAction

        val stateAccessHandler : CoreHandler[ State ] = DartCircuitContext.coreHandler[ State ]( _.zoomTo( _.conceptState.stateAccessState ) ) {
            prevState : State =>

                {
                    case RefreshKeyMap( newKeys ) =>
                        prevState.copy( savedStateKeys = newKeys )

                    case UpdateKey( key : String, count : Int ) =>
                        val oldKeys : Map[ String, Int ] = prevState.savedStateKeys
                        prevState.copy(
                            savedStateKeys = oldKeys + (key -> count)
                        )

                    case UpdateAndSwitchToStateId( key : String, numVersions : Int, version : Int ) =>
                        val oldKeys : Map[ String, Int ] = prevState.savedStateKeys
                        val oldCurrState : Option[ StateId ] = prevState.currentState
                        val newKeys = oldKeys + (key -> numVersions)
                        prevState.copy(
                            savedStateKeys = newKeys,
                            currentState = Some( StateId( key, version ) )
                        )

                    case SwitchToKey( key, version ) =>
                        prevState.copy(
                            currentState = Some( StateId( key, version ) )
                        )

                    case ClearStateId =>
                        prevState.copy( currentState = None )
                }
        }

    }

}

trait StateAccessComponentLayoutDeps {
    this : StateAccessComponentDI
      with DartComponentDI =>

    type StateAccessComponentRenderContext
    type StateAccessComponentLayoutState

    val stateAccessComponentLayout : StateAccessComponentLayout

    trait StateAccessComponentLayout
      extends DartLayoutComponent[
        StateAccessComponent.LayoutProps,
        StateAccessComponentRenderContext,
        StateAccessComponentLayoutState,
      ]
}
