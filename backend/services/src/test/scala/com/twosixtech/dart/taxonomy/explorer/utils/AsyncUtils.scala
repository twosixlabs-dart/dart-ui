package com.twosixtech.dart.taxonomy.explorer.utils

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class AsyncUtils( timeout : Duration, writeWait : Duration = Duration( 0, TimeUnit.MILLISECONDS ) ) {
    implicit class AwaitableFuture[ T ]( fut : Future[ T ] ) {
        def await : T = Await.result( fut, timeout )
        def awaitWrite : T = {
            val res = Await.result( fut, timeout )
            Thread.sleep( writeWait.toMillis )
            res
        }
    }
}
