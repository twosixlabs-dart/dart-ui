package com.twosixtech.dart.taxonomy.explorer.frontend.base

import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDeps

trait DartRootDeps {
    this : DartRouterDeps with DartComponentDI =>

    val dartRoot : SimpleDartComponent[ DartRouter.DartRoute, Unit ]

}
