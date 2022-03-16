//package com.twosixtech.dart.scalajs.layout.css.palette
//
//import com.twosixtech.dart.scalajs.react.JsReactComponent.ScalaProps
//import com.twosixtech.dart.scalajs.react.{ReactComponent, ReactFacadeNoChildren, SimpleReactComponent}
//import japgolly.scalajs.react.component.Scala.Unmounted
//import japgolly.scalajs.react.raw.React
//import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
//
//import scala.scalajs.js
//
//class DartStylesPaletteFacadeWrapper(
//    override val rawComponent : js.Object,
//) extends ReactFacadeNoChildren[ DartStylesPalette.JsProps, DartStylesPalette.Props ]
//
//class DartStylesPaletteFacade(
//    rawComponent : js.Object,
//) extends DartStylesPalette[ Unit ]
//    with SimpleReactComponent[ DartStylesPalette.Props ] {
//
//    val wrapper = new DartStylesPaletteFacadeWrapper( rawComponent )
//
//    override protected def render( props : DartStylesPalette.Props ) : VdomElement = {
//        wrapper( props )
//    }
//}
//
//trait DartStylesPalette[ State ] extends ReactComponent[ DartStylesPalette.Props, State ]
//
//object DartStylesPalette {
//
//    @js.native
//    trait JsClasses extends js.Object {
//        var fullHeight : String = js.native
//        var fullWidth : String = js.native
//        var thinPadding : String = js.native
//        var medPadding : String = js.native
//        var thickPadding : String = js.native
//        var thinMargin : String = js.native
//        var medMargin : String = js.native
//        var thickMargin : String = js.native
//    }
//
//    @js.native
//    trait JsProps extends js.Object {
//        var render : js.Function1[ JsClasses, React.Node ]
//    }
//
//    case class Classes(
//        fullHeight : String,
//        fullWidth : String,
//        thinPadding : String,
//        medPadding : String,
//        thickPadding : String,
//        thinMargin : String,
//        medMargin : String,
//        thickMargin : String,
//    )
//
//    case class Props(
//        render : Classes => VdomElement,
//    ) extends ScalaProps[ JsProps ] {
//        override def toJs : JsProps = {
//            val jp = ( new js.Object ).asInstanceOf[ JsProps ]
//            jp.render = { (classes : JsClasses) =>
//                render(
//                    Classes(
//                        classes.fullHeight,
//                        classes.fullWidth,
//                        classes.thinPadding,
//                        classes.medPadding,
//                        classes.thickPadding,
//                        classes.thinMargin,
//                        classes.medMargin,
//                        classes.thickMargin,
//                    )
//                ).rawNode
//            }
//            jp
//        }
//    }
//
//}