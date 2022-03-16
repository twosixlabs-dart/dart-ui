package com.twosixtech.dart.scalajs.dom

import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, document}
import org.scalajs.dom.raw.{Blob, MouseEventInit, Window}

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object DownloadUtils {

    def downloadObject( obj : js.Any, fileName : String ) : Unit = {
        val url = dom.URL.createObjectURL( new Blob( js.Array( obj ) ) )
        val a = document.createElement( "a" )
        val mouseEventInit = new MouseEventInit {
            override val view : UndefOr[ Window ] = dom.window
            override val detail : UndefOr[ Int ] = 0
        }
        mouseEventInit.cancelable = true
        mouseEventInit.bubbles = true
        mouseEventInit.screenX = 0
        mouseEventInit.screenY = 0
        mouseEventInit.clientX = 0
        mouseEventInit.clientY = 0
        mouseEventInit.ctrlKey = false
        mouseEventInit.altKey = false
        mouseEventInit.metaKey = false
        mouseEventInit.shiftKey = false
        mouseEventInit.button = 0
        mouseEventInit.relatedTarget = null
        val evt = new MouseEvent(
            "click",
            mouseEventInit,
        )
        a.setAttribute( "style", "display: none")
        a.setAttribute( "href", url )
        a.setAttribute( "download", fileName )
        document.body.appendChild( a )
        a.dispatchEvent( evt )
        dom.URL.revokeObjectURL( url )
        document.body.removeChild( a )
    }

}
