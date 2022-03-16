package com.twosixtech.dart.scalajs.layout.events

import com.twosixtech.dart.scalajs.layout.events.KeyHandler.WithKeyHandler
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.raw.{ClientRect, MouseEvent}
import org.scalajs.dom.{document, html, window}

import scala.scalajs.js

object ClickOffHandler extends ReactComponent[ ClickOffHandlerProps, Unit ] {
    override type BackendType = Backend

    class Backend( scope : BackendScope[ ClickOffHandlerProps, Unit ] ) {

        var outerRef : html.Element = scope.props.runNow().ref.getOrElse( null )

        val clickOutsideHandler : js.Function1[ MouseEvent, Unit ] = ( event : MouseEvent ) => {
            val ref = scope.props.runNow().ref match {
                case None => outerRef
                case Some( r ) => r
            }
            if ( ref != null ) {
                val rect : ClientRect = ref.getBoundingClientRect()
                if ( event.clientX < rect.left ||
                     event.clientX > rect.right ||
                     event.clientY < rect.top ||
                     event.clientY > rect.bottom ) {
                    scope.props.runNow().onClick( event ).runNow()
                }
            }
        }

        def render( props : ClickOffHandlerProps ): VdomElement = {
            val tagMod : TagMod = props.ref match {
                case None => ^.untypedRef( v => outerRef = v.asInstanceOf[ html.Element ] )
                case Some( _ ) => ^.disabled := false
            }
            <.div(
                tagMod,
                props.className match {
                    case None => ""
                    case Some( cn ) => ^.className := cn
                },
                props.element,
            )
        }

    }

    private lazy val component = ScalaComponent.builder[ ClickOffHandlerProps ]
      .renderBackend[ BackendType ]
      .componentDidMount( cdm => Callback {
          document.addEventListener( "mousedown", cdm.backend.clickOutsideHandler )
      } )
      .componentWillUnmount( cwu => Callback {
          document.removeEventListener( "mousedown", cwu.backend.clickOutsideHandler )
      } )
      .build

    override def apply(
        props : ClickOffHandlerProps ) : Unmounted[ClickOffHandlerProps, Unit, Backend] = {
        component( props )
    }

    object Implicits {

        implicit class ClickOffableElement( element : VdomNode ) {
            def withClickOff(
                onClick : Callback,
                className : Option[ String ] = None,
            ) : Unmounted[ ClickOffHandlerProps, Unit, Backend ] = apply( ClickOffHandlerProps(
                _ => onClick,
                element,
                None,
                className,
            ) )
        }

        implicit def ClickOffableUnmounted[ A, B, C ]( um : Unmounted[ A, B, C ] ) : ClickOffableElement = {
            ClickOffableUnmounted( um )
        }

    }
}

case class ClickOffHandlerProps(
    onClick : MouseEvent => Callback,
    element : VdomNode,
    ref : Option[ html.Element ] = None,
    className : Option[ String ] = None,
)
