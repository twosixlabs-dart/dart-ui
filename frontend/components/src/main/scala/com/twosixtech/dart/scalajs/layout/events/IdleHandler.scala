package com.twosixtech.dart.scalajs.layout.events

import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.{Callback, CtorType, PropsChildren, ScalaComponent}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import scala.scalajs.js
import scala.scalajs.js.Date

object IdleHandler {
    case class Props(
        checkIntervalMs : Int,
        idleTimoutMs : Int,
        onIdle : Callback,
        className : Option[ String ] = None,
    )

    class Backend( scope : BackendScope[ Props, Unit ] ) {

        var lastTime : Option[ Double ] = None
        var interval : Option[ Int ] = None

        def idleCallback() : Unit = {
            scope.props.runNow().onIdle.runNow()
            lastTime = None
            interval.foreach( window.clearInterval )
            interval = None
        }

        val checkIdle : () => Unit = () => {
            for {
                lastT <- lastTime
                _ <- interval
            } {
                val currentTime = Date.now()
                if ( currentTime - lastT > scope.props.runNow().idleTimoutMs ) idleCallback()
            }
        }

        val onActivity : js.Function1[ Any, Unit ] = _ => {
            lastTime = Some( Date.now() )

            if ( interval.isEmpty ) {
                interval = Some( window.setInterval( checkIdle, scope.props.runNow().checkIntervalMs ) )
            }
        }

        def render( p : Props, c : PropsChildren ) : VdomNode = {
            <.div(
                p.className match {
                    case None => ""
                    case Some( cn ) => ^.className := cn
                },
                c,
                ^.onMouseDown ==> ( e => Callback( onActivity( e ) ) ),
                ^.onMouseMove ==> ( e => Callback( onActivity( e ) ) ),
                ^.onWheel ==> ( e => Callback( onActivity( e ) ) ),
            )
        }

    }

    val component = ScalaComponent.builder[ Props ]
      .backend( new Backend( _ ) )
      .renderBackendWithChildren
      .componentDidMount( cdm => Callback {
          cdm.backend.onActivity()
//          window.addEventListener( "mousemove", cdm.backend.onActivity )
//          window.addEventListener( "keypress", cdm.backend.onActivity )
//          window.addEventListener( "scroll", cdm.backend.onActivity, true )
      } )
      .componentWillUnmount( cwu => Callback {
          cwu.backend.interval.foreach( window.clearInterval )
//          window.removeEventListener( "mousemove", cwu.backend.onActivity )
//          window.removeEventListener( "keypress", cwu.backend.onActivity )
//          window.removeEventListener( "scroll", cwu.backend.onActivity, true )
      } )
      .build

    def apply( props : Props )( children : CtorType.ChildArg* ) : Unmounted[ Props, Unit, Backend ] =
        component( props )( children : _* )

}
