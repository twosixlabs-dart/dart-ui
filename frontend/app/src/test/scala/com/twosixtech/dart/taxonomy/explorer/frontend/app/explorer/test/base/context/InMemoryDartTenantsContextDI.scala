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

		case class TestTenantsHook(
			getState : () => TestTenantsState,
			modState : ( TestTenantsState => TestTenantsState ) => Unit,
		)
	}

	import TestTenantsHook._

	override type DartTenantsContextState = TestTenantsState

	override val dartTenantsContext : DartTenantsContext = new DartTenantsContext {
		override type BackendType = Backend

		private val contextComponent = ContextHook.Component[ TestTenantsHook.TestTenantsHook ]( Some( hookId ) )

		class Backend( scope : BackendScope[ DartTenantsContext.Props, TestTenantsState ] ) {
			def refresh : Callback = scope.modState( st => st.copy( displayedTenants = st.tenants ) )

			def render( props : DartTenantsContext.Props, state : TestTenantsState ) : VdomNode = {
				val tenantsContext : TestTenantsHook.TestTenantsHook = TestTenantsHook.TestTenantsHook(
				  ( ) => scope.state.runNow(),
				  ( mod : TestTenantsState => TestTenantsState ) => scope.modState( mod ).runNow(),
				)

				contextComponent(
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
