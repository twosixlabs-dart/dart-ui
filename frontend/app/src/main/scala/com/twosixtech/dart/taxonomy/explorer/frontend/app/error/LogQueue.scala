package com.twosixtech.dart.taxonomy.explorer.frontend.app.error

import scala.collection.immutable.Queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters.genTravConvertible2JSRichGenTrav

sealed trait LogEntry {
    def toJs : JsLogEntry
}

case class MessageLog( message : String ) extends LogEntry {
    override def toJs : JsLogEntry = {
        val jse = ( new js.Object ).asInstanceOf[ JsLogEntry ]
        jse.message = message
        jse.exception = js.undefined
        jse
    }
}
case class ExceptionLog( exception : Throwable, message : Option[ String ] = None )
  extends LogEntry {
    override def toJs : JsLogEntry = {
        val jse = ( new js.Object ).asInstanceOf[ JsLogEntry ]
        message match {
            case None => jse.message = js.undefined
            case Some( msg ) => jse.message = msg
        }
        jse.exception = {
            val exc = ( new js.Object ).asInstanceOf[ JsExceptionLog ]
            exc.className = exception.getClass.getName
            exc.message = exception.getMessage
            exc.exception = exception
            exc
        }
        jse
    }
}

@js.native
trait JsExceptionLog extends js.Object {
    var className : String = js.native
    var message : js.UndefOr[ String ] = js.native
    var exception : Throwable = js.native
}

@js.native
trait JsLogEntry extends js.Object {
    var message : js.UndefOr[ String ] = js.native
    var exception : js.UndefOr[ JsExceptionLog ]
}

sealed class LogQueue( protected val entries : Queue[ LogEntry ], val size : Int, val maxLength : Int ) {
    val length : Int = size

    def isEmpty : Boolean = size == 0

    def nonEmpty : Boolean = !isEmpty

    def map[ T ]( fn : LogEntry => T ) : List[ T ] = entries.map( fn ).toList

    def flatMap( fn : LogEntry => LogQueue ) : LogQueue = {
        val newQueue : Queue[ LogEntry ] = entries.flatMap( v => fn( v ).entries )
        val newSize = newQueue.size
        val (updated, finalSize) = {
            val maybeNewSize = newSize - maxLength
            if ( maybeNewSize > 0 )
                (newQueue.drop( newSize - maxLength ), maybeNewSize)
            else (newQueue, newSize)
        }
        new LogQueue( updated, finalSize, maxLength )
    }

    def foreach( fn : LogEntry => Unit ) : Unit = entries.foreach( fn )

    def log( entry : LogEntry ) : LogQueue = {
        if ( size + 1 > maxLength )
            new LogQueue( entries.drop( 1 ).enqueue( entry ), maxLength, maxLength )
        else new LogQueue( entries.enqueue( entry ), size + 1, maxLength )
    }

    def read : Option[ LogEntry ] = entries.headOption
    def next : LogQueue = {
        if ( size == 0 ) this
        else new LogQueue( entries.drop( 1 ), size - 1, maxLength )
    }

    def toJs : js.Array[ JsLogEntry ] =
        entries.toArray.map( _.toJs ).toJSArray

    override def equals( obj : Any ) : Boolean = obj match {
        case v : LogQueue => entries == v.entries
        case _ => false
    }
}

object LogQueue {
    def apply( maxLength : Int, entries : LogEntry* ) : LogQueue = {
        val entriesQueue = Queue( entries : _* )
        val size = entries.size

        new LogQueue( entriesQueue, size, maxLength )
    }
}
