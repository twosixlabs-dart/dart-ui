package com.twosixtech.dart.scalajs.layout.list.virtual

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.raw.React.RefHandle
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom
import japgolly.scalajs.react.{Callback, Ref, raw}
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "SelfMeasuringWindowedList" )
object SelfMeasuringWindowedListRaw extends js.Object

object SelfMeasuringWindowedList
  extends AbstractReactFacade( SelfMeasuringWindowedListRaw, SelfMeasuringWindowedListTranslation.Translation )

object SelfMeasuringWindowedListTranslation {

    @js.native
    trait JsRenderParameters extends js.Object {
        var index : Int = js.native
        var updateRow : js.Function0[ Unit ] = js.native
        var parent : js.Object = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var rowForWindow : js.Function1[ JsRenderParameters, raw.React.Node ] = js.native
        var rowForMeasure : js.Function1[ JsRenderParameters, raw.React.Node ] = js.native
        var rowCount : Int = js.native
        var measureChunkSize : Int = js.native
        var windowWidth : Int = js.native
        var windowHeight : Int = js.native
        var overscanCount : Int = js.native
        var onScroll : js.Function1[ Int, Unit ] = js.native
        var scrollTop : Int = js.native
        var scrollToIndex : Int = js.native
        var scrollToIndexCallback : js.Function0[ Unit ] = js.native
        var scrollOffset : Int = js.native
        var scrollOffsetCallback : js.Function0[ Unit ] = js.native
        var key : String = js.native
    }

    case class RenderParameters(
        index : Int,
        updateRow : Callback,
        parent : Ref.Simple[ _ ],
    )

    case class Props(
        rowForWindow : RenderParameters => VdomNode,
        rowForMeasure : RenderParameters => VdomNode,
        rowCount : Int,
        windowWidth : Int,
        windowHeight : Int,
        overscanCount : Option[ Int ] = None,
        measureChunkSize : Option[ Int ] = None,
        onScroll : Option[ Int => Callback ] = None,
        scrollTop : Option[ Int ] = None,
        scrollToIndex : Option[ Int ] = None,
        scrollToIndexCallback : Option[ Callback ] = None,
        scrollOffset : Option[ Int ] = None,
        scrollOffsetCallback : Option[ Callback ] = None,
        key : Option[ String ] = None,
    ) extends {
        private  def rpToScala : JsRenderParameters => RenderParameters = (rp : JsRenderParameters) => {
            RenderParameters(
                rp.index,
                Callback( rp.updateRow() ),
                Ref.fromJs( rp.parent.asInstanceOf[ RefHandle[ _ <: HTMLElement ] ] )
            )
        }

        def toJs : JsProps = {
            val jp = ( new js.Object ).asInstanceOf[ JsProps ]
            jp.rowForWindow = rp => rowForWindow( rpToScala( rp ) ).rawNode
            jp.rowForMeasure = rp => rowForMeasure( rpToScala( rp ) ).rawNode
            jp.rowCount = rowCount
            jp.windowWidth = windowWidth
            jp.windowHeight = windowHeight
            overscanCount.foreach( oc => jp.overscanCount = oc )
            measureChunkSize.foreach( mcs => jp.measureChunkSize = mcs )
            onScroll.foreach( os => jp.onScroll = ( i : Int ) => os( i ).runNow() )
            scrollTop.foreach( st => jp.scrollTop = st )
            scrollToIndex.foreach( sti => jp.scrollToIndex = sti )
            scrollToIndexCallback.foreach( stic => jp.scrollOffsetCallback = () => stic.runNow() )
            scrollOffset.foreach( so => jp.scrollOffset = so )
            scrollOffsetCallback.foreach( soc => jp.scrollOffsetCallback = () => soc.runNow() )
            jp
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }

}
