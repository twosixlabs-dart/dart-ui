package com.twosixtech.dart.taxonomy.explorer.frontend.configuration

import com.twosixtech.dart.scalajs.keycloak.{KeycloakInit, KeycloakParams}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.|


@js.native
@JSGlobal( "env" )
object DartConfigUntyped extends js.Object

@js.native
trait DartConfig extends js.Object {
    val enableLogging : Boolean = js.native
    val publicReportDuration : Int = js.native
    val logMaxLength : Int = js.native
    val keycloakParams : KeycloakParams | String = js.native
    val keycloakInit : KeycloakInit = js.native
    val disableAuth : Boolean = js.native
    val basePath : String = js.native
    val tenantsBaseUrl : String = js.native
}

trait DartConfigDeps {

    type DartConfigType <: DartConfig

    lazy val dartConfig : DartConfigType = DartConfigUntyped.asInstanceOf[ DartConfigType ]

}

trait GenericDartConfigDI extends DartConfigDeps {
    override type DartConfigType = DartConfig
}
