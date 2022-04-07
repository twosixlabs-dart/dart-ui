package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants

import com.twosixlabs.dart.auth.tenant.{ CorpusTenant, DartTenant, GlobalCorpus }
import com.twosixtech.dart.scalajs.backend.{ HttpMethod, HttpRequest, HttpResponse }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

import scala.util.{ Failure, Success }

trait DartTenantsDI {
	this : DartComponentDI
	  with DartTenantsLayoutDeps
	  with DartContextDeps
	  with DartConfigDeps =>

	class DartTenants
	  extends SimpleDartComponent[
		DartTenants.Props,
		DartTenantsRenderContext,
	  ] {
		override def render(
			props : DartTenants.Props
		)(
			implicit
			renderProps : DartTenantsRenderContext,
			context : DartContext
		) : VdomElement = {
			val client = context.backendContext.authClient

			import scalajs.concurrent.JSExecutionContext.Implicits.queue

			val addTenant : String => Callback = {
				( tenantName : String ) =>
					Callback( client.submit(
						method = HttpMethod.Post,
						request = HttpRequest(
							url = dartConfig.tenantsBaseUrl + "/" + tenantName,
						),
					).onComplete {
						case Success( HttpResponse( _, 201, _ ) ) => context.refreshTenants.runNow()
						case Success( HttpResponse( _, status, body ) ) =>
							context.report.logMessage( "Unable to add tenant", s"status: $status, body: ${body.toString}" )
							  .>>( context.refreshTenants ).runNow()
						case Failure( e ) =>
							context.report.logMessage( "Unable to add tenant", e )
							  .>>( context.refreshTenants ).runNow()
					} )
			}

			val removeTenant : String => Callback = {
				( tenantName : String ) =>
					Callback( client.submit(
						method = HttpMethod.Delete,
						request = HttpRequest(
							url = dartConfig.tenantsBaseUrl + "/" + tenantName,
						),
					).onComplete {
						case Success( HttpResponse( _, 200, _ ) ) => context.refreshTenants.runNow()
						case Success( HttpResponse( _, status, body ) ) =>
							context.report.logMessage( "Unable to remove tenant", s"status: $status, body: ${body.toString}" )
							  .>>( context.refreshTenants ).runNow()
						case Failure( e ) =>
							context.report.logMessage( "Unable to remove tenant", e )
							  .>>( context.refreshTenants ).runNow()
					} )
			}

			dartTenantsLayout( DartTenants.LayoutProps(
				context.tenants map {
					case GlobalCorpus => DartTenant.globalId
					case CorpusTenant( id, _ ) => id
				},
				addTenant,
				removeTenant,
				context.refreshTenants,
			).toDartProps )
		}
	}

	object DartTenants {
		case class Props( )

		case class LayoutProps(
			tenants : Seq[ String ],
			addTenant : String => Callback,
			removeTenant : String => Callback,
			refreshTenants : Callback,
		)
	}

}

trait DartTenantsLayoutDeps {
	this : DartTenantsDI
	  with DartComponentDI =>

	type DartTenantsRenderContext
	type DartTenantsLayoutState

	val dartTenantsLayout : DartTenantsLayout

	trait DartTenantsLayout
	  extends DartLayoutComponent[
		DartTenants.LayoutProps,
		DartTenantsRenderContext,
		DartTenantsLayoutState,
	  ]

}
