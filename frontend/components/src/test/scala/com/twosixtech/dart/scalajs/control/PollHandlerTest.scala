package com.twosixtech.dart.scalajs.control

import com.twosixtech.dart.scalajs.control.PollHandler.PollContext
import com.twosixtech.dart.scalajs.react.ReactTestUtilities
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import utest._

import scala.concurrent.Future

object PollHandlerTest extends TestSuite {

    class TestBackend( scope : BackendScope[ PollContext, Unit ] ) {
        def getContext() : PollContext = scope.props.runNow()
    }

    def TestComponent(
        onMount : PollContext => Callback = _ => Callback(),
        render : PollContext => VdomNode = _ => "",
    ) = ScalaComponent.builder[ PollContext ]
      .backend( new TestBackend( _ ) )
      .render_P( pc => {
          <.div(
              <.div( ^.id := "currentPoll", s"${pc.currentPoll}" ),
              <.div( ^.id := "polling", s"${pc.polling}" ),
              <.div( render( pc ) ),
          )
      } )
      .componentDidMount( cdm => onMount( cdm.props ) )
      .build

    override def tests : Tests = Tests {

        test( "PollContext.currentPoll should be empty if not set" ) {
            val testComponent = TestComponent()

            val poll = ReactTestUtilities.testOn( PollHandler( pc => testComponent( pc ) ) ) {
                _.querySelector( "#currentPoll" ).textContent
            }

            assert( poll == "None" )
        }

        test( "PollContent.currentPoll should not be empty if set, but should not be polling if not started" ) {
            val testComponent = TestComponent( onMount = _.setPoll( poll = Callback(), autoStart = false ) )

            val (pollTxt, pollingTxt) = ReactTestUtilities.testOn( PollHandler( pc => testComponent( pc ) ) ) {
                ele =>
                    val ph = ele.querySelector( "#currentPoll" ).textContent
                    val pb = ele.querySelector( "#polling" ).textContent

                    (ph, pb)
            }

            assert( pollTxt.startsWith( "Some(") )
            assert( pollingTxt == "false" )
        }

        test( "PollContent.currentPoll should not be empty if set, and should be set to polling if started" ) {
            val testComponent = TestComponent( onMount = _.setPoll( poll = Callback() ) )

            val (pollTxt, pollingTxt) = ReactTestUtilities.testOn( PollHandler( pc => testComponent( pc ) ) ) {
                ele =>
                    val ph = ele.querySelector( "#currentPoll" ).textContent
                    val pb = ele.querySelector( "#polling" ).textContent

                    (ph, pb)
            }

            assert( pollTxt.startsWith( "Some(") )
            assert( pollingTxt == "true" )
        }

        import scalajs.concurrent.JSExecutionContext.Implicits.queue

        test( "PollContent.polling should transition from false to true when PollContext.startPoll is called" ) {
            val testComponent = TestComponent(
                onMount = pc => pc.setPoll(
                    poll = Callback(),
                    autoStart = false
                ) >> Callback( AsyncUtils.after( 250 )( pc.startPoll.runNow() ) )
            )

            ReactTestUtilities.asyncTestOn( PollHandler( pc => testComponent( pc ) ) ) {
                ele =>
                  for {
                      p1 <- Future {
                          ele.querySelector( "#polling" ).textContent
                      }
                      p2 <- AsyncUtils.after( 3000 ) {
                          ele.querySelector( "#polling" ).textContent
                      }
                  } yield (p1, p2)
            } map { case (pollingTxt1, pollingTxt2) =>
                assert( pollingTxt1 == "false" )
                assert( pollingTxt2 == "true" )
            }
        }

        test( "PollContent.polling should transition from true to false when PollContext.pausePoll is called" ) {
            val testComponent = TestComponent(
                onMount = pc => pc.setPoll(
                    poll = Callback(),
                    autoStart = true
                ) >> Callback( AsyncUtils.after( 250 )( pc.pausePoll.runNow() ) )
            )

            ReactTestUtilities.asyncTestOn( PollHandler( pc => testComponent( pc ) ) ) {
                ele =>
                    for {
                        p1 <- Future {
                            ele.querySelector( "#polling" ).textContent
                        }
                        p2 <- AsyncUtils.after( 3000 ) {
                            ele.querySelector( "#polling" ).textContent
                        }
                    } yield (p1, p2)
            } map { case (pollingTxt1, pollingTxt2) =>
                assert( pollingTxt1 == "true" )
                assert( pollingTxt2 == "false" )
            }
        }

        test( "It should actually poll when it says its polling and not poll when it says it's not polling" ) {
            var count = 0

            val testComponent = TestComponent(
                onMount = {
                    pc =>
                        pc.setPoll(
                            poll = Callback {
                                count += 1
                            },
                            interval = Some( 1000 ),
                            autoStart = false
                        ) >>
                        Callback( AsyncUtils.after( 5000 )( pc.startPoll.runNow() ) ) >>
                        Callback( AsyncUtils.after( 10000 )( pc.pausePoll.runNow() ) )
                },
            )

            ReactTestUtilities.asyncTestOn( PollHandler( pc => testComponent( pc ) ) ) {
                ele =>
                    def getPandC() : (Boolean, Int) = {
                        val p = ele.querySelector( "#polling" ).textContent.toBoolean
                        val c = count
                        (p, c)
                    }
                    for {
                        (p1, c1) <- Future( getPandC() )
                        (p2, c2) <- AsyncUtils.after( 4000 )( getPandC() )
                        (p3, c3) <- AsyncUtils.after( 5000 )( getPandC() )
                        (p4, c4) <- AsyncUtils.after( 5000 )( getPandC() )
                    } yield {
                        // on mount, poll handler is set but not polling
                        assert(!p1)
                        assert(c1 == 0)
                        // after 4 seconds, polling still hasn't started
                        assert(!p2)
                        assert(c2 == 0)
                        // by 9 seconds, polling has been going on for ~4 seconds
                        assert(p3)
                        assert(c3 == 3 || c3 == 4 || c3 == 5 )
                        // by 14 seconds, polling went on for ~5 seconds, but stopped after that
                        assert(!p4)
                        assert(c4 == 4 || c4 == 5 || c4 == 6)
                    }
            }

        }

        test( "PollContext.currentPoll should not be empty if PollContext.polling should be true if initial state is set in props" ) {
            val testComponent = TestComponent()

            ReactTestUtilities.testOn( PollHandler(
                element = pc => testComponent( pc ),
                initialPoll = Some( pc => Callback( AsyncUtils.after( 2000 )( pc.removePoll.runNow() ) ) ),
                initialTimeout = Some( 10000 ),
            ) ) {
                ele =>
                  def getHandB() : (String, Boolean) = {
                      val ph = ele.querySelector( "#currentPoll" ).textContent
                      val pb = ele.querySelector( "#polling" ).textContent
                      (ph, pb.toBoolean)
                  }
                  for {
                      (ph1, pb1) <- Future( getHandB() )
                      (ph2, pb2) <- AsyncUtils.after( 4000 )( getHandB() )
                  } yield {
                      assert( ph1.startsWith( "Some(") )
                      assert( pb1 )

                      assert( !pb2 )
                      assert( ph2 == "None" )
                  }
            }


        }

        test( "PollHandler should stop polling on unmount" ) {
            var count : Int = 0

            val testComponent = TestComponent()

            val Wrapper = ScalaComponent.builder[ Unit ]
              .initialState( true )
              .noBackend
              .render_S( if ( _ ) {
                  PollHandler( testComponent( _ ), initialPoll = Some( _ => Callback( count += 1 ) ), Some( 2000 ) )
              } else <.div() )
              .componentDidMount( cdm => {
                  Callback( AsyncUtils.after( 2000 )( cdm.setState( false ).runNow() ) )
              } )
              .build

            ReactTestUtilities.testOn( Wrapper() ) {
                ele =>
                    for {
                        (c1) <- Future( count )
                        (c2) <- AsyncUtils.after( 2500 )( count )
                        (c3) <- AsyncUtils.after( 3000 )( count )
                    } yield {
                        assert( c1 == 1 )
                        assert( c2 == 2 )
                        assert( c3 == 2 )
                    }
            }
        }
    }
}
