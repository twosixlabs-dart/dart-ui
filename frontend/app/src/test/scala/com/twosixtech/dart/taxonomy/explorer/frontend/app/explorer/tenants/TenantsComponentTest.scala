package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants

import com.twosixlabs.dart.auth.tenant.{ CorpusTenant, DartTenant, GlobalCorpus }
import teststate.Exports._
import utest.{ Tests, test }

import scalajs.concurrent.JSExecutionContext.Implicits.queue

trait TenantsComponentTest
  extends TenantsComponentTestConfig {

    implicit def tenantEq : Equal[ DartTenant ] = new Equal[ DartTenant ]( _ == _ )

    override def tests : Tests = Tests {
        test( "Check integration between mocking and DartContext" ) {
            Plan.action(
                clearMockedTenants()
                >> refreshContextTenants()
                >> refreshContextTenants()
                  +> mockedTenants.assert.equal()
                  +> contextTenants.assert.equal()
                >> addMockedTenant( "test-tenant" )
                  +> mockedTenants.assert.equal( CorpusTenant( "test-tenant" ) )
                  // Shouldn't end up in context until context is refreshed
                  +> contextTenants.assert.equal()
                >> refreshContextTenants()
                  +> mockedTenants.assert.equal( CorpusTenant( "test-tenant" ) )
                  +> contextTenants.assert.equal( CorpusTenant( "test-tenant" ) )
                >> addMockedTenant( DartTenant.globalId )
                >> refreshContextTenants()
                  +> mockedTenants.assert.equalIgnoringOrder( CorpusTenant( "test-tenant" ), GlobalCorpus )
                  +> contextTenants.assert.equalIgnoringOrder( CorpusTenant( "test-tenant" ), GlobalCorpus )
                >> removeMockedTenant( "test-tenant" )
                >> refreshContextTenants()
                  +> mockedTenants.assert.equal( GlobalCorpus )
                  +> contextTenants.assert.equal( GlobalCorpus )
                >> removeMockedTenant( DartTenant.globalId )
                >> refreshContextTenants()
                  +> mockedTenants.assert.equal()
                  +> contextTenants.assert.equal()
            ).run().map( _.assert() )
        }

        test( "Displaying tenants" ) {
            test( "should display no tenants" ) {
                Plan.action(
                    clearMockedTenants()
                      >> refreshContextTenants()
                      +> currentTenants.assert.equal()
                ).run().map( _.assert() )
            }

            test( "should display a single tenant" ) {
                Plan.action(
                    dsl.emptyAction
                      >> clearMockedTenants()
                      >> addMockedTenant( "t1" )
                      >> refreshContextTenants()
                      +> html.map( _.contains( "t1" ) ).assert.equal( true )
                      +> currentTenants.assert.equal( "t1" )
                ).run().map( _.assert() )
            }

            test( "should display multiple tenants" ) {
                Plan.action(
                    clearMockedTenants()
                      >> addMockedTenant( "t1" )
                      >> addMockedTenant( "t2" )
                      >> addMockedTenant( "t3" )
                      >> addMockedTenant( "t4" )
                      >> addMockedTenant( "t5" )
                      >> refreshContextTenants()
                      +> currentTenants.assert.equalIgnoringOrder( "t1", "t2", "t3", "t4", "t5" )
                ).run().map( _.assert() )
            }
        }

        test( "Adding Tenants" ) {
            test( "should add a tenant" ) {
                Plan.action(
                    setBackendAction
                    >> clearMockedTenants()
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal()
                    >> addTenant( "test-tenant" )
                      +> currentTenants.assert.equal( "test-tenant" )
                ).run().map( _.assert() )
            }

            test( "should add multiple tenants" ) {
                Plan.action(
                    setBackendAction
                    >> clearMockedTenants()
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal()
                    >> addTenant( "t1" )
                    >> addTenant( "t2" )
                    >> addTenant( "t3" )
                      +> currentTenants.assert.equalIgnoringOrder( "t1", "t2", "t3" )
                ).run().map( _.assert() )
            }
        }

        test( "Removing Tenants" ) {
            test( "should remove a tenant" ) {
                Plan.action(
                    setBackendAction
                    >> clearMockedTenants()
                    >> addMockedTenant( "test-tenant" )
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal( "test-tenant" )
                    >> removeTenant( "test-tenant" )
                      +> currentTenants.assert.equal()
                ).run().map( _.assert() )
            }

            test( "should remove multiple tenants" ) {
                Plan.action(
                    setBackendAction
                    >> clearMockedTenants()
                    >> addMockedTenant( "test-tenant" )
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal( "test-tenant" )
                    >> removeTenant( "test-tenant" )
                      +> currentTenants.assert.equal()
                ).run().map( _.assert() )
            }

            test( "should not be able to remove global tenant" ) {
                Plan.action(
                    setBackendAction
                    >> clearMockedTenants()
                    >> addMockedTenant( DartTenant.globalId )
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal( DartTenant.globalId )
                    >> removeTenant( DartTenant.globalId )
                      +> currentTenants.assert.equal( DartTenant.globalId )
                ).run().map( _.assert() )
            }
        }

        test( "Refresh" ) {
            test( "should reveal updates when mocked tenants change" ) {
                Plan.action(
                    clearMockedTenants()
                    >> refreshContextTenants()
                      +> currentTenants.assert.equal()
                    >> addMockedTenant( "test-tenant" )
                      +> currentTenants.assert.equal()
                    >> refreshTenants()
                      +> currentTenants.assert.equal( "test-tenant" )
                ).run().map( _.assert() )
            }
        }
    }

}
