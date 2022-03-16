package com.twosixtech.dart.taxonomy.explorer.frontend.configuration

import com.twosixtech.dart.taxonomy.explorer.api.RootApiDeps

trait RootApiDI extends RootApiDeps { this : DartConfigDeps =>

    val RootApi = new RootApi {
        override val BASE_PATH : String = dartConfig.basePath
    }
}

