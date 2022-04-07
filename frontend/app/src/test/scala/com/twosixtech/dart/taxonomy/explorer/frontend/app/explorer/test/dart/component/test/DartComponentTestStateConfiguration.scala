package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test

import com.twosixtech.dart.scalajs.control.AsyncUtils
import com.twosixtech.dart.scalajs.test.MockBackendClientComponent.MockBackendContext
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.ContextHook
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._

import scala.concurrent.Future

trait DartComponentTestStateConfiguration
  extends DartComponentTestBase
    with DartComponentTestRenderer {

    abstract class TestHookObserver( ele : HTMLElement ) {

        import ContextHook.HookedNode

        def getDartContext( ) : DartContext = {
            ele.retrieveContext[ ContextHook ]( Some( contextHookId ) )
              .getContext()
              .asInstanceOf[ DartContext ]
        }

        def dispatch( action : DartAction ) : Unit = getDartContext().dispatch( action ).runNow()
        def setBackendResponse( setter : MockBackendContext => Callback ) : Unit = {
            setter( getDartContext().backendContext.mockContext ).runNow()
        }

    }

    type Obs <: TestHookObserver
    type St

    val dsl : Dsl[ Future, Unit, Obs, St, String ]

    def genObs( ele : HTMLElement ) : Obs

    def setupAllPlans( plan : dsl.Plan ) : dsl.Plan =
        plan.addInvariants( dsl.emptyInvariant )

    val defaultInitialState : St

    type PlanMagnet = Either[ dsl.Plan, dsl.PlanWithInitialState ]

    import scala.concurrent.ExecutionContext.Implicits.global

    // Actions using context hook
    def dispatch( action : DartAction ) : dsl.Actions =
        dsl.action( s"Dispatch action: ${action.getClass.getSimpleName}" )( v => {
            Future.successful( v.obs.dispatch( action ) ).flatMap( _ => AsyncUtils.delay( 1000 ) ).flatMap( _ => AsyncUtils.delay( 1000 ) )
        } )
    def setBackendResponse( setter : MockBackendContext => Callback ) : dsl.Actions =
        dsl.action( s"Updating backend response" )( v => Future( v.obs.setBackendResponse( setter ) ) )

    implicit def PlanToMagnet( plan : dsl.Plan ) : PlanMagnet = Left( plan )

    implicit def PlanWithInitialStateToMagnet( plan : dsl.PlanWithInitialState ) : PlanMagnet = Right( plan )

    def runPlan( runner : TestRunner = defaultRunner )( plan : Either[ dsl.Plan, dsl.PlanWithInitialState ] ) : Future[ Report[ String ] ] = {
        runner.runTest( ele => {

            def observe( ) : Obs = genObs( ele.asInstanceOf[ HTMLElement ] )

            val planWithInitialState : dsl.PlanWithInitialState = ( plan match {
                case Left( p ) => setupAllPlans( p ).withInitialState( defaultInitialState )
                case Right( p ) => setupAllPlans( p.plan ).withInitialState( p.initialState )
            } )

            planWithInitialState
              .test( Observer watch observe() ).withRetryPolicy( Retry.Policy.fixedIntervalAndAttempts( 1000, 5 ) )
              .runU()

        } )
    }

    implicit class RunnablePlanWIS( plan : dsl.PlanWithInitialState ) {
        def run( ) : Future[ Report[ String ] ] = runPlan()( plan )

        def runUsing( runner : TestRunner ) : Future[ Report[ String ] ] = runPlan( runner )( plan )
    }

    implicit class RunnablePlan( plan : dsl.Plan ) {
        def run( ) : Future[ Report[ String ] ] = runPlan()( plan )

        def runUsing( runner : TestRunner ) : Future[ Report[ String ] ] = runPlan( runner )( plan )

    }

}
