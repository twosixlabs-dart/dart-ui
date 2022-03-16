package com.twosixtech.dart.scalajs.react

import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.types
import japgolly.scalajs.react.component.Js
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom
import japgolly.scalajs.react.{Callback, Children, JsComponent, ReactMouseEvent, ScalaComponent}

import scala.scalajs.js

trait StoJ[ ScalaProps, JsProps <: js.Object ] {
    def scalaToJsBuilder( props : ScalaProps, jsProps : JsProps ) : (JsProps, VdomNode)
    def scalaToJs( props : ScalaProps ) : (JsProps, VdomNode) = {
        val emptyJsProps = ( new js.Object ).asInstanceOf[ JsProps ]
        scalaToJsBuilder( props, emptyJsProps )
    }
}

trait ReactFacade[ ScalaProps, JsProps <: js.Object ] extends ReactComponent[ ScalaProps, Unit ] {
    override type BackendType = Unit

    val rawComponent : js.Object
    val converter : StoJ[ ScalaProps, JsProps ]

    val jsComponent = JsComponent[ JsProps, Children.Varargs, js.Object ]( rawComponent )
    val scalaComponent = ScalaComponent.builder[ ScalaProps ]
      .noBackend
      .render_P( p => apply( p, new js.Object ) )
      .build

    protected def mergeJsObjs( objs : js.Object* ) : js.Object = {
        val result = js.Dictionary.empty[ Any ]
        for {
            source <- objs
            (key, value) <- source.asInstanceOf[ js.Dictionary[ Any ] ]
        } result( key ) = value
        result.asInstanceOf[ js.Object ]
    }

    def scalaToJs( props : ScalaProps ) : (JsProps, VdomNode) = converter.scalaToJs( props )

    def apply(
        props : ScalaProps,
        additionalProps : js.Object,
    ) : Js.Unmounted[ JsProps, js.Object ] = {
        val (jsProps, children) = scalaToJs( props )
        jsComponent( mergeJsObjs( jsProps, additionalProps ).asInstanceOf[ JsProps ] )( children )
    }

    override def apply( props : ScalaProps ) : Unmounted[ ScalaProps, Unit, BackendType ] = {
        scalaComponent( props )
    }
}

abstract class AbstractReactFacade[ ScalaProps, JsProps <: js.Object ](
    override val rawComponent : js.Object,
    override val converter : StoJ[ ScalaProps, JsProps ]
) extends ReactFacade[ ScalaProps, JsProps ]

object EmptyConverter extends StoJ[ Unit, js.Object ] {
    override def scalaToJsBuilder( props : Unit, jsProps : js.Object ) : (js.Object, VdomNode) = (js.Object, EmptyVdom)
}

class SimpleFacade( rawComponent : js.Object )
    extends AbstractReactFacade[ Unit, js.Object ]( rawComponent, EmptyConverter )