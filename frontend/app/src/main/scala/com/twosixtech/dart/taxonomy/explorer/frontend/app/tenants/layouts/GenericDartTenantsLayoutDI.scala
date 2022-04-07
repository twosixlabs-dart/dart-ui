package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts

import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.form.textinput.{ TextInput, TextInputMui }
import com.twosixtech.dart.scalajs.layout.text.TextMui
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.{ DartTenantsDI, DartTenantsLayoutDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

trait GenericDartTenantsLayoutDI
  extends DartTenantsLayoutDeps {
	this : DartComponentDI
	  with DartTenantsDI
	  with DartContextDeps =>

	override type DartTenantsRenderContext = Unit
	override type DartTenantsLayoutState = Option[ String ]

	override val dartTenantsLayout : DartTenantsLayout = GenericDartTenantsLayout

	object GenericDartTenantsLayout extends DartTenantsLayout {
		override def render(
			scope : Scope,
			state : Option[ String ],
			props : DartTenants.LayoutProps
		)(
			implicit
			renderProps : Unit,
			context : DartContext
		) : VdomElement = {

			import GenericDartTenantsLayoutClasses._
			import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions._

			<.div(
				ButtonMui(
					onClick = props.refreshTenants,
					element = "Refresh",
					classes = Button.Classes( refreshButtonClass.cName ),
				),
				<.div(
					tenantsListClass.cName,
					props.tenants.map( v => <.div(
						tenantClass.cName,
						TextMui(
							v
						),
						ButtonMui(
							onClick = props.removeTenant( v ),
							element = "Remove",
							classes = Button.Classes( removeTenantButtonClass.cName )
						),
					) ).toVdomArray,
				),
				state match {
					case None =>
						ButtonMui(
							onClick = scope.setState( Some( "" ) ),
							element = "Add",
							classes = Button.Classes( addTenantButtonClass.cName )
						)
					case Some( tenantId ) =>
						<.div(
							TextInputMui( TextInput.Props(
								value = Some( tenantId ),
								onChange = Some( newId => scope.setState( Some( newId ) ) ),
								onEnter = Some( props.addTenant( tenantId ) >> scope.setState( None ) ),
								classes = TextInput.Classes( input = newTenantInputClass.cName )
							) ),
							ButtonMui(
								onClick = props.addTenant( tenantId ) >> scope.setState( None ),
								element = "Add",
								classes = Button.Classes( addTenantInputButtonClass.cName )
							),
						)
				},
			)
		}

		override val initialState : Option[ String ] = None
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
