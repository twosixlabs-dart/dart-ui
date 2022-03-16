package com.twosixtech.dart.scalajs.layout.div.withdimensions

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, StoJ}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.raw.React.Node
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport( "dart-ui-components", "WithDimensions" )
object WithDimensionsRaw extends js.Object

object WithDimensions extends AbstractReactFacade( WithDimensionsRaw, WithDimensionsTranslation.Translation )

object WithDimensionsTranslation {

    @js.native
    trait JsRenderParameters extends js.Object {
        var outerHeight : Int = js.native
        var outerWidth : Int = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var render : js.Function1[ JsRenderParameters, Node ] = js.native
        var setHeight : js.Function1[ Int, Unit ] = js.native
        var setWidth : js.Function1[ Int, Unit ] = js.native
    }

    case class RenderParameters(
        outerHeight : Int,
        outerWidth : Int,
    )

    case class Props(
        render : RenderParameters => VdomNode,
        setHeight : Int => Callback,
        setWidth : Int => Callback,
    ) {
        def toJs : JsProps = {
            val jp = ( new js.Object ).asInstanceOf[ JsProps ]
            jp.render = ( rp : JsRenderParameters ) => {
                val rpScala = RenderParameters(
                    rp.outerHeight,
                    rp.outerWidth,
                )
                render( rpScala ).rawNode
            }
            jp.setHeight = i => setHeight( i ).runNow()
            jp.setWidth = i => setWidth( i ).runNow()
            jp
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }
}
