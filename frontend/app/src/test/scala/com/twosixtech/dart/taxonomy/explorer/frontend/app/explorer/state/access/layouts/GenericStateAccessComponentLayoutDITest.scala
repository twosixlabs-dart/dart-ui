package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.layouts

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.state.access.StateAccessComponentTest
import japgolly.scalajs.react.test.SimEvent
import org.scalajs.dom.Node
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLElement, HTMLInputElement}
import teststate.dsl.Dsl

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object GenericStateAccessComponentLayoutTest
  extends GenericStateAccessComponentTestConfig

trait GenericStateAccessComponentTestConfig
  extends StateAccessComponentTest {

    import GenericStateAccessComponentLayoutClasses._

    class Observation( ele : HTMLElement ) extends ObsType( ele ) {
        override def getCurrentStateId : Option[ StateAccessComponent.StateId ] =
            ele.querySelectorAll( s".$selectedKeyComponentClass" )
              .vector.headOption.flatMap { selectedKeyElement : Node =>
                val keyName : String = selectedKeyElement
                  .asInstanceOf[ HTMLElement ]
                  .querySelector( s".$keyTextClass" )
                  .textContent

                val versionsMap = selectedKeyElement
                                    .asInstanceOf[ HTMLElement ]
                                    .querySelectorAll( s".$versionComponentClass" )
                                    .vector
                                    .zipWithIndex

                versionsMap.find( _._1.asInstanceOf[ HTMLElement ].classList.contains( selectedVersionClass ) ) map {
                    case (_, i) => StateAccessComponent.StateId( keyName, versionsMap.length - i )
                }
            }

        override def getKeys : Seq[ String ] = {
            ele.querySelectorAll( s".$keyTextClass" )
              .vector
              .map( _.textContent )
        }

        override def getVersions( key : String ) : Option[ Int ] = {
            ele.querySelectorAll( s".${keyIdentifierClass( key )}" )
              .vector.headOption.map( keyNode => {
                keyNode.asInstanceOf[ HTMLElement ]
                  .querySelectorAll( s".$versionComponentClass" )
                  .vector
                  .length
            } )

        }

        override def getNewKeyText : String = {
            ele.querySelectorAll( s".$newKeyInputClass" )
              .vector.headOption.map( inputNode => {
                inputNode.asInstanceOf[ HTMLInputElement ]
                  .value
            } ).getOrElse( "" )
        }

        override def refresh( ) : Unit = {
            ele.querySelector( s".$refreshButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        override def chooseKey( key : String ) : Unit = {
            ele.querySelector( s".${keyIdentifierClass( key )} .$keyTextClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        override def chooseVersion( key : String, version : Int ) : Unit = {
            ele.querySelector( s".${keyIdentifierClass( key )} .${versionIdentifierClass( version )} .$versionTextClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }

        override def setKeyText( text : String ) : Unit = {
            val inputEle = ele.querySelector( s".$newKeyInputClass" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Change( text ) simulate inputEle
        }

        override def gotoPreviousState( ) : Unit = {
            ele.querySelector( s".$prevVersionButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def gotoNextState( ) : Unit = {
            ele.querySelector( s".$nextVersionButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def gotoOldestState( ) : Unit = {
            ele.querySelector( s".$oldestVersionButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def gotoLatestState( ) : Unit = {
            ele.querySelector( s".$latestVersionButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def saveCurrentState( ) : Unit = {
            ele.querySelector( s".$saveButtonClass" )
              .asInstanceOf[ HTMLElement ]
              .click()
        }

        override def saveCurrentStateToNewKey( ) : Unit = {
            val inputEle = ele.querySelector( s".$newKeyInputClass" )
              .asInstanceOf[ HTMLInputElement ]

            SimEvent.Keyboard( key = "Enter" ) simulateKeyDown inputEle
        }

    }

    override type Obs = Observation
    override def genObs( ele : HTMLElement ) : Observation = new Observation( ele )
    override lazy val dsl : Dsl[ Future, Unit, Observation, Unit, String ] = Dsl.full[ Future, Unit, Observation, Unit, String ]
    override val testRenderContext : StateAccessComponentRenderContext = ()

}

