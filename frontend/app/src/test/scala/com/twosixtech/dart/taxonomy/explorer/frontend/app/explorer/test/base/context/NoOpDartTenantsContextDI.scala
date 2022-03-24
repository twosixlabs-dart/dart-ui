package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context

import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants.DartTenantsContextDeps
import japgolly.scalajs.react.{ Callback, ScalaComponent }
import japgolly.scalajs.react.component.Scala.Unmounted

trait NoOpDartTenantsContextDI extends DartTenantsContextDeps {
	this : DartBackendDeps with ErrorHandlerDI =>

	override type DartTenantsContextState = Unit
	override val dartTenantsContext : DartTenantsContext = new DartTenantsContext {
		override type BackendType = Unit

		val component = ScalaComponent.builder[ DartTenantsContext.Props ]
		  .noBackend
		  .render_P( props => props.render( Seq.empty, Callback() ) )
		  .build

		override def apply( props : DartTenantsContext.Props ) : Unmounted[ DartTenantsContext.Props, Unit, BackendType ] =
			component( props )
	}

}
