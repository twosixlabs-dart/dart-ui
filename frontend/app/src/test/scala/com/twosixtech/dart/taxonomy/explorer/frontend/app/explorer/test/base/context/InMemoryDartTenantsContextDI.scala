package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.DartTenantsContextDeps
import japgolly.scalajs.react.component.Scala.{ BackendScope, Unmounted }
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.{ Callback, ScalaComponent }
import org.scalajs.dom.document

import java.util.UUID
import scala.scalajs.js
import scala.util.{ Failure, Success, Try }

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions



trait InMemoryDartTenantsContextDI
  extends DartTenantsContextDeps {
	this : DartBackendDeps with ErrorHandlerDI =>

	object TestTenantsHook {
		private val eleId : String = UUID.randomUUID().toString

		@js.native
		trait TestTenantsHook extends js.Object {
			var getState : js.Function0[ TestTenantsState ] = js.native
			var modState : js.Function1[ TestTenantsState => TestTenantsState, Unit ] = js.native
		}

		def injectContext( getter : () => TestTenantsState, setter : ( TestTenantsState => TestTenantsState ) => Unit ) : Unit = {
			val hook = ( new js.Object ).asInstanceOf[ TestTenantsHook.TestTenantsHook ]
			hook.modState = setter
			hook.getState = getter

			val injectionSite = document.querySelectorAll( s"#$eleId" ).vector.headOption  match {
				case Some( node ) => node.asInstanceOf[ js.Dynamic ]
				case None =>
					val ele = document.createElement( "div" )
					ele.setAttribute( "id", eleId );
					ele
			}

			injectionSite.asInstanceOf[ js.Dynamic ].contextHook = hook
		}

		def getState : TestTenantsState =
			document.querySelector( s"#$eleId" )
			  .asInstanceOf[ js.Dynamic ]
			  .contextHook
			  .asInstanceOf[ TestTenantsHook ]
			  .getState()

		def modState( mod : TestTenantsState => TestTenantsState ) : Unit =
			document.querySelector( s"#$eleId" )
			  .asInstanceOf[ js.Dynamic ]
			  .contextHook
			  .asInstanceOf[ TestTenantsHook ]
			  .modState( mod )

		case class TestTenantsState(
			tenants : Seq[ DartTenant ] = Nil,
			displayedTenants : Seq[ DartTenant ] = Nil,
		)
	}

	import TestTenantsHook._

	override type DartTenantsContextState = TestTenantsState

	override val dartTenantsContext : DartTenantsContext = new DartTenantsContext {
		override type BackendType = Backend


		class Backend( scope : BackendScope[ DartTenantsContext.Props, TestTenantsState ] ) {
			def injectContext() : Unit =
				TestTenantsHook.injectContext(
					() => scope.state.runNow(),
					( modder : TestTenantsState => TestTenantsState ) => scope.modState( modder ).runNow() )

			def refresh : Callback = scope.modState( st => st.copy( displayedTenants = st.tenants ) )

			def render( props : DartTenantsContext.Props, state: TestTenantsState ) : VdomNode = {
				props.render( state.displayedTenants, refresh )
			}
		}

		val component = ScalaComponent.builder[ DartTenantsContext.Props ]
		  .initialState( TestTenantsState() )
		  .backend( scope => {
			  val be = new Backend( scope )
			  be.refresh
			  be
		  } )
		  .renderBackend
		  .build

		override def apply( props : DartTenantsContext.Props ) : Unmounted[ DartTenantsContext.Props, TestTenantsState, Backend ] =
			component( props )
	}

}
