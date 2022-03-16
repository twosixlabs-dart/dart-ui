package com.twosixtech.dart.scalajs.control

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.{BackendScope, Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.vdom.VdomNode
import org.scalajs.dom.window

object PollHandler {

    val DEFAULT_TIMEOUT : Int = 5000

    trait PollContext {
        val polling : Boolean

        def setPoll( poll : Callback, interval : Option[ Int ] = None, autoStart : Boolean = true ) : Callback

        val startPoll : Callback
        val pausePoll : Callback
        val removePoll : Callback
        def currentPoll : Option[ Callback ]

        def setTimeout( interval : Int ) : Callback

        val removeTimeout : Callback
        def currentTimeout : Option[ Int ]
    }

    case class Props(
        element : PollContext => VdomNode,
        initialPoll : Option[ PollContext => Callback ] = None,
        initialTimeout : Option[ Int ] = None,
        defaultTimeout : Option[ Int ] = None,
    )

    case class State(
        pollCallback : Option[ Callback ] = None,
        timeout : Option[ Int ] = None,
        polling : Boolean = false,
        domInterval : Option[ Int ] = None,
    )

    class Backend( scope : BackendScope[ Props, State ] ) {

        def PollContext(
            pollingIn : Boolean,
            setPollIn : (Option[ Callback ], Option[ Int ], Boolean) => Callback,
            currentPollIn : Option[ Callback ],
            currentTimeoutIn : Option[ Int ],
        ) : PollContext = new PollContext {

            override val polling : Boolean = pollingIn

            override def setPoll( poll : Callback, interval : Option[ Int ], autoStart : Boolean ) : Callback = {
                setPollIn( Some( poll ), interval, autoStart )
            }

            override val startPoll : Callback = {
                scope.state.flatMap( s => setPollIn( s.pollCallback, s.timeout, true ) )
            }
            override val pausePoll : Callback =
                scope.state.flatMap( s => setPollIn( s.pollCallback, s.timeout, false ) )
            override val removePoll : Callback = setPollIn( None, None, false )
            override def currentPoll : Option[ Callback ] = scope.state.map( _.pollCallback ).runNow()

            override def setTimeout( interval : Int ) : Callback = {
                scope.state.flatMap( s => setPollIn( s.pollCallback, Some( interval ), s.polling ) )
            }

            override val removeTimeout : Callback =
                scope.state.flatMap( s => setPollIn( s.pollCallback, None, s.polling ) )

            override def currentTimeout : Option[ Int ] = scope.state.map( _.timeout ).runNow()
        }

        def getCtx() : PollContext = scope.state.map( state =>
            PollContext(
                pollingIn = state.polling,
                setPollIn = setPollFn,
                currentPollIn = state.pollCallback,
                currentTimeoutIn = state.timeout,
            )
        ).runNow()

        def stopPoll( state : State ) : CallbackTo[ State ] = CallbackTo {
            state.domInterval.foreach( window.clearInterval )
            state.copy( polling = false, domInterval = None )
        }

        def startPoll( state : State ) : CallbackTo[ State ] = for {
            props <- scope.props
            stoppedState <- stopPoll( state )
            newState <- stoppedState.pollCallback match {
                case None => CallbackTo( stoppedState )
                case Some( pcb ) =>
                    val timeout = stoppedState.timeout.getOrElse( props.defaultTimeout.getOrElse( DEFAULT_TIMEOUT ) )
                    val newInterval = window.setInterval( ( ) => pcb.runNow(), timeout )
                    pcb >> CallbackTo( stoppedState.copy(
                        polling = true,
                        domInterval = Some( newInterval )
                    ) )
            }
        } yield newState

        val setPollFn : (Option[ Callback ], Option[ Int ], Boolean) => Callback =
            ( pollCallbackIn : Option[ Callback ], timeoutIn : Option[ Int ], pollingIn : Boolean ) => for {
                state <- scope.state
                finalState <- {
                    if ( pollCallbackIn == state.pollCallback
                         && timeoutIn == state.timeout
                         && pollingIn == state.polling ) CallbackTo( state )
                    else for {
                        stoppedState <- stopPoll( state ).map( _.copy(
                            pollCallback = pollCallbackIn,
                            timeout = timeoutIn,
                        ) )
                        updatedPollState <- pollCallbackIn match {
                            case None => CallbackTo( stoppedState )
                            case _ =>
                                if ( pollingIn ) startPoll( stoppedState )
                                else CallbackTo( stoppedState )
                        }
                    } yield updatedPollState
                }
                _ <- scope.setState( finalState )
            } yield ()

        val updatePollingFn : Boolean => Callback =
            ( pollingIn : Boolean ) => for {
                state <- scope.state
                finalState <- {
                    if ( pollingIn == state.polling ) CallbackTo( state )
                    else if ( pollingIn )
                        startPoll( state )
                    else stopPoll( state )
                }
                _ <- scope.setState( finalState )
            } yield ()

        def render( props : Props ) : VdomNode = props.element( getCtx() )
    }

    val component = ScalaComponent.builder[ Props ]
      .initialState( State() )
      .backend( new Backend( _ ) )
      .renderBackend
      .componentDidMount( cdm => {
          cdm.props.initialPoll match {
              case None => Callback()
              case Some( pfn ) =>
                  val pcb : Callback = Callback( pfn( cdm.backend.getCtx() ).runNow() )
                  cdm.backend.setPollFn( Some( pcb ), cdm.props.initialTimeout, true )
          }
      } )
      .componentWillUnmount( cwu => Callback {
          cwu.state.domInterval.foreach( window.clearInterval )
      } )
      .build

    def apply( props : Props ) : Unmounted[ Props, State, Backend ] = component( props )

    def apply( element : PollContext => VdomNode, initialPoll : Option[ PollContext => Callback ] = None, initialTimeout : Option[ Int ] = None, defaultTimeout : Option[ Int ] = None ) : Unmounted[ Props, State, Backend ] =
        apply( Props( element = element, initialPoll = initialPoll, initialTimeout = initialTimeout, defaultTimeout = defaultTimeout ) )

}
