package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.cluster.layouts.wm.DartClusterCuratorClusterLayoutClasses._
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorClusterTest
import japgolly.scalajs.react.test.SimEvent
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLElement, HTMLInputElement, MouseEventInit}
import org.scalajs.dom.{Element, MouseEvent}
import teststate.dsl.Dsl

import scala.concurrent.Future
import scala.scalajs.js

trait WmDartClusterCuratorClusterLayoutTestStateConfig
  extends DartClusterCuratorClusterTest {

    class Observation( ele : HTMLElement ) extends ObsType( ele ) {

        override def getRecommendedName : String = ele.querySelector( s".${clusterNameClass}" ).textContent

        def getRecommendedPhrasesElements : Vector[ Element ] =
            ele.querySelectorAll( s".$recommendedPhrasesClass" )
              .vector
              .map( _.asInstanceOf[ Element ] )

        def getAcceptedPhrasesElements : Vector[ Element ] =
            ele.querySelectorAll( s".$acceptedPhrasesClass" )
              .vector
              .map( _.asInstanceOf[ Element ] )

        def getRejectedPhrasesElements : Vector[ Element ] =
            ele.querySelectorAll( s".$rejectedPhrasesClass" )
              .vector
              .map( _.asInstanceOf[ Element ] )

        override def getRecommendedPhrases : Set[ String ] =
            getRecommendedPhrasesElements
              .map( _.querySelector( s".$recommendedPhrasePhraseTextClass" ).textContent.trim )
              .toSet

        override def getAcceptedPhrases : Set[ String ] =
            getAcceptedPhrasesElements
              .map( _.querySelector( s".$recommendedPhrasePhraseTextClass" ).textContent.trim )
              .toSet

        override def getRejectedPhrases : Set[ String ] =
            getRejectedPhrasesElements
              .map( _.querySelector( s".$recommendedPhrasePhraseTextClass" ).textContent.trim )
              .toSet

        override def getIsRejected : Boolean = {
            ele.querySelector( s".${rejectClusterButtonClass}" )
              .querySelector( "input" )
              .asInstanceOf[ HTMLInputElement ]
              .checked
        }

        override def setClusterToCurate() : Unit = {
            if ( getIsRejected ) {
                val curateBox = ele
                  .querySelector( s".${curateClusterButtonClass}" )
                  .querySelector( "input" )
                  .asInstanceOf[ HTMLInputElement ]
                SimEvent.Change( "restore", true ) simulate curateBox
            }
        }
        override def setClusterToRejected() : Unit = {
            if ( !getIsRejected ) {
                val rejectBox : HTMLInputElement = ele
                  .querySelector( s".${rejectClusterButtonClass}" )
                  .querySelector( "input" )
                  .asInstanceOf[ HTMLInputElement ]
                SimEvent.Change( "reject", true ) simulate rejectBox
            }
        }

        def getRecommendedPhraseElement( index : Int ) : Element =
            ele.querySelectorAll( s".$recommendedPhrasesClass" )
              .vector( index )
              .asInstanceOf[ Element ]

        def getRecommendedPhraseElement( phrase : String ) : Element =
            ele.querySelectorAll( s".$recommendedPhrasesClass" )
              .vector
              .map( _.asInstanceOf[ Element ] )
              .find( _.querySelector( s".$recommendedPhrasePhraseTextClass" ).textContent == phrase )
              .get

        override def acceptPhrase( phrase : String ) : Unit =
            getRecommendedPhraseElement( phrase )
              .querySelector( s".$recommendedPhraseAcceptButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def rejectPhrase( phrase : String ) : Unit =
            getRecommendedPhraseElement( phrase )
              .querySelector( s".$recommendedPhraseRejectButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()


        override def usePhraseAsName( phrase : String ) : Unit =
            getRecommendedPhraseElement( phrase )
              .querySelector( s".$recommendedPhraseUseNameButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def restorePhrase( phrase : String ) : Unit =
            getRecommendedPhraseElement( phrase )
              .querySelector( s".$recommendedPhraseRestoreButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click

        override def getSelectedPhrases : Set[ String ] =
            ele.querySelectorAll( s".$selectedPhrasesClass" )
              .vector
              .map(
                  _.asInstanceOf[ HTMLElement ]
                    .querySelector( s".${recommendedPhrasePhraseTextClass}" )
                    .textContent
              )
              .toSet

        def getSelectedPhrasesElement( phrase : String ) : Element =
            ele.querySelectorAll( s".$selectedPhrasesClass" )
              .vector
              .find( _.textContent == phrase )
              .get
              .asInstanceOf[ Element ]

        override def selectPhrases( phrases : Iterable[ String ] ) : Unit = {
            val phraseSet = phrases.toSet

            ele.querySelectorAll( s".$recommendedPhrasePhraseTextClass" )
              .vector
              .filter( el => phraseSet.contains( el.textContent ) )
              .foreach( el => {
                  val init = ( new js.Object ).asInstanceOf[ MouseEventInit ]
                  init.bubbles = true
                  init.metaKey = true
                  val evt = new MouseEvent( "mousedown", init )
                  el.dispatchEvent( evt )
              } )
        }

        override def selectAllPhrases( ) : Unit =
            ele.querySelector( s".$selectAllCheckBoxClass" )
              .querySelector( "input" )
              .asInstanceOf[ HTMLInputElement ]
              .click()

        override def selectAcceptedPhrases( ) : Unit =
            ele.querySelector( s".$selectAcceptedCheckBoxClass" )
              .querySelector( "input" )
              .asInstanceOf[ HTMLInputElement ]
              .click()

        override def selectRejectedPhrases( ) : Unit =
            ele.querySelector( s".$selectRejectedCheckBoxClass" )
              .querySelector( "input" )
              .asInstanceOf[ HTMLInputElement ]
              .click()

        override def selectUncuratedPhrases( ) : Unit =
            ele.querySelector( s".$selectUncuratedCheckBoxClass" )
              .querySelector( "input" )
              .asInstanceOf[ HTMLInputElement ]
              .click()

        override def clearSelection( ) : Unit =
            ele.querySelector( s".$clearSelectionButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def acceptSelectedPhrases( ) : Unit =
            ele.querySelector( s".$selectedPhrasesAcceptButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def rejectSelectedPhrases( ) : Unit =
            ele.querySelector( s".$selectedPhrasesRejectButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def restoreSelectedPhrases( ) : Unit =
            ele.querySelector( s".$selectedPhrasesRestoreButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def getRecommendedConcepts : Set[ Vector[ String ] ] =
            ele.querySelectorAll( s".$recommendedConceptsClass" )
              .vector
              .map( _.textContent.split( "/" ).map( _.trim ).filter( _.nonEmpty ).toVector )
              .toSet

        def getRecommendedConceptElement( path : Vector[ String ] ) : Element =
            ele.querySelectorAll( s".$recommendedConceptsClass" )
              .vector
              .find( _.textContent.split( "/" ).filter( _.trim.nonEmpty ).toVector == path )
              .get
              .asInstanceOf[ Element ]

        override def getConceptSearchInput : String = {
            ele.querySelector( s".$conceptSearchInputClass" )
              .asInstanceOf[ HTMLInputElement ]
              .value
        }

        override def getSearchedConcepts : Option[ Set[ Vector[ String ] ] ] =
            if ( ele.querySelectorAll( s".$conceptSearchResultsWrapperClass" )
              .vector.isEmpty ) None
            else Some(
                ele.querySelectorAll( s".$conceptSearchResultsClass" )
                  .vector
                  .map( _.textContent.split( "/" ).filter( _.trim.nonEmpty ).toVector )
                  .toSet
            )

        def getSearchedConceptEle( path : Vector[ String ] ) : Element =
            ele.querySelectorAll( s".$conceptSearchResultsClass" )
              .vector
              .find( _.textContent.split( "/" ).filter( _.trim.nonEmpty ).toVector == path )
              .get
              .asInstanceOf[ Element ]

        override def getTargetConcept : Option[ Vector[ String ] ] =
            ele.querySelectorAll( s".$conceptTargetClass" )
              .vector
              .headOption
              .map(
                  _.textContent
                    .split( "/" )
                    .map( _.trim )
                    .filter( _.nonEmpty )
                    .toVector
              )


        def getAcceptedPhraseTargetElement( phrase : String ) : Element =
            ele.querySelectorAll( s".$acceptedPhrasesClass" )
              .vector
              .find( _.asInstanceOf[ HTMLElement ].querySelector( s".${recommendedPhrasePhraseTextClass}" ).textContent.trim == phrase )
              .get.asInstanceOf[ Element ]
              .querySelector( s".$acceptedPhraseTargetClass" )

        override def getAcceptedPhraseTarget( phrase : String ) : Seq[ String ] =
            getAcceptedPhraseTargetElement( phrase )
              .textContent
              .split( "/" )
              .filter( _.trim.nonEmpty )

        override def setCustomRecommendedName( name : String ) : Unit = ???

        override def targetRecommendedConcept( value : Vector[ String ] ) : Unit =
            getRecommendedConceptElement( value )
//              .querySelector( s".$recommendedConceptTargetButton" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def viewRecommendedConcept( value : Vector[ String ] ) : Unit =
            getRecommendedConceptElement( value )
              .querySelector( s".$recommendedConceptViewButton" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def updateConceptSearchInput( value : String ) : Unit = {
            val inputEle = ele.querySelector( s".$conceptSearchInputClass" )
              .asInstanceOf[ HTMLInputElement ]
            inputEle.focus()
            SimEvent.Change( value ) simulate inputEle
        }

        override def exitConceptSearch( ) : Unit = ele.click()

        override def usePhraseForConceptSearch( phrase : String ) : Unit =
            getRecommendedPhraseElement( phrase )
              .querySelector( s".$recommendedPhraseUseForConceptSearchButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click

        override def targetSearchedConcept( value : Vector[ String ] ) : Unit = {
            getSearchedConceptEle( value )
//              .querySelector( s".$conceptSearchResultTargetButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .firstElementChild
              .dispatchEvent( {
                  val init = ( new js.Object ).asInstanceOf[ MouseEventInit ]
                  init.bubbles = true
                  new MouseEvent( "mousedown", init )
              } )
        }

        override def viewSearchedConcept( value : Vector[ String ] ) : Unit =
            getSearchedConceptEle( value )
              .querySelector( s".$conceptSearchResultTargetButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def targetAcceptedPhraseTarget( value : String ) : Unit =
            getAcceptedPhraseTargetElement( value )
              .querySelector( s".$acceptedPhraseTargetTargetButton" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def viewAcceptedPhraseTarget( value : String ) : Unit =
            getAcceptedPhraseTargetElement( value )
              .querySelector( s".$acceptedPhraseTargetViewButton" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()

        override def addDefaultTarget( ) : Unit = {
            ele.querySelector( s".$addDefaultTargetButtonClass" )
              .asInstanceOf[ HTMLButtonElement ]
              .click()
        }
    }
    override type Obs = Observation

    import scalajs.concurrent.JSExecutionContext.Implicits.queue

    override lazy val dsl : Dsl[ Future, Unit, Observation, State, String ] =
        Dsl.full[ Future, Unit, Observation, State, String ]

    override def genObs(
        ele : HTMLElement,
    ) : Observation = new Observation( ele )

}

object WmDartClusterCuratorClusterLayoutTest
  extends WmDartClusterCuratorClusterLayoutTestStateConfig

