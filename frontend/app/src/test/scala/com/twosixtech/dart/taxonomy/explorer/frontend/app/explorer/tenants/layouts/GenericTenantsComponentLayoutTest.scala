package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants.layouts

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.scalajs.layout.form.select.{ Select, SelectBasic }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants.TenantsComponentTest
import japgolly.scalajs.react.test.SimEvent
import org.scalajs.dom.raw.{ HTMLButtonElement, HTMLElement, HTMLInputElement }
import teststate.dsl.Dsl
import teststate.Exports._

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Future
import scala.util.Try


object GenericTenantsComponentLayoutTest
    extends GenericTenantsComponentLayoutTest

trait GenericTenantsComponentLayoutTest
  extends TenantsComponentTest {

    // Can't consistently test mui select element -- need to override with simple select
    override lazy val tenantOntologyComponentLayout : TenantOntologyComponentLayout =
        new GenericTenantOntologyComponentLayout {
            override lazy val SelectComponent : Select[ String, _ ] = new SelectBasic[ String ]
        }

    class Observation( ele : HTMLElement ) extends ObsType( ele ) {
        import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts.GenericDartTenantsLayoutClasses._

        def tenantEle( tenant : String ) : Option[ HTMLElement ] = {
            ele.querySelectorAll( s".$tenantClass" )
              .vector
              .find { ( node ) =>
                  Try(
                      node.asInstanceOf[ HTMLElement]
                        .querySelector( s".$tenantNameClass" )
                        .textContent
                        .trim
                  ).toOption.contains( tenant )
              }
              .map( _.asInstanceOf[ HTMLElement ] )
        }

        override def getTenants : Seq[ String ] = {
            ele.querySelectorAll( s".$tenantClass" )
              .vector
              .map(
                  _.asInstanceOf[ HTMLElement]
                    .querySelector( s".$tenantNameClass" )
                    .textContent
              )
        }

        override def tenantExists( tenantId : String ) : Boolean =
            ele.querySelectorAll( s".$tenantClass" )
              .vector
              .exists( _.textContent.trim == tenantId )

        override def addTenant( tenant : String ) : Unit = {
            ???
        }

        def openTenantInput() : Unit = {
            ele.querySelector( s".$addTenantButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        def changeNewTenantInput( newInput : String ) : Unit = {
            val inputEle = ele.querySelector( s".$newTenantInputClass" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Change( newInput ) simulate inputEle
        }

        def addNewTenant() : Unit = {
            ele.querySelector( s".$addTenantInputButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        override def removeTenant( tenant : String ) : Unit = {
            tenantEle( tenant )
              .foreach { ele =>
                  Try(
                      ele.querySelector( s".$removeTenantButtonClass" )
                        .asInstanceOf[ HTMLButtonElement ]
                        .click()
                  )
              }
        }

        override def refresh( ) : Unit =
            ele.querySelector( s".$refreshButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
    }

    override def addTenant( tenant : String ) : dsl.Actions = {
        dsl.action( "Open new tenant input" )( v => Future( v.obs.openTenantInput() ) )
          .>>( dsl.action( s"Input new tenant: $tenant" )( v => Future( v.obs.changeNewTenantInput( tenant ) ) ) )
          .>>( dsl.action( "Click add tenant button" )( v => Future( v.obs.addNewTenant() ) ) )
    }


    override type Obs = Observation
    override def genObs( ele : HTMLElement ) : Observation = new Observation( ele )
    override lazy val dsl : Dsl[ Future, Unit, Observation, Unit, String ] = Dsl.full[ Future, Unit, Observation, Unit, String ]
    override lazy val testRenderContext : Unit = ()

}
