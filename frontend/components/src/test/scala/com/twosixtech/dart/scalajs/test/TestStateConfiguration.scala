package com.twosixtech.dart.scalajs.test

import com.twosixtech.dart.scalajs.react.ReactTestUtilities
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._
import teststate.data.Id
import teststate.run.PlanLike
import utest._

import scala.language.{higherKinds, implicitConversions}
import scala.util.{Failure, Try}

trait TestStateConfiguration {

    type Obs
    type St

    val dsl : Dsl[ Id, Unit, Obs, St, String ]

    def genObs( ele : HTMLElement ) : Obs

    def setupAllPlans( plan : dsl.Plan ) : dsl.Plan =
        plan.addInvariants( dsl.emptyInvariant )

    val defaultInitialState : St

    type PlanMagnet = Either[ dsl.Plan, dsl.PlanWithInitialState ]

    implicit def PlanToMagnet( plan : dsl.Plan ) : PlanMagnet = Left( plan )

    implicit def PlanWithInitialStateToMagnet( plan : dsl.PlanWithInitialState ) : PlanMagnet = Right( plan )

    def runPlan( reactElement : VdomElement, plan : Either[ dsl.Plan, dsl.PlanWithInitialState ] ) : Id[ Report[ String ] ] = {
        ReactTestUtilities.testOn( reactElement )( ele => {

            def observe( ) : Obs = genObs( ele.asInstanceOf[ HTMLElement ] )

            val planWithInitialState : dsl.PlanWithInitialState = ( plan match {
                case Left( p ) => setupAllPlans( p ).withInitialState( defaultInitialState )
                case Right( p ) => setupAllPlans( p.plan ).withInitialState( p.initialState )
            } )

            planWithInitialState
              .test( Observer watch observe() )
              .runU()

        } )
    }

    implicit class RunnablePlanWIS( plan : dsl.PlanWithInitialState ) {
        def runOn( reactElement : VdomElement ) : Id[ Report[ String ] ] = runPlan( reactElement, plan )
    }

    implicit class RunnablePlan( plan : dsl.Plan ) {
        def runOn( reactElement : VdomElement ) : Id[ Report[ String ] ] = runPlan( reactElement, plan )
    }

    implicit class TestableTestStateReport( report : Id[ Report[ String ] ] ) {
        def utest() : Unit = Try {
            report.assert()
        } match {
            case Failure( e : java.lang.AssertionError ) =>
                throw AssertionError( e.getMessage, Nil )
            case Failure( e ) =>
                throw AssertionError( "Unexpected exception from test-state", Nil, e )
            case _ =>
        }
    }

}
