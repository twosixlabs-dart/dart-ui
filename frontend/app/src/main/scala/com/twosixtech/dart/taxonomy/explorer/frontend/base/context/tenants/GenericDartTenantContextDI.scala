package com.twosixtech.dart.taxonomy.explorer.frontend.base.context.tenants

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.backend.{ HttpBody, HttpMethod, HttpRequest, HttpResponse }
import com.twosixtech.dart.scalajs.control.PollHandler
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.component.Scala.{ BackendScope, Unmounted }
import japgolly.scalajs.react.{ Callback, ScalaComponent }
import japgolly.scalajs.react.vdom.{ VdomElement, VdomNode }
import upickle.default._

import scala.util.{ Failure, Success }

trait GenericDartTenantContextDI extends DartTenantsContextDeps {
	this : DartBackendDeps with DartConfigDeps with ErrorHandlerDI =>

	override type DartTenantsContextState = Seq[ DartTenant ]

	override lazy val dartTenantsContext : DartTenantsContext = new GenericDartTenantContext {}

	trait GenericDartTenantContext extends DartTenantsContext {
		lazy val tenantsUrl : String = dartConfig.tenantsBaseUrl

		override type BackendType = Backend

		class Backend( scope : BackendScope[ DartTenantsContext.Props, Seq[ DartTenant ] ] ) {
			import scalajs.concurrent.JSExecutionContext.Implicits.queue

			def refreshTenants : Callback = {
				scope.props map { ( props : DartTenantsContext.Props ) =>
					props.backend.authClient.submit(
						HttpMethod.Get,
						HttpRequest(
							tenantsUrl,
						),
					) onComplete {
						case Success( HttpResponse( _, 200, HttpBody.TextBody( body ) ) ) =>
							val tenantIds = read[ Seq[ String ] ]( body )
							val newTenants = tenantIds.map( DartTenant.fromString )
							scope.setState( newTenants ).runNow()
						case Success( HttpResponse( _, 200, HttpBody.BinaryBody( body ) ) ) =>
							val tenantIds = readBinary[ Seq[ String ] ]( body )
							val newTenants = tenantIds.map( DartTenant.fromString )
							scope.setState( newTenants ).runNow()
						case Success( HttpResponse( _, 200, HttpBody.NoBody ) ) =>
							props.report.logMessage( "No tenants data available" )
						case Success( HttpResponse( _, status, body ) ) =>
							props.report.logMessage( s"Unable to retrieve tenants data", s"Unable to retrieve tenants data. Response status: ${status}. Response body: ${body.toString}" )
						case Failure( e ) =>
							props.report.logMessage( "Unable to retrieve tenants data", e )
					}
				}
			}

			def render( props : DartTenantsContext.Props, state : Seq[ DartTenant ] ) : VdomNode = {
				props.render( state, refreshTenants )
			}
		}

		val component = ScalaComponent.builder[ DartTenantsContext.Props ]
		  .initialState( Seq.empty[ DartTenant ] )
		  .backend( v => new Backend( v ) )
		  .renderBackend
		  .componentDidMount( cdm => cdm.backend.refreshTenants )
		  .build

		override def apply(
			props : DartTenantsContext.Props
		) : Unmounted[DartTenantsContext.Props, Seq[DartTenant], Backend] = component( props )
	}
}
