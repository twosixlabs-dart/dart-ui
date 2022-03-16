package com.twosixtech.dart.scalajs.react

import com.twosixtech.dart.scalajs.dom.DomUtils
import japgolly.scalajs.react.vdom.html_<^.VdomElement
import org.scalajs.dom.html.Div
import org.scalajs.dom.{Element, document}

import java.util.UUID
import scala.concurrent.Future
import scala.util.Random

object ReactTestUtilities {

    def genDivId : String = UUID.randomUUID().toString

    def testOn[ M, Result ]( element : VdomElement )( testFn : Element => Result ) : Result = {
        val eleDiv : Div = DomUtils.targetDiv( s"div-${genDivId}" )
        element.renderIntoDOM( eleDiv )
        val res = testFn( eleDiv.firstElementChild )
        document.body.removeChild( eleDiv )
        res
    }

    def asyncTestOn[ M, Result ]( element : VdomElement )( testFn : Element => Future[ Result ] ) : Future[ Result ] = {
        import scalajs.concurrent.JSExecutionContext.Implicits.queue
        val eleDiv : Div = DomUtils.targetDiv( s"div-${genDivId}" )
        element.renderIntoDOM( eleDiv )
        val res = testFn( eleDiv.firstElementChild )
        res.onComplete( _ => document.body.removeChild( eleDiv ) )
        res
    }

}
