package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame

import com.twosixtech.dart.scalajs.backend.HttpBody.{BinaryBody, NoBody, TextBody}
import com.twosixtech.dart.scalajs.backend.{HttpMethod, HttpRequest, HttpResponse, XhrBackendClient, XhrLocalErrorEvent, XhrNetworkErrorEvent}
import com.twosixtech.dart.scalajs.dom.DownloadUtils
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface.DartLoadingInterfaceDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.DartConceptFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.search.ConceptSearchDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


trait DartConceptExplorerFrameDI {
    this : DartComponentDI
      with DartCircuitDeps
      with DartConceptExplorerFrameLayoutDeps
      with DartContextDeps
      with DartConceptDeps
      with DartConceptExplorerDI
      with StateAccessComponentDI
      with DartConceptFrameDI
      with DartStateDI
      with DartSerializationDeps
      with DartTaxonomyDI
      with DartLoadingDI
      with ConceptSearchDeps =>

    lazy val dartConceptExplorerFrame : DartConceptExplorerFrame = new DartConceptExplorerFrame

    class DartConceptExplorerFrame
      extends ViewedDartComponent[ DartConceptExplorerFrame.Props, DartConceptExplorerFrameRenderContext, DartConceptExplorer.State ] {

        override protected def componentDidMount(
            contextView : DartConceptExplorer.State, props : DartConceptExplorerFrame.Props,
            modState : ( SnapshotType => SnapshotType ) => Callback )
          ( implicit context : DartContext ) : Callback = {
            val loader = DartConceptExplorer.loader( context.dispatch )

            import DartSerialization._

            // Uncomment this to seed taxonomy
            Callback {
//                if ( contextView.taxonomy.rootConcepts.isEmpty ) {
//                    val loadingId = loader.start.runNow()
//
//                    context
//                      .backendContext
//                      .authClient
//                      .submit( HttpMethod.Get, HttpRequest( "/taxonomies/wm" ) )
//                      .map( res => {
//                          val tax = res.body match {
//                              case NoBody => DartTaxonomy( Set() )
//                              case TextBody( text ) => text.unmarshalTaxonomy
//                              case BinaryBody( binary ) => binary.unmarshalTaxonomy
//                          }
//                          context.dispatch( DartConceptExplorer.LoadTaxonomy( tax ) ).runNow()
//                      } )
//                      .onComplete( _ => loader.complete( loadingId ).runNow() )
//                }
            }
        }

        override def stateView( coreState : CoreState ) : DartConceptExplorer.State = coreState.conceptState

        override def render( props : DartConceptExplorerFrame.Props, stateView : DartConceptExplorer.State )(
            implicit renderContext : DartConceptExplorerFrameRenderContext,
            stateContext : DartContext ) : VdomElement = {
            import DartConceptExplorerFrame._

            val loader = DartConceptExplorer.loader( stateContext.dispatch )

            val uploadTaxonomy : String => Callback = taxonomyYml => Callback {
                import DartSerialization._

                val loadingId = loader.start.runNow()

                stateContext
                  .backendContext
                  .authClient
                  .submit(
                      HttpMethod.Post,
                      HttpRequest(
                          "/ontology/parse",
                          Map( "ContentType" -> "application/json" ),
                          TextBody( taxonomyYml ),
                      )
                  ) map {
                    case HttpResponse( _, _, TextBody( ontology ) ) =>
                        ontology.unmarshalTaxonomy
                    case HttpResponse( _, _, BinaryBody( ontology ) ) =>
                        ontology.unmarshalTaxonomy
                    case other => throw new Exception( s"Unknown response: ${other}" )
                } onComplete {
                    case Success( taxonomy ) =>
                        stateContext.dispatch( DartConceptExplorer.LoadTaxonomy( taxonomy ) )
                          .>>( stateContext.dispatch( StateAccessComponent.ClearStateId ) )
                          .>>( loader.complete( loadingId ) ).runNow()
                    case Failure( e : XhrNetworkErrorEvent ) =>
                        stateContext.report
                          .logMessage( "Unable to load taxonomy (network error)", e )
                          .>>( loader.complete( loadingId ) ).runNow()
                    case Failure( e : XhrLocalErrorEvent ) =>
                        stateContext.report
                          .logMessage( "Unable to load taxonomy (local error)", e )
                          .>>( loader.complete( loadingId ) ).runNow()
                    case Failure( e ) =>
                        stateContext.report
                          .logMessage( "Unable to load taxonomy (unknown error)", e )
                          .>>( loader.complete( loadingId ) ).runNow()
                }
            }

            val downloadTaxonomy : () => Callback = () => Callback {
                import DartSerialization._

                val taxText = stateView.taxonomy.marshalJson

                XhrBackendClient.submit(
                    HttpMethod.Post,
                    HttpRequest(
                        "/ontology",
                        Map( "ContentType" -> "application/json" ),
                        TextBody( taxText ),
                    )
                ) onComplete {
                    case Success( HttpResponse( _, _, TextBody( yml ) ) )  =>
                        DownloadUtils.downloadObject( yml, "ontology.yml" )
                    case Success( other ) =>
                        stateContext.report.logMessage( "Unable to download ontology", s"Unknown response when trying to download taxonomy: ${other}" )
                    case Failure( e : XhrNetworkErrorEvent ) =>
                        stateContext.report
                          .logMessage( "Unable to download taxonomy (network error)", e ).runNow()
                    case Failure( e : XhrLocalErrorEvent ) =>
                        stateContext.report
                          .logMessage( "Unable to download taxonomy (local error)", e ).runNow()
                    case Failure( e ) =>
                        stateContext.report
                          .logMessage( "Unable to download taxonomy (unknown error)", e ).runNow()
                }
            }

            val searchProps = ConceptSearch.Props(
                taxonomy= stateView.taxonomy,
                onSelectConcept = id => stateContext.dispatch( DartConceptExplorer.ChooseConcept( id ) )
            )

            import DartConceptExplorer.ChooseConcept

            val conceptFrameProps : Option[ DartConceptFrame.Props ] = stateView.conceptChoice
              .flatMap( id => stateView.taxonomy.idEntry( id ).map( entry => {
                  DartConceptFrame.Props( entry, id => stateContext.dispatch( ChooseConcept( id ) ) )
              } ) )

            dartConceptExplorerFrameLayout( LayoutProps(
                conceptFrameProps,
                downloadTaxonomy,
                uploadTaxonomy,
                searchProps,
                stateView.loadingState,
            ).toDartProps )
        }

    }

    object DartConceptExplorerFrame {
        case class Props()
        case class LayoutProps(
            conceptFrameProps : Option[ DartConceptFrame.Props ],
            downloadTaxonomy : () => Callback,
            uploadTaxonomy : String => Callback,
            searchProps : ConceptSearch.Props,
            loadingProps : DartLoading.State,
        )
    }
}

trait DartConceptExplorerFrameLayoutDeps { this : DartConceptExplorerFrameDI with DartComponentDI =>
    type DartConceptExplorerFrameRenderContext
    type DartConceptExplorerFrameLayoutState
    val dartConceptExplorerFrameLayout : DartConceptExplorerFrameLayout

    trait DartConceptExplorerFrameLayout
      extends DartLayoutComponent[
        DartConceptExplorerFrame.LayoutProps,
        DartConceptExplorerFrameRenderContext,
        DartConceptExplorerFrameLayoutState
      ]
}