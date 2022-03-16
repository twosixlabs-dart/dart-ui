package com.twosixtech.dart.scalajs.styles

import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions
import utest.{TestSuite, Tests, assert, test}

object ClassNameConversionsTest extends TestSuite {
    override def tests : Tests = Tests {

        test( "ClassNamesLike.classString should combine strings correctly" ) {
            import ClassNameConversions.ClassNamesLike
            val combinedClasses = new ClassNamesLike( Seq( "style", " another-style ", "third-style" ) )
            assert( combinedClasses.classString == "style another-style third-style" )
        }

        test( "ClassNameConversions should lift and combine optional strings correctly" ) {
            import ClassNameConversions._
            val combinedClasses = Some( "style" ) and None and Some( "" ) and Some( "another-style" ) and Some( "    " ) and Some( "third-style" )
            assert( combinedClasses.classString == "style another-style third-style" )
        }

        test( "ClassNameConversions should combine scalajs-css styles correctly" ) {
            import scalacss.DevDefaults._
            object Styles extends StyleSheet.Inline {

                import dsl._

                val firstStyle = style( height( 100.%% ) )
                val secondStyle = style( width( 100.%% ) )
                val thirdStyle = style( display.flex )
            }

            import ClassNameConversions._

            val combinedClasses = Styles.firstStyle and Styles.secondStyle and Styles.thirdStyle
            assert( combinedClasses.classString == s"${Styles.firstStyle.htmlClass} ${Styles.secondStyle.htmlClass} ${Styles.thirdStyle.htmlClass}" )
        }

        test( "ClassNameConversions should combine different kinds of styles correctly" ) {
            import scalacss.DevDefaults._
            object Styles extends StyleSheet.Inline {

                import dsl._

                val firstStyle = style( height( 100.%% ) )
                val secondStyle = style( width( 100.%% ) )
                val thirdStyle = style( display.flex )
            }

            import ClassNameConversions._

            val combinedClasses = " " and Some( "class-a" ) and "" and "class-b " and None and Styles.firstStyle and Some( "" ) and Styles.secondStyle and Some( "  " ) and
                                  Styles.thirdStyle
            assert( combinedClasses.classString == s"class-a class-b ${Styles.firstStyle.htmlClass} ${Styles.secondStyle.htmlClass} ${Styles.thirdStyle.htmlClass}" )
        }

        test( "ClassNameConversions should filter out repeat classes" ) {
            import scalacss.DevDefaults._
            object Styles extends StyleSheet.Inline {

                import dsl._

                val firstStyle = style( height( 100.%% ) )
                val secondStyle = style( width( 100.%% ) )
                val thirdStyle = style( display.flex )
            }

            import ClassNameConversions._

            val combinedClasses = " " and Some( "class-a" ) and "" and "class-b " and None and Styles.firstStyle and Some( "" ) and Styles.secondStyle and Some( "  " ) and Some(
                "  class-b" ) and Styles.thirdStyle
            assert( combinedClasses.classString == s"class-a class-b ${Styles.firstStyle.htmlClass} ${Styles.secondStyle.htmlClass} ${Styles.thirdStyle.htmlClass}" )
        }

        test( "ClassNameConversions should handle internal classnames as well" ) {
            import ClassNameConversions._
            val combinedClasses = "  class-1    class-2   " and "class-3" and "class-2   class-4 " and "class-3    class-5 class-4   class-5"
            assert( combinedClasses.classString == "class-1 class-2 class-3 class-4 class-5" )
        }

        test( "ClassNameConversions implicits should be able to convert class names gracefully" ) {
            import ClassNameConversions._
            val classesString : String = "some-class" and "" and Some( "other-class" )
            assert( classesString == "some-class other-class" )
            val classesOption : Option[ String ] = "some-class" and "" and Some( "other-class" )
            assert( classesOption.contains( "some-class other-class" ) )
            val classesOption2 : Option[ String ] = "   " and None and ""
            assert( classesOption2.isEmpty )
        }

        test( "ClassNamesLike.overriddenBy should overwrite if class name passed to it is defined" ) {
            import ClassNameConversions._
            import scalacss.DevDefaults._
            object Styles extends StyleSheet.Inline {

                import dsl._

                val overrideClassNameStyle = style( height( 100.%% ) )
            }

            val originalClassName = "original-class"
            val overrideClassNameString = "override-class"
            val overrideClassNameOption = Some( "override-class" )

            assert( originalClassName.overriddenBy( overrideClassNameString ).classString == "override-class" )
            assert( originalClassName.overriddenBy( overrideClassNameOption ).classString == "override-class" )
            assert( originalClassName.overriddenBy( Styles.overrideClassNameStyle ).classString == Styles.overrideClassNameStyle.htmlClass )

        }
    }
}
