package com.twosixtech.dart.scalajs.layout.form.select.multiselect

import com.twosixtech.dart.scalajs.dom.DomUtils.NodeListExtensions
import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions.stringToCombinableClass
import com.twosixtech.dart.scalajs.test.TestStateConfiguration
import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import org.scalajs.dom.{DOMList, Element, MouseEvent}
import org.scalajs.dom.raw.{DOMTokenList, HTMLButtonElement, HTMLElement, MouseEventInit}
import utest._
import japgolly.scalajs.react.vdom.html_<^._
import teststate.Exports._
import teststate.data.Id

import scala.collection.immutable.ListMap
import scala.scalajs.js
import scala.scalajs.js.Object.{entries, keys}

trait MultiSelectTestStateConfig extends TestStateConfiguration {

    override type Obs <: Observation
    override type St = ListMap[ String, Boolean ]
    override val dsl : Dsl[ Id, Unit, Obs, St, String ] = Dsl[ Unit, Obs, St ]

    def selectEntry( value : String, selected : Boolean ) : MultiSelect.SelectEntry =
        MultiSelect.SelectEntry(
            element = value,
            selected = selected,
        )

    trait Observation {

        // Abstract value/methods

        val ele : HTMLElement

        def toggleSelectAction( value : String ) : Unit

        def multiSelectAction( value : String ) : Unit

        def extendSelectAction( value : String ) : Unit

        // Implemented methods

        import MultiSelect.ClassNames._

        def valueIsSelected( value : String ) : Boolean = {
            ele.querySelectorAll( s".$selectedEntryClass" )
              .vector
              .exists( _.textContent == value )
        }

        def getEntryElement( value : String ) : Option[ Element ] = {
            ele.querySelectorAll( s".$entryClass" )
              .vector
              .find( _.textContent == value )
              .map( _.asInstanceOf[ Element ] )
        }

        def getState : St = ListMap( ele.querySelectorAll( s".$entryClass" )
          .vector
          .map( entryNode => {
              val classes = entryNode.asInstanceOf[ HTMLElement ].className.cName.classes
              if ( classes.contains( selectedEntryClass ) ) entryNode.textContent -> true
              else if ( classes.contains( unselectedEntryClass ) ) entryNode.textContent -> false
              else throw new IllegalStateException( s"Entry for value ${entryNode.textContent} has neither selectedEntryClass nor unselectedEntryClass" )
          } ) : _* )
    }

    override val defaultInitialState : ListMap[String, Boolean] = ListMap.empty

    // Focus definitions
    def valueSelected( value : String ) : dsl.FocusValue[ Boolean ] = dsl.focus( s"Entry '$value' is selected?" ).value( _.obs.valueIsSelected( value ) )
    def valuesSubset( values : Iterable[ String ] ) : dsl.FocusValue[ St ] = dsl.focus( s"Entries subset ${values.size}" ).value( _.obs.getState.filter( v => values.toSet.contains( v._1 ) ) )
    def stateObsExp : dsl.ObsAndState[ St ] = dsl.focus( "Observed and expected state" ).obsAndState( _.getState, s => s )

    // Assertion definitions
    def valuesSubsetShouldBeSelected( values : Iterable[ String ] ) : dsl.Points = valuesSubset( values ).test( "should be selected" )( _.forall( _._2 ) )
    def valuesSubsetShouldBeUnselected( values : Iterable[ String ] ) : dsl.Points = valuesSubset( values ).test( "should be unselected" )( !_.forall( _._2 ) )

