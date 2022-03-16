package com.twosixtech.dart.taxonomy.explorer.api

trait RootApiDI
  extends RootApiDeps {
    override val RootApi : RootApi = new RootApi {
        override val BASE_PATH : String = "/concepts/explorer"
    }
}
