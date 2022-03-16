package com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit

import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartStateDI
import diode.react.{ModelProxy, ReactConnectProps, ReactConnectProxy, ReactConnector}
import diode.{ActionHandler, ActionResult, ActionType, Circuit, ModelRW, RootModelRW}
import japgolly.scalajs.react.{Callback, CtorType, ScalaComponent, preventDefaultAndStopPropagation}
import japgolly.scalajs.react.component.Generic
import japgolly.scalajs.react.component.Scala.{BackendScope, Component}
import japgolly.scalajs.react.vdom.VdomElement

trait DartCircuitDeps {
    this : DartStateDI =>

    class DartCircuit extends Circuit[ DartState ] with ReactConnector[ DartState ] {

        override protected def initialModel : DartState = DartCircuitContext.initState

        val rmw = new RootModelRW[ DartState ]( initialModel )

        val setStateHandler : ActionHandler[ DartState, DartState ] = new ActionHandler[ DartState, DartState ]( rmw ) {
            override protected def handle : PartialFunction[ Any, ActionResult[ DartState ] ] = {
                case SetState( newState ) => updated( newState )
            }
        }

        val exposedHandler : HandlerFunction = foldHandlers(
            composeHandlers( setStateHandler ) +:
              ( DartCircuitContext.coreHandlers.map( v => composeHandlers( v.resolve( rmw ) ) ) ++
                DartCircuitContext.layoutHandlers.map( v => composeHandlers( v.resolve( rmw ) ) ) ) : _*
        )

        override protected def actionHandler : HandlerFunction = exposedHandler

        final val connector : ReactConnectProxy[ DartState ] = connect( v => v )

        final def connectComponent(
            component : Component[ ModelProxy[ DartState ], _, _, CtorType.Summoner.Aux[ _, _, CtorType.Props ]#CT ] ) : Generic.UnmountedWithRoot[ ReactConnectProps[ DartState
        ], _, _, _ ] = {
            connector( p => component( p ) )
        }

    }

    sealed trait DartAction extends ActionType[ DartAction ]
    trait CoreAction extends DartAction
    trait LayoutAction extends DartAction
    case class SetState( state : DartState ) extends DartAction

    trait CoreHandler[ HandledState ] {

        final private[ base ] def resolve( modelRW : ModelRW[ DartState, DartState ] ) : ActionHandler[ DartState, HandledState ] = {
            val zoomedModelRW = zoomFn( modelRW.zoomTo( _.coreState ) )
            new ActionHandler( zoomedModelRW ) {
                override protected def handle : PartialFunction[ Any, ActionResult[ DartState ] ] = {
                    case ca : CoreAction if handler( value ).isDefinedAt( ca ) => updated( handler( value )( ca ) )
                }
            }
        }

        def zoomFn( model : ModelRW[ DartState, CoreState ] ) : ModelRW[ DartState, HandledState ]

        def handler( state : HandledState ) : PartialFunction[ CoreAction, HandledState ]
    }

    trait LayoutHandler[ HandledState ] {

        final private[ base ] def resolve( modelRW : ModelRW[ DartState, DartState ] ) : ActionHandler[ DartState, HandledState ] = {
            val zoomedModelRW = zoomFn( modelRW.zoomTo( _.layoutState ) )
            new ActionHandler[ DartState, HandledState ]( zoomedModelRW ) {
                override protected def handle : PartialFunction[ Any, ActionResult[ DartState ] ] = {
                    case ca : LayoutAction if handler( value ).isDefinedAt( ca ) => updated( handler( value )( ca ) )
                }
            }
        }

        def zoomFn( model : ModelRW[ DartState, LayoutState ] ) : ModelRW[ DartState, HandledState ]

        def handler( state : HandledState ) : PartialFunction[ LayoutAction, HandledState ]
    }


    trait DartCircuitContext {

        val initState : DartState

        def coreHandlers : Seq[ CoreHandler[ _ ] ]
        def layoutHandlers : Seq[ LayoutHandler[ _ ] ]

        private def connector( circuit : DartCircuit ) : ReactConnectProxy[ DartState ] = circuit.connect( v => v )

        case class Context(
            circuit : DartCircuit,
            dispatcher : DartAction => Callback,
            state : DartState,
        )

        class ContextBuilderBackend( scope : BackendScope[ Context => VdomElement, Unit ] ) {
            val dartCircuit = new DartCircuit

            import diode.AnyAction.aType
            def render( renderer : Context => VdomElement ) : VdomElement = {
                connector( dartCircuit )( ( proxy : ModelProxy[ DartState ] ) => {
                    val dispatcher = ( action : DartAction ) => proxy.dispatchCB( action )
                    val state = proxy.modelReader.value
                    renderer( Context( dartCircuit, dispatcher, state ) )
                } )
            }
        }

        lazy val ContextBuilder = {
            import diode.AnyAction.aType
            ScalaComponent.builder[ Context => VdomElement ]
              .initialState()
              .backend( new ContextBuilderBackend( _ ) )
              .renderBackend
              .componentDidMount( _ => Callback( "Dart circuit context builder mounted" ) )
              .build
        }

        type Handler[ CoreState, LayoutState ] = (DartState, Any) => Option[ ActionResult[ DartState ] ]

        case class Something( i : Long, j : Boolean )

        def coreHandler[ ZoomState ]( zoom : ModelRW[ DartState, CoreState ] => ModelRW[ DartState, ZoomState ] )
          ( handle : ZoomState => PartialFunction[ CoreAction, ZoomState ] ) : CoreHandler[ ZoomState ] = {
            new CoreHandler[ ZoomState ] {
                override def zoomFn( model : ModelRW[ DartState, CoreState ] ) : ModelRW[ DartState, ZoomState ] = zoom( model )

                override def handler( state : ZoomState ) : PartialFunction[ CoreAction, ZoomState ] = {
                    handle( state )
                }
            }
        }

        def layoutHandler[ ZoomState ]( zoom : ModelRW[ DartState, LayoutState ] => ModelRW[ DartState, ZoomState ] )
          ( handle : ZoomState => PartialFunction[ LayoutAction, ZoomState ] ) : LayoutHandler[ ZoomState ] = {
            new LayoutHandler[ ZoomState ] {
                override def zoomFn( model : ModelRW[ DartState, LayoutState ] ) : ModelRW[ DartState, ZoomState ] = zoom( model )

                override def handler( state : ZoomState ) : PartialFunction[ LayoutAction, ZoomState ] = {
                    handle( state )
                }
            }
        }
    }

    val DartCircuitContext : DartCircuitContext
}
