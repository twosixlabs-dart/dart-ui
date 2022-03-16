package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.scalajs.backend.HttpBody.{BinaryBody, NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{BackendClient, HttpMethod, HttpRequest, HttpResponse, XhrLocalErrorEvent, XhrNetworkErrorEvent}
import com.twosixtech.dart.scalajs.control.PollHandler
import com.twosixtech.dart.scalajs.control.PollHandler.PollContext
import com.twosixtech.dart.taxonomy.explorer.api.ClusteringApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.DartConceptFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.{Cluster, CuratedClusterDI, DartConceptDeps, DartTaxonomyDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, WmDartSerializationDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import org.scalajs.dom.window

import java.util.UUID
import scala.util.{Failure, Success, Try}

trait DartClusterCuratorFrameDI {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartClusterCuratorFrameLayoutDeps
      with DartStateDI
      with DartContextDeps
      with DartConceptDeps
      with DartTaxonomyDI
      with DartConceptFrameDI
      with DartConceptExplorerDI
      with DartCircuitDeps
      with DartLoadingDI
      with ClusteringApiDI
      with DartSerializationDeps
      with CuratedClusterDI =>

    lazy val dartClusterCuratorFrame : DartClusterCuratorFrame = new DartClusterCuratorFrame

    class DartClusterCuratorFrame
      extends StatefulViewedDartComponent[ DartClusterCuratorFrame.Props, DartClusterCuratorFrameRenderContext, DartClusterCurator.State, DartClusterCuratorFrame.State ] {
        override def stateView( coreState : CoreState ) : DartClusterCurator.State = coreState.conceptState.cluster

        override val initialState : DartClusterCuratorFrame.State  = DartClusterCuratorFrame.State()

        val loadId = UUID.randomUUID()

        import DartConceptExplorer.AddConcept
        import DartClusterCurator.ClusterResults

        def pollfn(
            client : BackendClient,
            jobId : Option[ String ],
            resHandler : Seq[ Cluster ] => Callback,
            failHandler : Throwable => Callback,
        ) : Callback = Callback {
            import com.twosixtech.dart.taxonomy.explorer.serialization.ClusterSerialization._
            import ClusteringApi.{DeserializableJson, DeserializableBinary}

            import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

            client.submit(
                HttpMethod.Get,
                HttpRequest(
                    ClusteringApi.reclusterResultsEndpoint + "/" + ( jobId match {
                        case None => ""
                        case Some( id ) => id.toString
                    } ),
                    Map.empty,
                    NoBody,
                ),
            ) map {
                case HttpResponse( _, 200, TextBody( json ) ) =>
                    json.unmarshalClusterResults.clusters.map( _.map( _.toCluster ) )
                case HttpResponse( _, 200, BinaryBody( data ) ) =>
                    data.unmarshalClusterResults.clusters.map( _.map( _.toCluster ) )
                case HttpResponse( _, 200, body ) =>
                    throw new Exception( s"Unexpected body type: ${body}" )
                case HttpResponse( _, _, _ ) =>
                    throw new Exception( s"Unable to reach clustering service" )
            } onComplete {
                case Success( Some( clusters ) ) =>
                    resHandler( clusters ).runNow()
                case Success( None ) =>
                case Failure( e ) =>
                    failHandler( e ).runNow()
            }
        }

        case class PollHandlerProps(
            dartContext : DartContext,
            contextView : DartClusterCurator.State,
            pollContext : PollContext,
            element : VdomNode,
        )

        override def render(
            scope : Scope, state : DartClusterCuratorFrame.State, props : DartClusterCuratorFrame.Props,
            stateView : DartClusterCurator.State )(
            implicit renderContext : DartClusterCuratorFrameRenderContext,
            stateContext : DartContext ) : VdomElement = {

            val loader = DartClusterCurator.buildLoader( stateContext.dispatch )

            val taxonomy = stateContext.coreState.conceptState.taxonomy

            def resHandler( res : Seq[ Cluster ] )( implicit pollContext : PollContext ) : Callback =
                stateContext.dispatch( ClusterResults( res ) ) >>
                pollContext.removePoll >>
                loader.complete( loadId )


            def failHandler( err : Throwable ) : Callback = {
                err match {
                    case e : XhrNetworkErrorEvent =>
                        stateContext.report.logMessage( "Clustering service unreachable", e )
                    case e : XhrLocalErrorEvent =>
                        stateContext.report.logMessage( "Failed execute clustering request", e )
                    case e =>
                        stateContext.report.logMessage( "Failed to reach clustering service (unknown exception)", e )
                }
            }

            PollHandler(
                defaultTimeout = Some( 3500 ),
                initialPoll = stateView.clusterState match {
                      case _ : DartClusterCurator.ClusterComplete =>
                          None
                      case _ =>
                          Some( implicit pollContext => {
                              val clusterState = stateView.clusterState
                              val jobId = clusterState match {
                                  case DartClusterCurator.ReclusterPending( _, id, _ ) => Some( id.toString )
                                  case _ => None
                              }

                              loader.startAs( loadId ) >>
                              pollfn( stateContext.backendContext.authClient, jobId, resHandler, failHandler )
                          } )
                },
                element = { implicit pollContext =>

                    import DartClusterCurator._

                    val jobId = stateView.clusterState match {
                        case NoClusterState => None
                        case InitialClusterPending => None
                        case ClusterComplete( _, jobId, _, _ ) => jobId.map( _.toString )
                        case ReclusterPending( _, jobId, _ ) => Some( jobId.toString )
                    }

                    def getJobResults( id : String, setJobIdTo : Option[ UUID ] = None ) : Callback = {
                        Try( UUID.fromString( id ) ) toOption match {
                            case None =>
                                Callback( window.alert( "Job id must be UUID" ) )
                            case Some( uuid ) =>
                                def jobResultsResHandler( res : Seq[ Cluster ] ) : Callback =
                                    stateContext.dispatch( ClusterResults( res ) ) >>
                                    pollContext.removePoll >>
                                    loader.complete( loadId )

                                val pollCallback : Callback =
                                    pollfn( stateContext.backendContext.authClient, Some( uuid.toString ), jobResultsResHandler, failHandler )

                                loader.startAs( loadId ) >>
                                pollContext.setPoll( pollCallback ) >>
                                pollCallback >>
                                stateContext.dispatch( DartClusterCurator.ReclusterSubmitted( setJobIdTo.getOrElse( uuid ) ) )
                        }
                    }

                    def getRescoreResults( id : String, setJobIdTo : Option[ UUID ] = None ) : Callback = {
                        Try( UUID.fromString( id ) ) toOption match {
                            case None =>
                                Callback( window.alert( "Job id must be UUID" ) )
                            case Some( uuid ) =>
                                def jobResultsResHandler( res : Seq[ Cluster ] ) : Callback = {
                                    stateContext.dispatch( RescoreResults( res ) ) >>
                                      pollContext.removePoll >>
                                      loader.complete( loadId )
                                }

                                val pollCallback : Callback =
                                    pollfn( stateContext.backendContext.authClient, Some( uuid.toString ), jobResultsResHandler, failHandler )

                                loader.startAs( loadId ) >>
                                  pollContext.setPoll( pollCallback ) >>
                                  pollCallback >>
                                  stateContext.dispatch( DartClusterCurator.ReclusterSubmitted( setJobIdTo.getOrElse( uuid ) ) )
                        }
                    }

                    val clusters = stateView.clusterState match {
                        case NoClusterState => None
                        case InitialClusterPending => None
                        case cs : ClusterStateWithResults => Some( cs.clusters )
                    }

                    // Submit recluster job
                    val recluster = stateView.clusterState match {
                        case ClusterComplete( clusters, jobId, _, _ ) if clusters.exists {
                            cluster =>
                                ( cluster.rejectedPhrases ++ cluster.acceptedPhrases ).nonEmpty
                        } => () =>
                            import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

                            val endpoint =
                                ClusteringApi.reclusterSubmitEndpoint +
                                  jobId.map( v => s"/$v" ).getOrElse( "" )

                            loader.startAs( loadId ) >> Callback {
                                stateContext.backendContext.authClient.submit(
                                    HttpMethod.Post,
                                    HttpRequest(
                                        endpoint,
                                        Map.empty,
                                        TextBody(
                                            new ClusteringApi.DartReclusterRequest(
                                                clusters.reclusterablePhrases( taxonomy ).toSeq,
                                                DartSerialization.taxonomyToJson( taxonomy ),
                                            ).marshalJson
                                        )
                                    )
                                ) onComplete {
                                    case Success( HttpResponse( _, status, TextBody( id ) ) ) if status == 200 =>
                                        ( getJobResults( id ) ).runNow()
                                    case Success( other ) =>
                                        stateContext.report
                                          .logMessage( "Unable to submit reclustering job", s"Unrecognized response from reclustering submission: ${other}" )
                                          .>> ( loader.complete( loadId ) ).runNow()
                                    case Failure( e ) =>
                                        ( failHandler( e ) >>
                                          loader.complete( loadId ) ).runNow()
                                }
                            }
                        case _ => ( ) => Callback()
                    }

                    val rescore = jobId match {
                        case None => () => Callback()
                        case Some( id ) => () =>
                            import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

                            loader.startAs( loadId ) >> Callback {
                                stateContext.backendContext.authClient.submit(
                                    HttpMethod.Post,
                                    HttpRequest(
                                        ClusteringApi.rescoreSubmitEndpoint,
                                        Map.empty,
                                        TextBody(
                                            new ClusteringApi.RescoreRequest(
                                                DartSerialization.taxonomyToJson( taxonomy ),
                                                UUID.fromString( id ),
                                            ).marshalJson
                                        )
                                    )
                                ) onComplete {
                                    case Success( HttpResponse( _, status, TextBody( newId ) ) ) if status == 200 =>
                                        ( getRescoreResults( newId, Some( UUID.fromString( id ) ) ) ).runNow()
                                    case Success( other ) =>
                                        stateContext.report
                                          .logMessage( "Unable to submit rescoring job", s"Unrecognized response from reclustering submission: ${other}" )
                                          .>>( loader.complete( loadId ) ).runNow()
                                    case Failure( e ) =>
                                        ( failHandler( e ) >>
                                          loader.complete( loadId ) ).runNow()
                                }
                            }
                    }

                    val reclusterButtonDisabled = stateView.clusterState match {
                        case NoClusterState => true
                        case InitialClusterPending => true
                        case cs : ClusterStateWithResults =>
                            if ( cs.clusters.forall {
                                cluster =>
                                    ( cluster.rejectedPhrases ++ cluster.acceptedPhrases ).isEmpty
                            } ) true
                            else false
                    }

                    val rescoreButtonDisabled = jobId.isEmpty || ( stateView.clusterState match {
                        case _ : ClusterStateWithResults => false
                        case _ => true
                    } )

                    val clusterPending = stateView.clusterState match {
                        case ReclusterPending( _, _, _ ) => true
                        case _ => false
                    }

                    val activeClusterOpt = stateView.clusterState match {
                        case cs : ClusterStateWithResults =>
                            Some( cs.activeCluster )
                        case _ => None
                    }

                    val conceptEntry : Option[ DartTaxonomyEntry ] = for {
                        index <- activeClusterOpt
                        cluster <- stateView.clusterState match {
                            case cs : ClusterStateWithResults =>
                                cs.clusters.lift( index )
                            case _ => None
                        }
                        target <- cluster.currentTarget
                        entry <- stateContext.coreState.conceptState.taxonomy.idEntry( target )
                    } yield entry

                    val conceptProps = conceptEntry match {
                        case None => None
                        case Some( entry ) =>
                            activeClusterOpt match {
                                case None => None
                                case Some( activeCluster ) =>
                                    Some( DartConceptFrame.Props(
                                        entry,
                                        id => stateContext.dispatch( TargetConcept( activeCluster, id ) ),
                                    ) )
                            }
                    }

                    dartClusterCuratorFrameLayout(
                        DartClusterCuratorFrame.LayoutProps(
                            jobId = jobId,
                            getJobResults = jid => getJobResults( jid ),
                            clusters = clusters,
                            reclusterButtonDisabled = reclusterButtonDisabled,
                            recluster = recluster,
                            rescoreButtonDisabled = rescoreButtonDisabled,
                            rescore = rescore,
                            clusterPending = clusterPending,
                            reclusterablePhrases =
                            ( ) => clusters.map( _.reclusterablePhrases( taxonomy ) ).toSeq.flatten,
                            activeCluster = activeClusterOpt,
                            conceptProps = conceptProps,
                            loadingState = stateView.loadingState,
                            clear = () => stateContext.dispatch( ClearClusterState ),
                            clearDisabled = stateView.clusterState == NoClusterState,
                        ).toDartProps
                    )
                }
            )
        }
    }

    object DartClusterCuratorFrame {

        case class State( )

        case class Props( )

        case class LayoutProps(
            jobId : Option[ String ],
            getJobResults : String => Callback,
            clusters : Option[ Vector[ CuratedCluster ] ],
            reclusterButtonDisabled : Boolean,
            recluster : () => Callback,
            rescoreButtonDisabled : Boolean,
            rescore : () => Callback,
            clusterPending : Boolean,
            reclusterablePhrases : () => Seq[ String ],
            activeCluster : Option[ Int ],
            conceptProps : Option[ DartConceptFrame.Props ],
            loadingState : DartLoading.State,
            clear : () => Callback,
            clearDisabled : Boolean,
        )

    }

}

trait DartClusterCuratorFrameLayoutDeps
  extends DartClusterCuratorFrameDI {
    this : DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartCircuitDeps
      with DartTaxonomyDI
      with DartConceptFrameDI
      with DartContextDeps
      with DartConceptExplorerDI
      with CuratedClusterDI
      with DartLoadingDI
      with ClusteringApiDI
      with DartSerializationDeps
      with DartConceptDeps =>

    type DartClusterCuratorFrameRenderContext
    type DartClusterCuratorFrameLayoutState

    val dartClusterCuratorFrameLayout : DartClusterCuratorFrameLayout

    trait DartClusterCuratorFrameLayout
      extends DartLayoutComponent[
        DartClusterCuratorFrame.LayoutProps,
        DartClusterCuratorFrameRenderContext,
        DartClusterCuratorFrameLayoutState,
      ]

}

