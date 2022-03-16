package com.twosixtech.dart.scalajs.layout.styles

import japgolly.scalajs.react.vdom.TagMod
import scalacss.defaults.Exports

import scala.language.implicitConversions

object ClassNameConversions {

    implicit def stringToCombinableClass( className : String ) : ClassNamesLike =
        if ( className.isEmpty ) new ClassNamesLike( Nil )
        else new ClassNamesLike( Seq( className ) )

    implicit def optionToCombinableClass( classNameOption : Option[ String ] ) : ClassNamesLike =
        if ( classNameOption.exists( _.isEmpty ) ) new ClassNamesLike( Nil )
        else new ClassNamesLike( classNameOption.toSeq )

    implicit def styleToCombinableClass( style : Exports#StyleA ) : ClassNamesLike = {
        stringToCombinableClass( style.htmlClass )
    }

    class ClassNamesLike( private[styles] val classNamesInput : Seq[ String ] ) {
        def and( other : ClassNamesLike ) : ClassNamesLike =
            new ClassNamesLike( classNamesInput ++ other.classNamesInput )

        def overriddenBy( overrideClass: ClassNamesLike ) : ClassNamesLike = {
            if ( overrideClass.classes.isEmpty ) this
            else overrideClass
        }

        def getClasses( cls : String ) : Seq[ String ] = {
            val trimmedClassName = cls.trim
            if ( trimmedClassName.isEmpty ) Seq.empty
            else {
                trimmedClassName
                  .split( "\\s+" )
                  .toList match {
                    case res@( _ :: Nil ) => res
                    case other => other.flatMap( getClasses )
                }
            }
        }

        lazy val classes : Seq[ String ] = classNamesInput.flatMap( getClasses ).distinct

        lazy val classString : String = classes.mkString( " " )
        def cName : ClassNamesLike = this : ClassNamesLike
    }

    object ClassNamesLike {
        implicit def classNameLikeToString( classNameLike : ClassNamesLike ) : String = classNameLike.classString
        implicit def classNameLikeToOption( classNameLike : ClassNamesLike ) : Option[ String ] = {
            val str = classNameLike.classString
            if ( str.isEmpty ) None
            else Some( str )
        }
        implicit def classNameLikeToTagMod( ClassNameLike : ClassNamesLike ) : TagMod = {
            import japgolly.scalajs.react.vdom.html_<^._
            ^.className := classNameLikeToString( ClassNameLike )
        }
    }

}
