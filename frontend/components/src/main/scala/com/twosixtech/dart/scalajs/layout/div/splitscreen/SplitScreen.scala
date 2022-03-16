package com.twosixtech.dart.scalajs.layout.div.splitscreen

import com.twosixtech.dart.scalajs.react.{AbstractReactFacade, ReactComponent, StoJ}
import japgolly.scalajs.react.raw
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.all.EmptyVdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

trait SplitScreen[ State ] extends ReactComponent[ SplitScreen.Props, State ]

object SplitScreen {

    @js.native
    trait JsClasses extends js.Object {
        var container : String = js.native
        var left : String = js.native
        var right : String = js.native
    }

    @js.native
    trait JsProps extends js.Object {
        var childLeft : raw.React.Node = js.native
        var childRight : raw.React.Node = js.native
        var divisionType : String = js.native
        var independentScroll : Boolean = js.native
        var className : String = js.native
        var classes : JsClasses = js.native
    }

    sealed trait DivisionType {
        def toJs : String
    }

    case object NarrowLeft extends DivisionType { override def toJs : String = "narrow-left"}
    case object Left extends DivisionType { override def toJs : String = "left"}
    case object Middle extends DivisionType { override def toJs : String = "middle"}
    case object Right extends DivisionType { override def toJs : String = "right"}
    case object NarrowRight extends DivisionType { override def toJs : String = "narrow-right"}

    case class Classes(
        container : Option[ String ] = None,
        left : Option[ String ] = None,
        right : Option[ String ] = None,
    ) {
        def toJs : JsClasses = {
            val jc = ( new js.Object ).asInstanceOf[ JsClasses ]
            container.foreach( jc.container = _ )
            left.foreach( jc.left = _ )
            right.foreach( jc.right = _ )
            jc
        }
    }

    case class Props(
        childLeft : VdomNode,
        childRight : VdomNode,
        divisionType : DivisionType,
        independentScroll : Boolean,
        classes : Classes = Classes(),
    ) extends {
        def toJs : JsProps = {
            val jsProps = ( new js.Object ).asInstanceOf[ JsProps ]
            jsProps.childLeft = childLeft.rawNode
            jsProps.childRight = childRight.rawNode
            jsProps.divisionType = divisionType.toJs
            jsProps.independentScroll = independentScroll
            jsProps.classes = classes.toJs
            jsProps
        }
    }

    object Translation extends StoJ[ Props, JsProps ] {
        override def scalaToJsBuilder(
            props : Props,
            jsProps : JsProps ) : (JsProps, VdomNode) = (props.toJs, EmptyVdom)
    }
}

@js.native
@JSImport( "dart-ui-components", "SplitScreen" )
object SplitScreenRaw extends js.Object

object SplitScreenMui extends AbstractReactFacade( SplitScreenRaw, SplitScreen.Translation )
