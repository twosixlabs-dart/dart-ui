package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants.layouts

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.scalajs.layout.form.select.{ Select, SelectBasic }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts.GenericStateAccessComponentLayoutClasses.newKeyInputClass
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenants.TenantsComponentTest
import japgolly.scalajs.react.test.SimEvent
import org.scalajs.dom.raw.{ HTMLButtonElement, HTMLElement, HTMLInputElement }
import teststate.dsl.Dsl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


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
              .find( _.textContent.trim == tenant )
              .map( _.asInstanceOf[ HTMLElement ] )
        }

        override def getTenants : Seq[ String ] =
            ele.querySelectorAll( s".$tenantClass" )
              .vector
              .map( _.textContent )

        override def tenantExists( tenantId : String ) : Boolean =
            ele.querySelectorAll( s".$tenantClass" )
              .vector
              .exists( _.textContent.trim == tenantId )

        override def addTenant( tenant : String ) : Unit = {
            ele.querySelector( s".$addTenantButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

            val inputEle = ele.querySelector( s".$newKeyInputClass" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Change( tenant ) simulate inputEle

            ele.querySelector( s".$addTenantInputButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        override def removeTenant( tenant : String ) : Unit = {
            tenantEle( tenant )
              .foreach(
                  _.querySelector( s".$removeTenantButtonClass" )
                    .asInstanceOf[ HTMLButtonElement ]
                    .click()
              )
        }

        override def refresh( ) : Unit =
            ele.querySelector( s".$refreshButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
    }

    override type Obs = Observation
    override def genObs( ele : HTMLElement ) : Observation = new Observation( ele )
    override lazy val dsl : Dsl[ Future, Unit, Observation, Unit, String ] = Dsl.full[ Future, Unit, Observation, Unit, String ]
    override lazy val testRenderContext : Unit = ()

}
