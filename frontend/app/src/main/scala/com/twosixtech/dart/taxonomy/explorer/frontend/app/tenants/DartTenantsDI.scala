package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants

import com.twosixlabs.dart.auth.tenant.{ CorpusTenant, DartTenant, GlobalCorpus }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement

trait DartTenantsDI {
	this : DartComponentDI
	  with DartTenantsLayoutDeps
	  with DartContextDeps =>

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
			val addTenant : String => Callback = {
				( tenantName : String ) =>
					???
			}

			val removeTenant : String => Callback = {
				( tenantName : String ) =>
					???
			}

			dartTenantsLayout( DartTenants.LayoutProps(
				context.tenants map {
					case GlobalCorpus => DartTenant.globalId
					case CorpusTenant( id, _ ) => id
				},
				addTenant,
				removeTenant,
			).toDartProps )
		}
	}

	object DartTenants {
		case class Props( )

		case class LayoutProps(
			tenants : Seq[ String ],
			addTenant : String => Callback,
			removeTenant : String => Callback,
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
