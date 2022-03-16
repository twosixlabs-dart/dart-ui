package com.twosixtech.dart.scalajs.control

import scala.concurrent.{Future, Promise}
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

object AsyncUtils {

    def delay( milliseconds: Int ): Future[ Unit ] = {
        val p = Promise[ Unit]()
        js.timers.setTimeout( milliseconds ) {
            p.success(())
        }

        p.future
    }

    def after[ T ]( milliseconds : Int )( andThen : => T ) : Future[ T ] =
        delay( milliseconds ).map( _ => andThen )

}
