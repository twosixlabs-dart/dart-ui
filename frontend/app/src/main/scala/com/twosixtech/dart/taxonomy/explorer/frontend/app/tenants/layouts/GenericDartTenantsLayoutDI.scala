package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts

import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.{ DartTenantsDI, DartTenantsLayoutDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement

trait GenericDartTenantsLayoutDI
  extends DartTenantsLayoutDeps {
	this : DartComponentDI
	  with DartTenantsDI
	  with DartContextDeps =>

	override type DartTenantsRenderContext = Unit
	override type DartTenantsLayoutState = Unit

	override val dartTenantsLayout : DartTenantsLayout = GenericDartTenantsLayout

	object GenericDartTenantsLayout extends DartTenantsLayout {
		override def render(
			scope : Scope,
			state : Unit,
			props : DartTenants.LayoutProps
		)(
			implicit
			renderProps : Unit,
			context : DartContext
		) : VdomElement = {
			???
		}

		override val initialState : Unit = ()
	}
}

object GenericDartTenantsLayoutClasses {
	val tenantsListClass : String = "tenants-list"
	val tenantClass : String = "tenant"
	val removeTenantButtonClass : String = "remove-tenant-button"
	val addTenantButtonClass : String = "add-tenant-button"
	val newTenantInputClass : String = "new-tenant-input"
	val addTenantInputButtonClass : String = "add-tenant-input-button"
	val refreshButtonClass : String = "tenants-refresh-button-class"
}
