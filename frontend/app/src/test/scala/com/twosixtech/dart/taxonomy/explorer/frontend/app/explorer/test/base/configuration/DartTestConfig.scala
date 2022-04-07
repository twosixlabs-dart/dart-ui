package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.configuration

import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.{DartConfig, DartConfigDeps}

import scala.scalajs.js

@js.native
trait DartTestConfig extends js.Object {
    var enableLogging : Boolean = js.native
    var logMaxLength : Int = js.native
    var publicReportDuration : Int = js.native
    var basePath : String = js.native
    var disableAuth : Boolean = js.native
    var tenantsBaseUrl : String = js.native
}

trait DartTestConfigDI extends DartConfigDeps {

    override type DartConfigType = DartConfig

    override lazy val dartConfig : DartConfig = {
        val dc = ( new js.Object ).asInstanceOf[ DartTestConfig ]
        dc.enableLogging = false
        dc.logMaxLength = 0
        dc.publicReportDuration = 0
        dc.basePath = ""
        dc.disableAuth = false
        dc.tenantsBaseUrl = "test-tenants-url"
        dc.asInstanceOf[ DartConfig ]
    }

}
