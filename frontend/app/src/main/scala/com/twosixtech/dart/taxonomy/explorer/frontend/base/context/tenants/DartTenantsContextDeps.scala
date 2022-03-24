package com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode

trait DartTenantsContextDeps {
	this : DartBackendDeps with ErrorHandlerDI =>

	type DartTenantsContextState

	val dartTenantsContext : DartTenantsContext

	trait DartTenantsContext extends ReactComponent[ DartTenantsContext.Props, DartTenantsContextState ]

	object DartTenantsContext {
		case class Props(
			backend : DartBackend.Cx,
			report : ErrorHandler.PublicReporter,
			render : (Seq[ DartTenant ], Callback) => VdomNode
		)

		def ContextBuilder( backend : DartBackend.Cx, report : ErrorHandler.PublicReporter )( render : (Seq[ DartTenant ], Callback) => VdomNode ) : VdomNode =
			dartTenantsContext( Props( backend, report, render ) )
	}

}
