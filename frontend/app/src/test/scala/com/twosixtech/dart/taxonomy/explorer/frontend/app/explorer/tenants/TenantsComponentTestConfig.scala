package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants

import com.twosixlabs.dart.auth.tenant.DartTenant
import com.twosixtech.dart.scalajs.backend.HttpBody.NoBody
import com.twosixtech.dart.scalajs.backend.{ HttpBody, HttpMethod, HttpRequest, HttpResponse }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.context.InMemoryDartTenantsContextDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.dart.component.test.DartComponentTestStateConfiguration
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.raw.HTMLElement
import teststate.Exports._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait TenantsComponentTestConfig
  extends DartComponentTestStateConfiguration
    with InMemoryDartTenantsContextDI {

    val testRenderContext : TenantOntologyComponentRenderContext

    override def defaultRenderer( implicit context : DartContext ) : VdomElement = {
        tenantOntologyComponent( TenantOntologyComponent.Props().toDartPropsRC( testRenderContext ) )
    }

    case class TOContents(
        publishedVersion : Boolean,
        stagedVersion : Boolean,
    )

    abstract class ObsType( ele : HTMLElement )
      extends TestHookObserver( ele ) {

        import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.ContextHook.HookedNode

        def getTenantsHook : TestTenantsHook.TestTenantsHook = ele.retrieveContext[ TestTenantsHook.TestTenantsHook ]( Some( TestTenantsHook.hookId ) )

        // Read state
        def getContextTenants : Seq[ DartTenant ] = getDartContext().tenants
        def contextTenantExists( tenant : DartTenant ) : Boolean = getContextTenants.contains( tenant )
        def getMockedTenants : Seq[ DartTenant ] = getTenantsHook.getState().tenants
        def mockedTenantExists( tenant : DartTenant ) : Boolean = getMockedTenants.contains( tenant )

        // Update state
        def addMockedTenant( tenant : String ) : Unit = getTenantsHook.modState( v => v.copy( tenants = DartTenant.fromString( tenant ) +: v.tenants ) )
        def removeMockedTenant( tenant : String ) : Unit = getTenantsHook.modState( v => v.copy( tenants = v.tenants.filter( _ != DartTenant.fromString( tenant ) ) ) )
        def clearMockedTenants() : Unit = getTenantsHook.modState( v => v.copy( tenants = Nil ) )
        def refreshContextTenants() : Unit = getDartContext().refreshTenants.runNow()

        // Read methods
        def getTenants : Seq[ String ]
        def tenantExists( tenantId : String ) : Boolean

        // Action methods
        def addTenant( tenant : String ) : Unit
        def removeTenant( tenant : String ) : Unit
        def refresh() : Unit

        def setTenantsBackendHandler() : Unit = {
            val hook = getTenantsHook
            getDartContext().backendContext.mockContext.setHandler { ( method, request ) =>
                (method, request) match {
                    case (HttpMethod.Post, HttpRequest( TenantUrlPattern( tenantId ), _, HttpBody.NoBody )) =>
                        hook.modState( v => v.copy( tenants = DartTenant.fromString( tenantId ) +: v.tenants ) )
                        HttpResponse( Map.empty[ String, String ], 201, NoBody )
                    case (HttpMethod.Delete, HttpRequest( TenantUrlPattern( tenantId ), _, HttpBody.NoBody )) =>
                        hook.modState( v => v.copy( tenants = v.tenants.filter( _ != DartTenant.fromString( tenantId ) ) ) )
                        HttpResponse( Map.empty[ String, String ], 200, NoBody )
                    case (m, r) =>
                        throw new Exception( s"Unexpected request: method: $m, response: $r" )
                }
            }
        }
    }

    override type Obs <: ObsType

    def genObs( ele : HTMLElement ) : Obs

    override type St = Unit

    override val defaultInitialState : Unit = ()

    // Focus definitions
    def currentTenants : dsl.FocusColl[ Seq, String ] = dsl.focus( "Tenants" ).collection( _.obs.getTenants )
    def contextTenants : dsl.FocusColl[ Seq, DartTenant ] = dsl.focus( "Tenants in DartContext" ).collection( _.obs.getContextTenants )
    def mockedTenants : dsl.FocusColl[ Seq, DartTenant ] = dsl.focus( "Mocked tenants" ).collection( _.obs.getMockedTenants )
    def tenantExistence( tenant : String ) : dsl.FocusValue[ Boolean ] = dsl.focus( s"Existence of tenant $tenant" ).value( _.obs.tenantExists( tenant ) )
    def contextTenantExistence( tenant : DartTenant ) : dsl.FocusValue[ Boolean ] = dsl.focus( s"Existence of tenant $tenant in DartContext" ).value( _.obs.contextTenantExists( tenant ) )
    def mockedTenantExistence( tenant : DartTenant ) : dsl.FocusValue[ Boolean ] = dsl.focus( s"Existence of mocked tenant $tenant" ).value( _.obs.mockedTenantExists( tenant ) )

    // Actions definitions
    def addTenant( tenant : String ) : dsl.Actions = dsl.action( s"Add tenant $tenant" )( v => Future( v.obs.addTenant( tenant  ) ) )
    def addMockedTenant( tenant : String ) : dsl.Actions = dsl.action( s"Add mocked tenant $tenant" )( v => Future( v.obs.addMockedTenant( tenant  ) ) )
    def removeTenant( tenant : String ) : dsl.Actions = dsl.action( s"Remove tenant $tenant" )( v => Future( v.obs.removeTenant( tenant ) ) )
    def removeMockedTenant( tenant : String ) : dsl.Actions = dsl.action( s"Remove mocked tenant $tenant" )( v => Future( v.obs.removeMockedTenant( tenant ) ) )
    def clearMockedTenants() : dsl.Actions = dsl.action( s"Clear mocked tenants" )( v => Future( v.obs.clearMockedTenants() ) )
    def refreshTenants() : dsl.Actions = dsl.action( "Refresh tenants" )( v => Future( v.obs.refresh() ) )
    def refreshContextTenants() : dsl.Actions = dsl.action( "Refresh tenants via DartContext" )( v => Future( v.obs.refreshContextTenants() ) )

    private val tenantUrlString = s"${dartConfig.tenantsBaseUrl}/(.+)"
    private val TenantUrlPattern = tenantUrlString.r

    // Backend handler
    def setBackendAction( ) : dsl.Actions = dsl.action( s"Set backend handler" )( v => Future( v.obs.setTenantsBackendHandler() ) )
}
