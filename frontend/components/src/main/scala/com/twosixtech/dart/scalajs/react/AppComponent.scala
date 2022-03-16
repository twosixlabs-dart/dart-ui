package com.twosixtech.dart.scalajs.react

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{BackendScope, Callback, CallbackTo}

trait AppProps[ MainPropsType, RenderPropsType, AppContext ] {
    val mainProps : MainPropsType
    val renderProps : RenderPropsType
    val context : AppContext
}

trait WithContextView[ AppContext, ContextView ] {
    def contextView( context : AppContext ) : ContextView
}

trait AppComponent[ MainProps, RenderProps, AppContext, ContextView, StateType ]
  extends ReactComponent[ AppProps[ MainProps, RenderProps, AppContext ], StateType ]

trait AppLifecycleMethods[ MainProps, RenderProps, AppContext, StateType ]
  extends LifecycleMethods[ AppProps[ MainProps, RenderProps, AppContext ], StateType ] {

    protected def componentDidMount( modState : (StateType => StateType) => Callback, props : MainProps )( implicit context : AppContext ) : Callback = Callback()
    protected def componentDidUpdate(
        modState : (StateType => StateType) => Callback,
        props : MainProps,
        prevProps : MainProps,
        state : StateType,
        prevState : StateType,
        prevContext : AppContext,
    )( implicit context : AppContext ) : Callback = Callback()
    protected def componentWillUnmount( props : MainProps )( implicit context : AppContext ) : Callback = Callback()
    protected def shouldComponentUpdate(
        props : MainProps,
        nextProps : MainProps,
        state : StateType,
        nextState : StateType,
        nextContext : AppContext,
    )( implicit context : AppContext ) : CallbackTo[ Boolean ] = CallbackTo( true )

    override protected def componentDidMount( cdm : Lifecycle.ComponentDidMount[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : Callback = {
        componentDidMount( cdm.modState, cdm.props.mainProps )(cdm.props.context )
    }
    override final protected def componentDidUpdate( cdu : Lifecycle.ComponentDidUpdate[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType, SnapshotType ] ) : Callback = {
        componentDidUpdate( cdu.modState, cdu.currentProps.mainProps, cdu.prevProps.mainProps, cdu.currentState, cdu.prevState, cdu.prevProps.context )( cdu.currentProps.context )
    }
    override final protected def componentWillUnmount( cwu : Lifecycle.ComponentWillUnmount[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : Callback = {
        componentWillUnmount( cwu.props.mainProps )( cwu.props.context )
    }
    override final protected def shouldComponentUpdate( scu : Lifecycle.ShouldComponentUpdate[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : CallbackTo[ Boolean ] = {
        shouldComponentUpdate( scu.currentProps.mainProps, scu.nextProps.mainProps, scu.currentState, scu.nextState, scu.nextProps.context )( scu.currentProps.context )
    }

}

trait AppLifecycleMethodsWithContextView[ MainProps, RenderProps, AppContext, ContextView, StateType ]
  extends LifecycleMethods[ AppProps[ MainProps, RenderProps, AppContext ], StateType ]
    with WithContextView[ AppContext, ContextView ] {

    protected def componentDidMount( contextView : ContextView, props : MainProps, modState : (StateType => StateType) => Callback )( implicit context : AppContext ) : Callback = Callback()
    protected def componentDidUpdate(
        modState : (StateType => StateType) => Callback,
        props : MainProps,
        prevProps : MainProps,
        state : StateType,
        prevState : StateType,
        stateView : ContextView,
        prevStateView : ContextView,
        prevContext : AppContext,
    )( implicit context : AppContext ) : Callback = Callback()
    protected def componentWillUnmount( contextView : ContextView, state : StateType, props : MainProps )( implicit context : AppContext ) : Callback = Callback()
    protected def shouldComponentUpdate(
        props : MainProps,
        nextProps : MainProps,
        state : StateType,
        nextState : StateType,
        stateView : ContextView,
        nextStateView : ContextView,
        nextContext : AppContext,
    )( implicit context : AppContext ) : CallbackTo[ Boolean ] = CallbackTo( true )

    override protected def componentDidMount( cdm : Lifecycle.ComponentDidMount[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : Callback = {
        componentDidMount( contextView( cdm.props.context ), cdm.props.mainProps, cdm.modState )( cdm.props.context )
    }
    override final protected def componentDidUpdate( cdu : Lifecycle.ComponentDidUpdate[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType, SnapshotType ] ) : Callback = {
        componentDidUpdate( ( stateModder : StateType => StateType ) => cdu.modState( stateModder ), cdu.currentProps.mainProps, cdu.prevProps.mainProps, cdu.currentState, cdu.prevState, contextView( cdu.currentProps.context ), contextView( cdu.prevProps.context ), cdu.prevProps.context )( cdu.currentProps.context )
    }
    override final protected def componentWillUnmount( cwu : Lifecycle.ComponentWillUnmount[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : Callback = {
        componentWillUnmount( contextView( cwu.props.context ), cwu.state, cwu.props.mainProps )( cwu.props.context )
    }
    override final protected def shouldComponentUpdate( scu : Lifecycle.ShouldComponentUpdate[ AppProps[ MainProps, RenderProps, AppContext ], StateType, BackendType ] ) : CallbackTo[ Boolean ] = {
        shouldComponentUpdate( scu.currentProps.mainProps, scu.nextProps.mainProps, scu.currentState, scu.nextState, contextView( scu.currentProps.context ), contextView( scu.nextProps.context ), scu.nextProps.context )( scu.currentProps.context )
    }

}

trait CoreComponentNoView[ MainProps, RenderProps, AppContext ]
  extends LifecycleReactComponent[ AppProps[ MainProps, RenderProps, AppContext ] ]
    with AppLifecycleMethods[ MainProps, RenderProps, AppContext, Unit ]
    with AppComponent[ MainProps, RenderProps, AppContext, AppContext, Unit ] {

    def render( props : MainProps )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override final protected def render( props : AppProps[ MainProps, RenderProps, AppContext ] ) : VdomElement = {
        render( props.mainProps )( props.renderProps, props.context )
    }
}

trait StatefulCoreComponentNoView[ MainProps, RenderProps, AppContext, State ]
  extends StatefulReactComponent[ AppProps[ MainProps, RenderProps, AppContext ], State ]
    with AppLifecycleMethods[ MainProps, RenderProps, AppContext, State ]
    with AppComponent[ MainProps, RenderProps, AppContext, AppContext, State ] {
    type Scope = BackendScope[ AppProps[ MainProps, RenderProps, AppContext ], State ]

    def render( scope : Scope,  state : State, props : MainProps )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override final protected def render(
        props : AppProps[ MainProps, RenderProps, AppContext ], state : State,
        scope : Scope ) : VdomElement = {
        render( scope, state, props.mainProps )( props.renderProps, props.context )
    }
}

trait CoreComponentWithView[ MainProps, RenderProps, AppContext, ContextView ]
  extends LifecycleReactComponent[ AppProps[ MainProps, RenderProps, AppContext ] ]
    with AppLifecycleMethodsWithContextView[ MainProps, RenderProps, AppContext, ContextView, Unit ]
    with AppComponent[ MainProps, RenderProps, AppContext, AppContext, Unit ] {

    def render( props : MainProps, contextView : ContextView )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override final protected def render( props : AppProps[ MainProps, RenderProps, AppContext ] ) : VdomElement = {
        render( props.mainProps, contextView( props.context ) )( props.renderProps, props.context )
    }
}

trait StatefulCoreComponentWithView[ MainProps, RenderProps, AppContext, ContextView, State ]
  extends StatefulReactComponent[ AppProps[ MainProps, RenderProps, AppContext ], State ]
    with AppLifecycleMethodsWithContextView[ MainProps, RenderProps, AppContext, ContextView, State ]
    with AppComponent[ MainProps, RenderProps, AppContext, AppContext, State ] {
    type Scope = BackendScope[ AppProps[ MainProps, RenderProps, AppContext ], State ]

    def render( scope : Scope,  state : State, props : MainProps, contextView : ContextView )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override final protected def render(
        props : AppProps[ MainProps, RenderProps, AppContext ], state : State,
        scope : Scope ) : VdomElement = {
        render( scope, state, props.mainProps, contextView( props.context ) )( props.renderProps, props.context )
    }
}

trait LayoutComponent[ MainProps, RenderProps, AppContext, ContextView, StateType ]
  extends AppComponent[ MainProps, RenderProps, AppContext, ContextView, StateType ] {
    type Scope = BackendScope[ AppProps[ MainProps, RenderProps, AppContext ], StateType ]
}

trait LayoutComponentNoView[ MainProps, RenderProps, AppContext, StateType ]
  extends StatefulReactComponent[ AppProps[ MainProps, RenderProps, AppContext ], StateType ]
    with AppLifecycleMethods[ MainProps, RenderProps, AppContext, StateType ]
    with LayoutComponent[ MainProps, RenderProps, AppContext, AppContext, StateType ] {

    def render( scope : Scope, state : StateType, props : MainProps )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override protected def render(
        props : AppProps[ MainProps, RenderProps, AppContext ], state : StateType,
        scope : Scope ) : VdomElement = {
        render( scope, state, props.mainProps )( props.renderProps, props.context )
    }
}

trait LayoutComponentWithView[ MainProps, RenderProps, AppContext, ContextView, StateType ]
  extends StatefulReactComponent[ AppProps[ MainProps, RenderProps, AppContext ], StateType ]
    with AppLifecycleMethodsWithContextView[ MainProps, RenderProps, AppContext, ContextView, StateType ]
    with LayoutComponent[ MainProps, RenderProps, AppContext, ContextView, StateType ] {

    def render( scope : Scope, props : MainProps, state : StateType, contextView : ContextView )( implicit renderProps : RenderProps, context : AppContext ) : VdomElement

    override protected def render(
        props : AppProps[ MainProps, RenderProps, AppContext ], state : StateType,
        scope : BackendScope[ AppProps[ MainProps, RenderProps, AppContext ], StateType ] ) : VdomElement = {
        render( scope, props.mainProps, state, contextView( props.context ) )( props.renderProps, props.context )
    }
}



trait RenderComponent
