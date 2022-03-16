package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import japgolly.scalajs.react.{Callback, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html

import scala.scalajs.js

@js.native
trait ContextHook extends js.Object {
    var getContext : Function0[ Any ] = js.native
}

object ContextHook {

    val className : String = s"test-context-hook"

    implicit class HookedNode( outerElement : html.Element ) {
        def injectContext[ T ]( context : T ) : Unit = {
            val injectionSite = outerElement.querySelectorAll( s".$className" )
              .vector
              .headOption match {
                case None =>
                    outerElement
                case Some( node ) => node
            }

            injectionSite.asInstanceOf[ js.Dynamic ].contextHook = apply( context )
        }

        def retrieveContext[ T ] : T = {
            val injectionSite = outerElement.querySelectorAll( s".$className" )
              .vector
              .headOption match {
                case None =>
                    outerElement
                case Some( node ) => node
            }

            injectionSite.asInstanceOf[ js.Dynamic ].contextHook.asInstanceOf[ T ]
        }

        def cleanupContext() : Unit = {
            val injectionSite = outerElement.querySelectorAll( s".$className" )
              .vector
              .headOption match {
                case None =>
                    outerElement
                case Some( node ) => node
            }

            injectionSite.asInstanceOf[ js.Dynamic ].contextHook = js.undefined
        }
    }

    def apply[ T ]( context : T ) : ContextHook = {
        val tch = ( new js.Object ).asInstanceOf[ ContextHook ]
        tch.getContext = () => context
        tch
    }

    class ComponentBackend[ T ]( scope : BackendScope[ (T, VdomElement), Unit ] ) {
        val ref = Ref[ html.Element ]

        def render( props : (T, VdomElement) ): VdomElement = {
            val (_, children) = props
            <.div(
                <.div(
                    ^.className := ContextHook.className,
                ),
                children
            ).withRef( ref )
        }
    }

    def Component[ T ] = ScalaComponent.builder[ (T, VdomElement) ]
      .initialState()
      .backend( new ComponentBackend[ T ]( _ ) )
      .renderBackend
      .componentDidMount( cdm => {
          val (dartContext, _) = cdm.props
          cdm.backend.ref.foreach( _.injectContext( dartContext ) )
      } )
      .componentDidUpdate( cdu => {
          val (dartContext, _) = cdu.currentProps
          cdu.backend.ref.foreach( _.injectContext( dartContext ) )
      } )
      .build
}
