package com.twosixtech.dart.taxonomy.explorer.utils

import scala.util.{Failure, Try}

object Assertions {

    implicit class TryAssertions[ T ]( tr : Try[ T ] ) {
        def assertFailed : Try[ T ] = {
            assert( tr.isFailure )
            tr
        }

        def assertFailureType[ E ] : Try[ T ] = {
            assert( tr.isFailure )
            tr match {
                case Failure( e : E ) =>
                case _ =>
                    assert( false )
            }
            tr
        }

    }

}
