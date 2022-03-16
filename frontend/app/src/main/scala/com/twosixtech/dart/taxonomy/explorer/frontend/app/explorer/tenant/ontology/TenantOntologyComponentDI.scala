package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology

import com.twosixtech.dart.scalajs.backend.HttpBody.{NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{HttpMethod, HttpRequest, HttpResponse}
import com.twosixtech.dart.taxonomy.explorer.api.OntologyPublicationApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.util.{Failure, Success, Try}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

trait TenantOntologyComponentDI {
    this : TenantOntologyComponentLayoutDeps
      with OntologyPublicationApiDI
      with StateAccessComponentDI
      with DartComponentDI
      with DartContextDeps
      with DartCircuitDeps
      with DartStateDI
      with DartConceptExplorerDI
      with DartClusterCuratorDI =>

    val tenantOntologyComponent : TenantOntologyComponent = new TenantOntologyComponent

    class TenantOntologyComponent
      extends SimpleDartComponent[
        TenantOntologyComponent.Props,
        TenantOntologyComponentRenderContext,
      ] {

        def refreshState( implicit context : DartContext ) : Callback = Callback {
            val backendClient = context.backendContext.authClient

            backendClient.submit(
                HttpMethod.Get,
                HttpRequest(
                    retrieveTenantOntologiesEndpoint,
                    Map.empty,
                ),
            ) onComplete {
                case Success( HttpResponse( _, 200, TextBody( refreshRes ) ) ) =>
                    context.dispatch(
                        TenantOntologyComponent.RefreshTenantOntologiesMap( refreshRes.parseTenantOntologyResponse ),
                    ).runNow()
                case Success( HttpResponse( _, unexpectedStatus, TextBody( msg ) ) ) =>
                    context.report
                      .logMessage(
                          s"Unable to retrieve tenant ontology versions",
                          s"Failed to retrieve tenant ontology versions. Status $unexpectedStatus. Response: $msg",
                      ).runNow()
                case Failure( e ) => context.report
                  .logMessage( s"Unable to retrieve tenant ontology versions", e ).runNow()
            }
        }

        override protected def componentDidMount(
            modState : ( SnapshotType => SnapshotType ) => Callback,
            props : TenantOntologyComponent.Props,
        )(
            implicit
            context : DartContext,
        ) : Callback = refreshState

        override def render(
            props : TenantOntologyComponent.Props,
        )(
            implicit
            renderProps : TenantOntologyComponentRenderContext,
            context : DartContext,
        ) : VdomElement = {
            val backendClient = context.backendContext.authClient
            val currentTaxonomy = context.coreState.conceptState.taxonomy

            def stageTo( tenantId : String ) : Callback = Callback {
                backendClient.submit(
                    HttpMethod.Post,
                    HttpRequest(
                        submitStagedEndpoint( tenantId ),
                        Map( "Content-Type" -> "application/json" ),
                        TextBody( currentTaxonomy.stageSubmissionJson )
                    ),
                ) onComplete {
                    case Success( HttpResponse( _, 201, TextBody( res ) ) ) =>
                        context.dispatch(
                            TenantOntologyComponent.UpdateTenantOntologyStagedVersion( tenantId, res.parseStageResponse )
                        ).runNow()
                    case Success( HttpResponse( _, unexpectedStatus, TextBody( msg ) ) ) =>
                        context.report
                          .logMessage(
                              s"Unable to stage ontology in tenant $tenantId",
                              s"Failed to stage ontology in tenant $tenantId. Status $unexpectedStatus. Response: $msg",
                          ).runNow()
                    case Failure( e ) => context.report
                          .logMessage( s"Unable to stage ontology in tenant $tenantId", e ).runNow()
                }
            }

            def publishedStagedIn( tenantId : String ) : Callback = Callback {
                backendClient.submit(
                    HttpMethod.Post,
                    HttpRequest(
                        publishEndpoint( tenantId ),
                        Map.empty,
                        NoBody,
                    ),
                ) onComplete {
                    case Success( HttpResponse( _, 201, TextBody( res ) ) ) =>
                        context.dispatch(
                            TenantOntologyComponent.UpdateTenantOntologyPublishedVersion( tenantId, res.parsePublishResponse )
                        ).runNow()
                    case Success( HttpResponse( _, unexpectedStatus, TextBody( msg ) ) ) =>
                        context.report
                          .logMessage(
                              s"Unable to publish staged ontology in tenant $tenantId",
                              s"Failed to publish staged ontology in tenant $tenantId. Status $unexpectedStatus. Response: $msg",
                          ).runNow()
                    case Failure( e ) => context.report
                      .logMessage( s"Unable to publish staged ontology in tenant $tenantId", e ).runNow()
                }
            }

            def importStaged( clearClusterState : Boolean )( tenantId : String, version : Option[ Int ] ) : Callback = Callback {
                backendClient.submit(
                    HttpMethod.Get,
                    HttpRequest(
                        retrieveStagedEndpoint( tenantId, version ),
                    ),
                ) onComplete {
                    case Success( HttpResponse( _, 200, TextBody( retrievedStagedRes ) ) ) =>
                        Try( retrievedStagedRes.parseRetrieveStagedOntologyResponse ) match {
                            case Success( tax ) =>
                                context.dispatch(
                                    DartConceptExplorer.LoadTaxonomy( tax ),
                                ).runNow()
                                context.dispatch(
                                    StateAccessComponent.ClearStateId,
                                ).runNow()
                                if ( clearClusterState ) context.dispatch(
                                    DartClusterCurator.ClearClusterState,
                                ).runNow()
                            case Failure( e ) =>
                                context.report
                                  .logMessage(
                                      s"Unable to retrieve${ if ( version.isEmpty ) " latest" else "" } staged version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId",
                                      e,
                                      s"Failed to parse response when retrieving${ if ( version.isEmpty ) " latest" else "" } staged version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId.",
                                  ).runNow()
                        }

                    case Success( HttpResponse( _, unexpectedStatus, TextBody( msg ) ) ) =>
                        context.report
                          .logMessage(
                              s"Unable to retrieve${ if ( version.isEmpty ) " latest" else "" } staged version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId",
                              s"Failed to retrieve${ if ( version.isEmpty ) " latest" else "" } staged version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId. Status $unexpectedStatus. Response: $msg",
                          ).runNow()

                    case Failure( e ) => context.report
                      .logMessage( s"Failed to retrieve${ if ( version.isEmpty ) " latest" else "" } staged version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId.", e )
                      .runNow()
                }
            }

            def importPublished( clearClusterState : Boolean )( tenantId : String, version : Option[ Int ] ) : Callback = Callback {
                backendClient.submit(
                    HttpMethod.Get,
                    HttpRequest(
                        retrieveEndpoint( tenantId, version ),
                    ),
                ) onComplete {
                    case Success( HttpResponse( _, 200, TextBody( retrievePublishedRes ) ) ) =>
                        Try( retrievePublishedRes.parseRetrieveStagedOntologyResponse ) match {
                            case Success( tax ) =>
                                context.dispatch(
                                    DartConceptExplorer.LoadTaxonomy( tax ),
                                ).runNow()
                                context.dispatch(
                                    StateAccessComponent.ClearStateId,
                                ).runNow()
                                if ( clearClusterState ) context.dispatch(
                                    DartClusterCurator.ClearClusterState,
                                ).runNow()
                            case Failure( e ) =>
                                context.report
                                  .logMessage(
                                      s"Unable to retrieve${if ( version.isEmpty ) " latest" else ""} published version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId",
                                      e,
                                      s"Failed to parse response when retrieving${if ( version.isEmpty ) " latest" else ""} published version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId.",
                                  ).runNow()
                        }
                    case Success( HttpResponse( _, unexpectedStatus, TextBody( msg ) ) ) =>
                        context.report
                          .logMessage(
                              s"Unable to retrieve${ if ( version.isEmpty ) " latest" else "" } published version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId",
                              s"Failed to retrieve${ if ( version.isEmpty ) " latest" else "" } published version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId. Status $unexpectedStatus. Response: $msg",
                          ).runNow()
                    case Failure( e ) => context.report
                      .logMessage( s"Failed to retrieve${ if ( version.isEmpty ) " latest" else "" } published version ${version.map( _.toString + " " ).getOrElse( "" )}from $tenantId.", e )
                      .runNow()
                }
            }

            tenantOntologyComponentLayout( TenantOntologyComponent.LayoutProps(
                tenantOntologies = context.coreState.conceptState.tenantOntologyState.tenantOntologies,
                stageTo = stageTo,
                publishStagedIn = publishedStagedIn,
                importStagedAndClearClusterState = importStaged( true ),
                importStagedAndKeepClusterState = importStaged( false ),
                importPublishedAndClearClusterState = importPublished( true ),
                importPublishedAndKeepClusterState = importPublished( false ),
                refresh = refreshState,
            ).toDartProps )
        }
    }

    object TenantOntologyComponent {
        case class Props( )

        case class LayoutProps(
            tenantOntologies : Map[ String, TenantVersion ],
            stageTo : String => Callback,
            publishStagedIn : String => Callback,
            importStagedAndClearClusterState : (String, Option[ Int ]) => Callback,
            importStagedAndKeepClusterState : (String, Option[ Int ]) => Callback,
            importPublishedAndClearClusterState : (String, Option[ Int ]) => Callback,
            importPublishedAndKeepClusterState : (String, Option[ Int ]) => Callback,
            refresh : Callback,
        )


        case class State(
            tenantOntologies : Map[ String, TenantVersion ] = Map.empty[ String, TenantVersion ],
        )

        sealed trait TenantOntologyAction extends CoreAction

        case class RefreshTenantOntologiesMap( newTenantOntologies : Map[ String, TenantVersion ] )
          extends TenantOntologyAction

        case class UpdateTenantOntologyState( tenantId : String, newState : TenantVersion )
          extends TenantOntologyAction

        case class UpdateTenantOntologyPublishedVersion( tenantId : String, newVersion : Int )
          extends TenantOntologyAction

        case class UpdateTenantOntologyStagedVersion( tenantId : String, newVersion : Int )
          extends TenantOntologyAction

        val tenantOntologyHandler : CoreHandler[ State ] =
            DartCircuitContext.coreHandler[ State ](
                _.zoomTo( _.conceptState.tenantOntologyState ),
            ) {
                prevState : State =>

                {
                    case RefreshTenantOntologiesMap( newMap ) => State( newMap )
                    case UpdateTenantOntologyState( tenantId, newState ) =>
                        prevState.copy(
                            tenantOntologies = prevState.tenantOntologies + (tenantId -> newState)
                        )
                    // Note updating published version automatically clears staged version
                    case UpdateTenantOntologyPublishedVersion( tenantId, newVersion ) =>
                        prevState.copy(
                            tenantOntologies = prevState.tenantOntologies + {
                                tenantId -> {
                                    prevState.tenantOntologies( tenantId )
                                      .copy(
                                          publishedVersion = Some( newVersion ),
                                          stagedVersion = None,
                                      )
                                }
                            }
                        )
                    case UpdateTenantOntologyStagedVersion( tenantId, newVersion ) =>
                        prevState.copy(
                            tenantOntologies = prevState.tenantOntologies + {
                                tenantId -> prevState.tenantOntologies( tenantId ).copy( stagedVersion = Some( newVersion ) )
                            }
                        )
                }
            }

    }

}

trait TenantOntologyComponentLayoutDeps {
    this : TenantOntologyComponentDI with DartComponentDI =>

    type TenantOntologyComponentRenderContext
    type TenantOntologyComponentLayoutState

    val tenantOntologyComponentLayout : TenantOntologyComponentLayout

    trait TenantOntologyComponentLayout extends DartLayoutComponent[
      TenantOntologyComponent.LayoutProps,
      TenantOntologyComponentRenderContext,
      TenantOntologyComponentLayoutState,
    ]

}
