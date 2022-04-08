package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.ContextHook
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.DartTenantsContextDeps
import japgolly.scalajs.react.component.Scala.{ BackendScope, Unmounted }
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.{ Callback, ScalaComponent }


trait InMemoryDartTenantsContextDI
  extends DartTenantsContextDeps {
	this : DartBackendDeps with ErrorHandlerDI =>

	object TestTenantsHook {
		val hookId : String = "TEST-TENANTS-HOOK"

		case class TestTenantsState(
			tenants : Seq[ DartTenant ] = Nil,
			displayedTenants : Seq[ DartTenant ] = Nil,
		)

		case class Hook(
			getState : () => TestTenantsState,
			modState : ( TestTenantsState => TestTenantsState ) => Unit,
		)

		val contextComponent = ContextHook.Component[ TestTenantsHook.Hook ]( Some( hookId ) )
	}

	import TestTenantsHook._

	override type DartTenantsContextState = TestTenantsState

	override val dartTenantsContext : DartTenantsContext = new DartTenantsContext {
		override type BackendType = Backend

		class Backend( scope : BackendScope[ DartTenantsContext.Props, TestTenantsState ] ) {
			def refresh : Callback = for {
				state <- scope.state
//				_ <- Callback( println( s"REFRESHING:\n==========\ntenants: ${state.tenants.mkString( ", " )} \ndisplayed tenants: ${state.displayedTenants.mkString( ", " )}" ) )
				_ <- scope.modState( st => st.copy( displayedTenants = st.tenants ) )
			} yield ()

			def render( props : DartTenantsContext.Props, state : TestTenantsState ) : VdomNode = {
//				println( s"RENDERING:\n=========\ntenants: ${state.tenants.mkString( ", " )} \ndisplayed tenants: ${state.displayedTenants.mkString( ", " )}" )

				val tenantsContext : TestTenantsHook.Hook = TestTenantsHook.Hook(
				  ( ) => scope.state.runNow(),
				  ( mod : TestTenantsState => TestTenantsState ) => ( for {
					  state <- scope.state
//					  _ <- Callback( println( s"UPDATING STATE:\n==============\nold state: $state\nnew state: ${mod( state )}" ) )
					  _ <- scope.modState( mod )
				  } yield () ).runNow(),
				)

				TestTenantsHook.contextComponent(
					(tenantsContext, props.render( state.displayedTenants, refresh ))
				)
			}
		}

		val component = ScalaComponent.builder[ DartTenantsContext.Props ]
		  .initialState( TestTenantsState() )
		  .backend( new Backend( _ ) )
		  .renderBackend
		  .build

		override def apply( props : DartTenantsContext.Props ) : Unmounted[ DartTenantsContext.Props, TestTenantsState, Backend ] =
			component( props )
	}

}