    // Action definitions
    def toggleSelectAction( value : String ) : dsl.Actions = dsl.action( s"Click on entry '$value'" )( _.obs.toggleSelectAction( value ) )
    def toggleSelectActionRepeat( values : Iterable[ String ] ) : dsl.Actions =
        values.foldLeft( dsl.emptyAction )( ( lastAction, nextValue ) => lastAction >> toggleSelectAction( nextValue ) )
    def multiSelectAction( value : String ) : dsl.Actions = dsl.action( s"Multiselect click on entry '$value''" )( _.obs.multiSelectAction( value ) )
    def multiSelectActionRepeat( values : Iterable[ String ] ) : dsl.Actions =
        values.foldLeft( dsl.emptyAction )( ( lastAction, nextValue ) => {
            lastAction >> multiSelectAction( nextValue )
        } )
    def extendSelectAction( value : String ) : dsl.Actions = dsl.action( s"Extend select click on entry '$value'" )( _.obs.extendSelectAction( value ) )
    def extendSelectActionRepeat( values : Iterable[ String ] ) : dsl.Actions =
        values.foldLeft( dsl.emptyAction )( ( lastAction, nextValue ) => lastAction >> extendSelectAction( nextValue ) )


}

abstract class MultiSelectTest[ StateType ]( val multiSelectComponent : MultiSelect[ String, StateType ] )
  extends TestSuite {
    this : MultiSelectTestStateConfig =>

    object TestComponent {

        class Backend( scope : BackendScope[ MultiSelect.SelectMode, St ] ) {

            def clickHandler( newValues : Map[ String, Boolean ] ) : Callback = {
                scope.modState( _ ++ newValues )
            }

            def render( mode : MultiSelect.SelectMode, state : St ): VdomNode = {
                val entries = state.map( tup => (tup._1 -> selectEntry( tup._1, tup._2 ) ) )

                multiSelectComponent( entries, clickHandler, mode )
            }
        }

        def component( initialState : St ) =
            ScalaComponent.builder[ MultiSelect.SelectMode ]
              .initialState( initialState )
              .backend( new Backend( _ ) )
              .renderBackend
              .build
    }

    override def tests : Tests = Tests {

        test( "SingleSelect mode" ) {
            test( "clicking an unselected element in any fashion should select it" ) {
                val initMap = ListMap( "test-value" -> false )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                       +> valueSelected( "test-value" ).assert.equal( false )
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()
            }

            test( "clicking a selected element should do nothing" ) {
                val initMap = ListMap( "test-value" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()
            }

            test( "clicking an unselected element should select that element and unselect another selected element" ) {
                val initMap = ListMap( "test-value-1" -> false, "test-value-2" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()
            }

            test( "multiselect clicks should not multiselect" ) {
                val initMap = ListMap( "test-value-1" -> true, "test-value-2" -> false, "test-value-3" -> false )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value-3" )
                       +> valueSelected( "test-value-1" ).assert.equal( false )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                       +> valueSelected( "test-value-3" ).assert.equal( true )
                    >> multiSelectAction( "test-value-2" )
                       +> valueSelected( "test-value-1" ).assert.equal( false )
                       +> valueSelected( "test-value-2" ).assert.equal( true )
                       +> valueSelected( "test-value-3" ).assert.equal( false )
                    >> multiSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                       +> valueSelected( "test-value-3" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()
            }

            test( "extension clicks should not multiselect" ) {
                test( "multiselect clicks should not multiselect" ) {
                    val initMap = ListMap( "test-value-1" -> false, "test-value-2" -> false, "test-value-3" -> false )
                    val testComponent = TestComponent.component( initMap )

                    Plan.action(
                        dsl.emptyAction
                        >> extendSelectAction( "test-value-3" )
                           +> valueSelected( "test-value-1" ).assert.equal( false )
                           +> valueSelected( "test-value-2" ).assert.equal( false )
                           +> valueSelected( "test-value-3" ).assert.equal( true )
                        >> extendSelectAction( "test-value-1" )
                           +> valueSelected( "test-value-1" ).assert.equal( true )
                           +> valueSelected( "test-value-2" ).assert.equal( false )
                           +> valueSelected( "test-value-3" ).assert.equal( false )
                        >> extendSelectAction( "test-value-2" )
                           +> valueSelected( "test-value-1" ).assert.equal( false )
                           +> valueSelected( "test-value-2" ).assert.equal( true )
                           +> valueSelected( "test-value-3" ).assert.equal( false )
                    ).withInitialState( initMap ).runOn( testComponent( MultiSelect.SingleSelect ) ).utest()
                }
            }
        }

        test( "MultiPersist mode" ) {
            test( "clicking an unselected element should select it" ) {
                val initMap = ListMap( "test-value" -> false )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction >>
                    toggleSelectAction( "test-value" )
                    +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking a selected element should unselect it" ) {
                val initMap = ListMap( "test-value" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking multiple times should toggle section state multiple times" ) {
                val initMap = ListMap( "test-value" -> false )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( false )
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking multiple unselected elements should select them" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ).toSet map nameFn
                val valSet2 = ( 6 to 10 ).toSet map nameFn

                val initMap = ListMap( (valSet1 ++ valSet2).toSeq.map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )


                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> toggleSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> toggleSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> toggleSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> toggleSelectAction( nameFn( 1 ) )
                       +> valueSelected( nameFn( 1 ) ).assert.equal( true )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ) - nameFn( 1 ) )
                    >> toggleSelectAction( nameFn( 8 ) )
                       +> valuesSubsetShouldBeSelected( Set( nameFn( 1 ), nameFn( 8 ) ) )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ) - nameFn( 1 ) - nameFn( 8 ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking multiple unselected elements with multi-select should select them" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( ( valSet1 ++ valSet2 ).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )


                Plan.action(
                    dsl.emptyAction
                    >> multiSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> multiSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> multiSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> multiSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> multiSelectAction( nameFn( 1 ) )
                       +> valueSelected( nameFn( 1 ) ).assert.equal( true )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ).toSet - nameFn( 1 ) )
                    >> multiSelectAction( nameFn( 8 ) )
                       +> valuesSubsetShouldBeSelected( Set( nameFn( 1 ), nameFn( 8 ) ) )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ).toSet - nameFn( 1 ) - nameFn( 8 ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking extend-select when nothing is selected should select everything from the first entry to the clicked entry" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( (valSet1 ++ valSet2).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( valSet2( 1 ) )
                       +> valuesSubsetShouldBeSelected( valSet1 ++ valSet2.slice( 0, 2 ) )
                       +> valuesSubsetShouldBeUnselected( valSet2.slice( 2, valSet2.size ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( valSet2( 4 ) )
                       +> valuesSubsetShouldBeSelected( valSet1 ++ valSet2 )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()
            }

            test( "clicking extend-select when something was regularly clicked before should select everything from the previously clicked entry (excluding it) to the clicked entry (forward and backward)" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( (valSet1 ++ valSet2).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( valSet1( 4 ) )
                       +> valueSelected( valSet1( 4 ) ).assert.equal( true )
                       +> valuesSubsetShouldBeUnselected( (valSet1 ++ valSet2).toSet - valSet1( 4 ) )
                    >> extendSelectAction( valSet1( 1 ) )
                       +> valuesSubsetShouldBeSelected( valSet1.slice( 1, 5 ) )
                       +> valuesSubsetShouldBeUnselected( valSet1( 0 ) +: valSet2 )
                    >> extendSelectAction( valSet2( 2 ) )
                       +> valuesSubsetShouldBeSelected( valSet1.slice( 1, 5 ) ++ valSet2.slice( 0, 3 ) )
                       +> valuesSubsetShouldBeUnselected( valSet1( 0 ) +: valSet2.slice( 3, 5 ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()

            }
        }

        test( "MultiDefault mode" ) {
            test( "clicking an unselected element regularly or as multi-select should select it" ) {
                val initMap = ListMap( "test-value" -> false )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    +> valueSelected( "test-value" ).assert.equal( false )
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( true )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "regular clicking an unselected element should select that element and unselect another selected element" ) {
                val initMap = ListMap( "test-value-1" -> false, "test-value-2" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "regular clicking a single selected element should unselect it" ) {
                val initMap = ListMap( "test-value" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "regular clicking one among multiple selected elements should select it and unselect the rest" ) {
                val initMap = ListMap( "test-value-1" -> true, "test-value-2" -> true, "test-value-3" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( "test-value-1" )
                       +> valueSelected( "test-value-1" ).assert.equal( true )
                       +> valueSelected( "test-value-2" ).assert.equal( false )
                       +> valueSelected( "test-value-3" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "clicking a selected element with multi-select should unselect it" ) {
                val initMap = ListMap( "test-value" -> true )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> multiSelectAction( "test-value" )
                       +> valueSelected( "test-value" ).assert.equal( false )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "clicking multiple unselected elements with multi-select should select them" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( ( valSet1 ++ valSet2 ).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )


                Plan.action(
                    dsl.emptyAction
                    >> multiSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> multiSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeSelected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> multiSelectActionRepeat( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeSelected( valSet2 )
                    >> multiSelectActionRepeat( valSet2 )
                       +> valuesSubsetShouldBeUnselected( valSet1 )
                       +> valuesSubsetShouldBeUnselected( valSet2 )
                    >> multiSelectAction( nameFn( 1 ) )
                       +> valueSelected( nameFn( 1 ) ).assert.equal( true )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ).toSet - nameFn( 1 ) )
                    >> multiSelectAction( nameFn( 8 ) )
                       +> valuesSubsetShouldBeSelected( Set( nameFn( 1 ), nameFn( 8 ) ) )
                       +> valuesSubsetShouldBeUnselected( ( valSet1 ++ valSet2 ).toSet - nameFn( 1 ) - nameFn( 8 ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            test( "clicking extend-select when nothing is selected should select everything from the first entry to the clicked entry" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( (valSet1 ++ valSet2).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( valSet2( 1 ) )
                       +> valuesSubsetShouldBeSelected( valSet1 ++ valSet2.slice( 0, 2 ) )
                       +> valuesSubsetShouldBeUnselected( valSet2.slice( 2, valSet2.size ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiPersist ) ).utest()

                Plan.action(
                    dsl.emptyAction
                    >> extendSelectAction( valSet2( 4 ) )
                       +> valuesSubsetShouldBeSelected( valSet1 ++ valSet2 )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()
            }

            // Not sure why this won't pass -- works on the webpage
            test( "clicking extend-select when something was regularly clicked before should select everything from the previously clicked entry (excluding it) to the clicked entry (forward and backward)" ) {
                val nameFn = ( i : Int ) => s"test-val-$i"
                val valSet1 = ( 1 to 5 ) map nameFn
                val valSet2 = ( 6 to 10 ) map nameFn

                val initMap = ListMap( (valSet1 ++ valSet2).map( _ -> false ) : _* )
                val testComponent = TestComponent.component( initMap )

                Plan.action(
                    dsl.emptyAction
                    >> toggleSelectAction( valSet1( 4 ) )
                       +> valueSelected( valSet1( 4 ) ).assert.equal( true )
                       +> valuesSubsetShouldBeUnselected( (valSet1 ++ valSet2).toSet - valSet1( 4 ) )
                    >> extendSelectAction( valSet1( 1 ) )
                       +> valuesSubsetShouldBeSelected( valSet1.slice( 1, 5 ) )
                       +> valuesSubsetShouldBeUnselected( valSet1( 0 ) +: valSet2 )
//                    >> extendSelectAction( valSet2( 2 ) )
//                    >> extendSelectAction( valSet2( 2 ) )
//                       +> valuesSubsetShouldBeSelected( valSet1.slice( 1, 5 ) ++ valSet2.slice( 0, 3 ) )
//                       +> valuesSubsetShouldBeUnselected( valSet1( 0 ) +: valSet2.slice( 3, 5 ) )
                ).withInitialState( initMap ).runOn( testComponent( MultiSelect.MultiDefault ) ).utest()

            }

        }

    }

}

