package com.twosixtech.dart.scalajs.dom

import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.{Node, NodeList, document}
import org.scalajs.dom.html.Div

object DomUtils {
    def targetDiv( divId : String, className : Option[ String ] = None ) : Div = {
        import scalatags.JsDom.all._

        val queryResult = document.querySelector( s"#$divId" )
        queryResult match {
            case elem : Div => elem
            case _ =>
                val targetEle : Div = div(
                    id := divId,
                    className match {
                        case Some( cn ) => `class` := cn
                        case None =>
                    },
                ).render
                document.body.appendChild( targetEle )
                targetEle
        }
    }

    implicit class NodeListExtensions( nl : NodeList ) {
        private lazy val pnl = new PimpedNodeList( nl )
        lazy val vector : Vector[ Node ] = pnl.toVector
    }
}
