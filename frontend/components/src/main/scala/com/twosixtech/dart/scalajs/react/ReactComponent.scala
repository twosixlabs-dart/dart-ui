package com.twosixtech.dart.scalajs.react

import japgolly.scalajs.react.component.Scala.{ BackendScope, Component, Unmounted }
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.internal.Box
import japgolly.scalajs.react.vdom.{ VdomElement, VdomNode }
import japgolly.scalajs.react.{ Callback, CallbackTo, Children, CtorType, ScalaComponent, UpdateSnapshot }

trait ReactComponent[ PropsType, StateType ] {
    type BackendType

    def apply( props : PropsType ) : Unmounted[ PropsType, StateType, BackendType ]
}

object ReactComponent {

    def functional[ PropsType ]( fn : PropsType => VdomNode ) : ReactComponent[ PropsType, Unit ] = {
        new SimpleReactComponent[ PropsType ] {
            override protected def render( props : PropsType ) : VdomNode = fn( props )
        }
    }

}

trait SimpleReactComponent[ PropsType ] extends ReactComponent[ PropsType, Unit ] {
    type BackendType = Unit

    protected def render( props : PropsType ) : VdomNode

    lazy val component = {
        ScalaComponent.builder[ PropsType ]
          .render_P( pr => render( pr ) )
          .build
    }

    override def apply( props : PropsType ) : Unmounted[PropsType, Unit, Unit] = component( props )
}

trait LifecycleMethods[ PropsType, StateType ] {
    type BackendType
    type SnapshotType = UpdateSnapshot.None#Value

    protected def componentDidMount( cdm : Lifecycle.ComponentDidMount[ PropsType, StateType, BackendType ] ) : Callback = Callback()
    protected def componentDidUpdate( cdu : Lifecycle.ComponentDidUpdate[ PropsType, StateType, BackendType, SnapshotType ] ) : Callback = Callback()
    protected def componentWillUnmount( cwu : Lifecycle.ComponentWillUnmount[ PropsType, StateType, BackendType ] ) : Callback = Callback()
    protected def shouldComponentUpdate( scu : Lifecycle.ShouldComponentUpdate[ PropsType, StateType, BackendType ] ) : CallbackTo[ Boolean ] = CallbackTo( true )
}

trait LifecycleReactComponent[ PropsType ]
  extends ReactComponent[ PropsType, Unit ]
    with LifecycleMethods[ PropsType, Unit ] {

    type BackendType = Unit

    protected def render( props : PropsType ) : VdomElement

    lazy val component = {
        ScalaComponent.builder[ PropsType ]
          .render_P( pr => render( pr ) )
          .componentDidMount( componentDidMount )
          .componentDidUpdate( componentDidUpdate )
          .componentWillUnmount( componentWillUnmount )
          .shouldComponentUpdate( shouldComponentUpdate )
          .build
    }

    override def apply( props : PropsType ) : Unmounted[PropsType, SnapshotType, SnapshotType] = component( props )
}

trait StatefulReactComponent[ PropsType, StateType ]
  extends ReactComponent[ PropsType, StateType ]
    with LifecycleMethods[ PropsType, StateType ] {

    type BackendType = Backend
    final type ScopeType = BackendScope[ PropsType, StateType ]

    val initialState : StateType

    protected def render( props : PropsType, state : StateType, scope : BackendScope[ PropsType, StateType ] ) : VdomElement

    private val outerRender : (PropsType, StateType, BackendScope[ PropsType, StateType ]) => VdomElement = render

    class Backend( scope : BackendScope[ PropsType, StateType ] ) {
        def render( props : PropsType, state : StateType ) : VdomElement = outerRender( props, state, scope )
    }

    lazy val component = {
        ScalaComponent.builder[ PropsType ]
          .initialState( initialState )
          .renderBackend[ Backend ]
          .componentDidMount( componentDidMount )
          .componentDidUpdate( componentDidUpdate )
          .componentWillUnmount( componentWillUnmount )
          .shouldComponentUpdate( shouldComponentUpdate )
          .build
    }

    override def apply( props : PropsType ) : Unmounted[PropsType, StateType, Backend] = component( props )
}
