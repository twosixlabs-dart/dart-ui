package com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading

import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import diode.ModelRW
import japgolly.scalajs.react.{Callback, CallbackTo}

import java.util.UUID
import scala.collection.immutable.{Queue, SortedSet}

trait DartLoadingDI {
    this : DartCircuitDeps
      with DartStateDI =>

    object DartLoading {

        case class State(
            loadings : SortedSet[ UUID ] = SortedSet.empty,
            loadingProgress : Map[ UUID, Float ] = Map.empty,
        )

        trait LoadingAction extends CoreAction
        case class AddLoading( componentId : UUID, id : UUID, progress : Option[ Float ] = None ) extends LoadingAction
        case class RemoveLoading( componentId : UUID, id : UUID ) extends LoadingAction
        case class UpdateLoadingProgress( componentId : UUID, id : UUID, progress : Option[ Float ] = None ) extends LoadingAction

        class Loader( componentId : UUID, dispatch : DartAction => Callback ) {
            def start : CallbackTo[ UUID ] = {
                val newId = UUID.randomUUID()
                dispatch( DartLoading.AddLoading( componentId, newId ) )
                  .map( _ => newId )
            }
            def startAs( id : UUID ) : Callback = dispatch( DartLoading.AddLoading( componentId, id ) )
            def start( startingProgress : Float ) : CallbackTo[ UUID ] = {
                val newId = UUID.randomUUID()
                dispatch( DartLoading.AddLoading( componentId, newId, Some( startingProgress ) ) )
                  .map( _ => newId )
            }
            def startAs( id : UUID, startingProgress : Float ) : Callback =
                dispatch( DartLoading.AddLoading( componentId, id, Some( startingProgress ) ) )
            def updateProgress( id : UUID, newProgress : Float ) : Callback =
                dispatch( DartLoading.UpdateLoadingProgress( componentId, id, Some( newProgress ) ) )

            def removeProgress( id : UUID ) : Callback =
                dispatch( DartLoading.UpdateLoadingProgress( componentId, id, None ) )

            def complete( id : UUID ) : Callback =
                dispatch( DartLoading.RemoveLoading( componentId, id ) )
        }

        /**
         * Defines a loading handler for a loading component somewhere in the core state
         * @param componentId an identifier to distinguish an instance of DartLoading within the
         *                    core state. LoadingActions that do not reference this id will be ignored.
         * @param loadingStateIdentifier a zoom function to zoom into the core state case class
         * @return
         */
        def loadingHandler(
            componentId : UUID,
            loadingStateIdentifier : ModelRW[ DartState, CoreState ] => ModelRW[ DartState, State ],
        ) : CoreHandler[ State ] =
            DartCircuitContext.coreHandler[ State ]( loadingStateIdentifier ) {
                prevState =>
                    {
                        case AddLoading( `componentId`, id, progressOpt ) =>
                            val newInstances = prevState.loadings + id
                            val newProgress = progressOpt match {
                                case None => prevState.loadingProgress
                                case Some( progress ) => prevState.loadingProgress + (id -> progress)
                            }
                            prevState.copy(
                                loadings = newInstances,
                                loadingProgress = newProgress,
                            )

                        case RemoveLoading( `componentId`, id ) =>
                            prevState.copy(
                                loadings = prevState.loadings - id,
                                loadingProgress = prevState.loadingProgress - id,
                            )

                        case UpdateLoadingProgress( `componentId`, id, progressOpt ) =>
                            val newProgress = progressOpt match {
                                case None => prevState.loadingProgress - id
                                case Some( progress ) => prevState.loadingProgress + (id -> progress)
                            }
                            prevState.copy(
                                loadingProgress = newProgress,
                            )
                    }
            }

    }

}
