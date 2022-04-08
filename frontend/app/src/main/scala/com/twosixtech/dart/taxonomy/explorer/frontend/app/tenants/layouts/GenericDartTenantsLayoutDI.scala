package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.layout.button.HoverButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.IconButton
import com.twosixtech.dart.scalajs.layout.button.iconbutton.mui.IconButtonMui
import com.twosixtech.dart.scalajs.layout.button.regular.Button
import com.twosixtech.dart.scalajs.layout.button.regular.mui.ButtonMui
import com.twosixtech.dart.scalajs.layout.div.flex.DartFlex.FlexItem
import com.twosixtech.dart.scalajs.layout.div.flex.{ DartFlex, DartFlexBasic }
import com.twosixtech.dart.scalajs.layout.div.panel.{ DartPanel, DartPanelMui }
import com.twosixtech.dart.scalajs.layout.events.{ ClickOffHandler, ClickOffHandlerProps }
import com.twosixtech.dart.scalajs.layout.form.textinput.{ TextInput, TextInputMui }
import com.twosixtech.dart.scalajs.layout.icon.Icons.SyncIconMui
import com.twosixtech.dart.scalajs.layout.text.{ Text, TextMui }
import com.twosixtech.dart.scalajs.layout.types
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.{ DartTenantsDI, DartTenantsLayoutDeps }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

import scala.language.postfixOps

trait GenericDartTenantsLayoutDI
  extends DartTenantsLayoutDeps {
	this : DartComponentDI
	  with DartTenantsDI
	  with DartContextDeps =>

	override type DartTenantsRenderContext = Unit
	override type DartTenantsLayoutState = Option[ String ]

	override val dartTenantsLayout : DartTenantsLayout = GenericDartTenantsLayout


	object GenericDartTenantsLayout extends DartTenantsLayout {

		import scalacss.DevDefaults._

		object Styles extends StyleSheet.Inline {

			import dsl._

			val invisible = style( visibility.hidden )
			val spaceRight = style( marginRight( 10 px ) )
			val panel = style(
				paddingLeft( 10 px ),
				paddingRight( 10 px ),
				paddingTop( 5 px ),
				paddingBottom( 10 px ),
				marginTop( 24 px ),
				margin.auto,
				width( 552 px ),
				height.unset,
			)
			val outerWrapper = style(
				height( 100 %% ),
				width( 100 %% ),
				paddingTop( 24 px ),
			)
			val tenantItem = style( marginBottom( 5 px ) )
			val addTenant = style()
			val tenantsList = style(
				paddingTop( 20 px ),
				maxWidth( 300 px ),
				margin.auto,
			)
		}

		Styles.addToDocument()

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
				Styles.outerWrapper.cName,
				DartPanelMui( DartPanel.Props(
					fullHeight = false,
					 classes = DartPanel.Classes( Styles.panel.cName ),
					 element = <.div(
						 DartFlexBasic(
							  direction = types.Row,
							  align = types.AlignCenter,
							  items = Vector(
								  DartFlex.FlexItem(
									  TextMui(
										   element = "Tenants",
										   size = types.Large,
										   color = Some( types.Primary ),
									  ),
									  classes = DartFlex.ItemClasses(
										   Styles.spaceRight.cName,
									  )
								  ),
								  DartFlex.FlexItem(
									  IconButtonMui( IconButton.Props(
										   onClick = props.refreshTenants,
										   icon = SyncIconMui(),
										   classes = IconButton.Classes( refreshButtonClass.cName ),
									  ) ),
								  ),
							  )
						 ),
						 <.div(
							 DartFlexBasic(
								 classes = DartFlex.Classes(
									 container = Styles.tenantsList and tenantsListClass,
									 items = Styles.tenantItem.cName,
								 ),
								 direction = types.Column,
								 align = types.AlignStart,
								 items = props.tenants.map( v =>
									 DartFlex.FlexItem(
										 key = Some( v ),
										 classes = DartFlex.ItemClasses( tenantClass.cName ),
										 element = {
											 if ( v != DartTenant.globalId ) HoverButton(
												 left = true,
												 remove = true,
												 onClick = Some( props.removeTenant( v ) ),
												 element = TextMui(
													 element = v,
													 classes = Text.Classes( tenantNameClass.cName )
												 ),
												 classes = HoverButton.Classes(
													 button = removeTenantButtonClass.cName,
												 ),
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
											 )
										 },
									 )
								 ).toVector :+ FlexItem(
									 classes = DartFlex.ItemClasses( Styles.addTenant.cName ),
									 align = Some( types.AlignCenter ),
									 element = state match {
										 case None =>
											 ButtonMui(
												 element = TextMui(
													 element = <.b( "+" ),
													 size = types.Large,
												 ),
												 size = Button.Small,
												 style = Button.Text,
												 onClick = scope.setState( Some( "" ) ),
												 classes = Button.Classes( addTenantButtonClass.cName )
											 )
										 case Some( tenantId ) =>
											 <.div(
												 HoverButton(
													 onClick = Some( props.addTenant( tenantId ) >> scope.setState( None ) ),
													 element = ClickOffHandler( ClickOffHandlerProps(
														 onClick = _ => scope.setState( None ),
														 element = TextInputMui( TextInput.Props(
															 value = Some( tenantId ),
															 autoFocus = true,
															 onChange = Some( newId => scope.setState( Some( newId ) ) ),
															 onEnter = Some( props.addTenant( tenantId ) >> scope.setState( None ) ),
															 onEscape = Some( scope.setState( None ) ),
															 variant = TextInput.Outlined,
															 size = types.Small,
															 classes = TextInput.Classes( input = newTenantInputClass.cName )
														 ) )
													 ) ),
													 classes = HoverButton.Classes(
														 button = addTenantInputButtonClass.cName,
													 ),
												 ),
											 )
									 },
								 ),
							 )
						 ),
					 ) ),
				 )
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
