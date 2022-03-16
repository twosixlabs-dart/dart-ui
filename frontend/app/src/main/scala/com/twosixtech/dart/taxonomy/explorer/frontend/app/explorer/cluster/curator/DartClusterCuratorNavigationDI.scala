package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.CuratedClusterDI
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartClusterCuratorNavigationDI {
    this : DartClusterCuratorNavigationLayoutDeps
      with DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with DartConceptExplorerDI
      with CuratedClusterDI =>

    lazy val dartClusterCuratorNavigation : DartClusterCuratorNavigation = new DartClusterCuratorNavigation

    class DartClusterCuratorNavigation
      extends ViewedDartComponent[ DartClusterCuratorNavigation.Props, DartClusterCuratorNavigationRenderContext, DartClusterCurator.State ] {
        override def stateView( coreState : CoreState ) : DartClusterCurator.State = coreState.conceptState.cluster

        override def render(
            props : DartClusterCuratorNavigation.Props,
            stateView : DartClusterCurator.State )(
            implicit renderContext : DartClusterCuratorNavigationRenderContext,
            stateContext : DartContext,
        ) : VdomElement = {

            import DartClusterCurator._

            def setActiveCluster( index : Int ) : Callback = {
                val validIndex = {
                    if ( index < 0 ) 0
                    else if ( index >= props.clusters.size ) props.clusters.size - 1
                    else index
                }
                stateContext.dispatch( SetActiveCluster( validIndex ) )
            }

            def searchClusters( term : String ) : Seq[ (String, Int) ] = {
                stateView.clusterState match {
                    case cs : ClusterStateWithResults =>
                        cs.clusters.zipWithIndex
                          .filter( _._1.cluster.rankedWords.exists( _.contains( term ) ) )
                          .map( c => (c._1.cluster.recommendedName, c._2) )
                    case _ => Nil
                }
            }

            val nextUncuratedClusterValue : Option[ Int ] = {
                stateView.clusterState match {
                    case cs : ClusterStateWithResults =>
                        cs.clusters.zipWithIndex.find( tup => {
                            val (cl, i) = tup
                            i > cs.activeCluster && cl.acceptedPhrases.isEmpty && cl.rejectedPhrases.isEmpty
                        } ).map( _._2 )
                    case _ => None
                }
            }

            val nextUncuratedCluster : Callback =
                nextUncuratedClusterValue match {
                    case Some( i ) => setActiveCluster( i )
                    case None => stateContext.report.message( "No remaining uncurated clusters" )
                }

            val nextCluster : Callback =
                setActiveCluster( props.activeCluster + 1 )

            val prevCluster : Callback =
                setActiveCluster( props.activeCluster - 1 )

            val lastCluster : Callback =
                setActiveCluster( props.clusters.size - 1 )

            val firstCluster : Callback = setActiveCluster( 0 )

            val rejectClusterAndMoveOn : Callback =
                stateContext.dispatch( RejectUncurated( props.activeCluster ) ) >>
                nextCluster

            dartClusterCuratorNavigationLayout(
                DartClusterCuratorNavigation.LayoutProps(
                    activeCluster = props.activeCluster,
                    totalClusters = props.clusters.size,
                    clusterSearch = searchClusters,
                    setActiveCluster = setActiveCluster,
                    nextUncuratedCluster = nextUncuratedCluster,
                    nextCluster = nextCluster,
                    prevCluster = prevCluster,
                    lastCluster = lastCluster,
                    firstCluster = firstCluster,
                    cantGoLeft = props.activeCluster == 0,
                    cantGoRight = props.activeCluster == props.clusters.size - 1,
                    rejectClusterAndMoveOn = rejectClusterAndMoveOn,
                ).toDartProps
            )
        }
    }

    object DartClusterCuratorNavigation {

        case class Props(
            clusters : Vector[ CuratedCluster ],
            activeCluster : Int,
        )

        case class LayoutProps(
            activeCluster : Int,
            totalClusters : Int,
            clusterSearch : String => Seq[ (String, Int) ],
            setActiveCluster : Int => Callback,
            nextUncuratedCluster : Callback,
            nextCluster : Callback,
            prevCluster : Callback,
            lastCluster : Callback,
            firstCluster : Callback,
            cantGoLeft : Boolean,
            cantGoRight : Boolean,
            rejectClusterAndMoveOn : Callback,
        )

    }

}

trait DartClusterCuratorNavigationLayoutDeps
  extends DartClusterCuratorNavigationDI {
    this : DartClusterCuratorNavigationDI
      with DartConceptExplorerDI
      with DartComponentDI
      with DartClusterCuratorDI
      with DartStateDI
      with DartContextDeps
      with DartCircuitDeps
      with CuratedClusterDI =>

    type DartClusterCuratorNavigationRenderContext
    type DartClusterCuratorNavigationLayoutState

    val dartClusterCuratorNavigationLayout : DartClusterCuratorNavigationLayout

    trait DartClusterCuratorNavigationLayout
      extends DartLayoutComponent[
        DartClusterCuratorNavigation.LayoutProps,
        DartClusterCuratorNavigationRenderContext,
        DartClusterCuratorNavigationLayoutState,
      ]

}

