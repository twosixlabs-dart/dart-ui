package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.DartTenantsContextDeps
import japgolly.scalajs.react.component.Scala.{ BackendScope, Unmounted }
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.{ Callback, ScalaComponent }


trait InMemoryDartTenantsContextDI
  extends DartTenantsContextDeps {
	this : DartBackendDeps with ErrorHandlerDI =>

	object TestTenantsHook {
		object TestTenantsService {
			var tenants : Seq[ DartTenant ] = Seq.empty
			def addTenant( tenant : String ) : Unit =
				tenants = DartTenant.fromString( tenant ) +: tenants
			def removeTenant( tenant : String ) : Unit =
				tenants = tenants.filter( _ != DartTenant.fromString( tenant ) )
		}

		case class TestTenantsState(
			tenants : Seq[ DartTenant ]
		)
	}

	import TestTenantsHook._

	override type DartTenantsContextState = TestTenantsState

	override val dartTenantsContext : DartTenantsContext = new DartTenantsContext {
		override type BackendType = Backend


		class Backend( scope : BackendScope[ DartTenantsContext.Props, TestTenantsState ] ) {
			def refresh : Callback = scope.modState( _.copy( tenants = TestTenantsService.tenants ) )

			def render( props : DartTenantsContext.Props, state: TestTenantsState ) : VdomNode = {
				props.render( state.tenants, refresh )
			}
		}

		val component = ScalaComponent.builder[ DartTenantsContext.Props ]
		  .initialState( TestTenantsState( TestTenantsService.tenants ) )
		  .backend( new Backend( _ ) )
		  .renderBackend
		  .build

		override def apply( props : DartTenantsContext.Props ) : Unmounted[ DartTenantsContext.Props, TestTenantsState, Backend ] =
			component( props )
	}

}
