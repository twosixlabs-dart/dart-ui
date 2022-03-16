package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class StaticRoutes( publicDir : String, defaultFile : String ) {
    private lazy val staticFiles : Route = getFromDirectory( publicDir )
    private lazy val defaultRoute : Route = getFromFile( defaultFile )

    def route : Route = staticFiles ~ defaultRoute
}
