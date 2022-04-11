package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.layouts

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.scalajs.layout.form.select.{Select, SelectBasic}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.tenant.ontology.TenantOntologyComponentTest
import japgolly.scalajs.react.test.SimEvent
import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement}
import teststate.dsl.Dsl

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Future


object GenericTenantOntologyComponentLayoutTest
  extends GenericTenantOntologyComponentLayoutTest

trait GenericTenantOntologyComponentLayoutTest
  extends TenantOntologyComponentTest {

    // Can't consistently test mui select element -- need to override with simple select
    override lazy val tenantOntologyComponentLayout : TenantOntologyComponentLayout =
        new GenericTenantOntologyComponentLayout {
            override lazy val SelectComponent : Select[ String, _ ] = new SelectBasic[ String ]
        }

    import GenericTenantOntologyComponentLayoutClasses._

    class Observation( ele : HTMLElement ) extends ObsType( ele ) {
        override def getTenants : Seq[ String ] =
            ele.querySelectorAll( s".$tenantNameClass" )
              .vector
              .map( _.textContent )

        override def getTenantOntologyContents( tenant : String ) : Option[ TOContents ] = {
            Option( ele.querySelector( s".${tenantOntologySectionClass( tenant )}" ) )
              .map { toEle =>
                  val publishEle = toEle.querySelector( s".${tenantOntologyPublishedsClass}" )
                  val stagedEle = toEle.querySelector( s".${tenantOntologyStagedsClass}" )
                  TOContents(
                      Option( publishEle ).nonEmpty,
                      Option( stagedEle ).nonEmpty,
                  )
              }

        }

        override def getPublishedOntologyVersionsCount( tenant : String ) : Option[ Int ] = {
            Option( ele.querySelector( s".${tenantOntologySectionClass( tenant )}" ) )
              .map( _.querySelectorAll( s".${versionItemsClass}" ).length )
        }

        override def getStagedOntologyVersionsCount( tenant : String ) : Option[ Int ] = {
            Option( ele.querySelector( s".${tenantOntologySectionClass( tenant )}" ) )
              .map( _.querySelectorAll( s".${versionItemsClass}" ).length )
        }

        override def importMenuIsVisible : Boolean = {
            Option( ele.querySelector( s".${importModalClass}" ) ).nonEmpty
        }

        override def confirmationMenuIsVisible : Boolean = {
            Option( ele.querySelector( s".${exportModalClass}" ) ).nonEmpty
        }

        override def refresh( ) : Unit = {
            ele.querySelector( s".$refreshButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def importPublishedLatestVersion( tenant : String ) : Unit = {
            val outerEle = ele.querySelector( s".${tenantOntologyPublishedClass( tenant )}" )

            outerEle.querySelector( s".$importLatestButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def importPublishedVersion( tenant : String, version : Int ) : Unit = {
            val selectComponent = ele.querySelector( s".${tenantOntologyPublishedClass( tenant )}" )
              .querySelector( s".${versionDropdown}" )
              .querySelector( "select" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Change( version.toString ) simulate selectComponent
        }

        override def importStagedLatestVersion( tenant : String ) : Unit = {
            val outerEle = ele.querySelector( s".${tenantOntologyStagedClass( tenant )}" )

            outerEle.querySelector( s".$importLatestButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }


        override def importStagedVersion( tenant : String, version : Int ) : Unit = {
            val selectComponent = ele.querySelector( s".${tenantOntologyStagedClass( tenant )}" )
              .querySelector( s".${versionDropdown}" )
              .querySelector( "select" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Change( version.toString ) simulate selectComponent
        }

        override def selectKeepClusterState( ) : Unit = {
            ele.querySelector( s".$selectKeepClusterStateButtonClass")
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def selectClearClusterState( ) : Unit = {
            ele.querySelector( s".$selectClearClusterStateButtonClass")
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def stageTaxonomyTo( tenant : String ) : Unit = {
            ele.querySelector( s".${tenantOntologySectionClass( tenant ) }" )
              .querySelector( s".$stageOntologyButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def publishStagedTaxonomy( tenant : String ) : Unit = {
            ele.querySelector( s".${tenantOntologySectionClass( tenant ) }" )
              .querySelector( s".$publishOntologyButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def confirmStageOrPublish( ) : Unit = {
            val exportModal = ele.querySelector( s".$exportModalClass" )
            val confButton = ele.querySelector( s".$exportConfirmationButton" )

            confButton.asInstanceOf[ HTMLElement ]
              .click()
        }

        override def cancelStageOrPublish( ) : Unit = {
            ele.querySelector( s".$exportCancelButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }
    }

    override type Obs = Observation
    override def genObs( ele : HTMLElement ) : Observation = new Observation( ele )
    override lazy val dsl : Dsl[ Future, Unit, Observation, Unit, String ] = Dsl.full[ Future, Unit, Observation, Unit, String ]
    override lazy val testRenderContext : Unit = ()

}
