package com.twosixtech.dart.taxonomy.explorer.api

trait UserDataApiDI {
  this : RootApiDeps =>

  object UserDataApi {

    val ENDPOINT : String = "/user/data"

    def PATH : String = RootApi.BASE_PATH + ENDPOINT

    def userDataPath( key : String ) : String = PATH + s"/$key"

  }
}
