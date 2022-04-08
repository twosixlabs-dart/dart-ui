package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.form.textinput.{ TextInput, TextInputMui }
import com.twosixtech.dart.scalajs.layout.icon.Icons.SyncIconMui
import com.twosixtech.dart.scalajs.layout.text.{ Text, TextMui }
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts.GenericStateAccessComponentLayoutClasses.refreshButtonClass
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.{ DartTenantsDI, DartTenantsLayoutDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
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

	import scalacss.DevDefaults._

	object Styles extends StyleSheet.Inline {

		import dsl._

		val invisible = style( visibility.hidden )
	}

	Styles.addToDocument()

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
				IconButtonMui( IconButton.Props(
					onClick = props.refreshTenants,
					icon = SyncIconMui(),
					classes = IconButton.Classes( refreshButtonClass.cName ),
				) ),
				<.div(
					tenantsListClass.cName,
					props.tenants.map( v => <.div(
						^.key := v,
						tenantClass.cName,
						TextMui(
							v,
							classes = Text.Classes( tenantNameClass.cName ),
						),
						if ( v == DartTenant.globalId ) HoverButton(
							left = true,
							remove = true,
							onClick = Some( props.removeTenant( v ) ),
							element = TextMui(
								element = v,
								classes = Text.Classes( tenantNameClass.cName )
							),
							classes = HoverButton.Classes(
								button = removeTenantButtonClass.cName,
							)

						) else HoverButton(
							left = true,
							remove = true,
							onClick = None,
							element = TextMui(
								element = v,
								classes = Text.Classes( tenantNameClass.cName )
							),
							classes = HoverButton.Classes(
								button = Styles.invisible.cName,
							),
						),
					) ).toVdomArray,
				),
				state match {
					case None =>
						ButtonMui(
							element = "+",
							size = Button.Small,
							style = Button.Text,
							onClick = scope.setState( Some( "" ) ),
							classes = Button.Classes( addTenantButtonClass.cName )
						)
					case Some( tenantId ) =>
						<.div(
							HoverButton(
								onClick = Some( props.addTenant( tenantId ) >> scope.setState( None ) ),
								element = TextInputMui( TextInput.Props(
									value = Some( tenantId ),
									autoFocus = true,
									onChange = Some( newId => scope.setState( Some( newId ) ) ),
									onEnter = Some( props.addTenant( tenantId ) >> scope.setState( None ) ),
									onEscape = Some( scope.setState( None ) ),
									variant = TextInput.Outlined,
									size = types.Small,
									classes = TextInput.Classes( input = newTenantInputClass.cName )
								) ),
								classes = HoverButton.Classes(
									button = addTenantInputButtonClass.cName,
								),
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
	val tenantNameClass : String = "tenant-name"
	val removeTenantButtonClass : String = "remove-tenant-button"
	val addTenantButtonClass : String = "add-tenant-button"
	val newTenantInputClass : String = "new-tenant-input"
	val addTenantInputButtonClass : String = "add-tenant-input-button"
	val refreshButtonClass : String = "tenants-refresh-button-class"
}
