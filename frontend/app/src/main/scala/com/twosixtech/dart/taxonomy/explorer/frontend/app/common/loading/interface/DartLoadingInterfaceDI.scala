package com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.interface

import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import japgolly.scalajs.react.vdom.VdomElement

import java.util.UUID

trait DartLoadingInterfaceDI {
    this : DartLoadingDI
      with DartComponentDI
      with DartLoadingInterfaceLayoutDeps
      with DartStateDI
      with DartContextDeps =>

    val dartLoadingInterface : DartLoadingInterface = new DartLoadingInterface

    class DartLoadingInterface
      extends SimpleDartComponent[
        DartLoadingInterface.Props,
        DartLoadingInterfaceRenderContext,
      ] {

        override def render(
            props : DartLoadingInterface.Props,
        )(
            implicit renderContext : DartLoadingInterfaceRenderContext,
            stateContext : DartContext,
        ) : VdomElement = {
            val isLoading : Boolean = props.loadingState.loadings.nonEmpty
            val primaryLoadingInstance : Option[UUID ] =
                props.loadingState.loadings.headOption

            val progress : Option[ Float ] = primaryLoadingInstance.flatMap( id => {
                props.loadingState.loadingProgress.get( id )
            } )

            dartLoadingInterfaceLayout( DartLoadingInterface.LayoutProps(
                isLoading,
                progress,
                props.display,
            ).toDartProps )
        }
    }

    object DartLoadingInterface {
        sealed trait LoadingDisplay
        case object DarkOverlay extends LoadingDisplay
        case object LightOverlay extends LoadingDisplay

        case class Props(
            loadingState : DartLoading.State,
            display : LoadingDisplay,
        )

        case class LayoutProps(
            isLoading : Boolean,
            progress : Option[ Float ],
            display : LoadingDisplay,
        )
    }
}

trait DartLoadingInterfaceLayoutDeps {
    this : DartLoadingInterfaceDI
      with DartComponentDI =>

    type DartLoadingInterfaceRenderContext
    type DartLoadingInterfaceLayoutState

    val dartLoadingInterfaceLayout : DartLoadingInterfaceLayout

    trait DartLoadingInterfaceLayout
      extends DartLayoutComponent[
        DartLoadingInterface.LayoutProps,
        DartLoadingInterfaceRenderContext,
        DartLoadingInterfaceLayoutState,
      ]
}
