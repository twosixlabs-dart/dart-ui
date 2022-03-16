package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI, TaxonomyIdDeps, TaxonomyIdSerializationDeps}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps

trait TaxonomyRoutesDI {
    this : DartConceptDeps
      with DartSerializationDeps
      with DartTaxonomyDI
      with AuthRouterDI
      with TaxonomyIdDeps
      with TaxonomyIdSerializationDeps  =>

    import DartSerialization._

    class TaxonomyRoutes( taxonomies : Map[ String, DartTaxonomy ] ) {
        lazy val listTaxonomies : Route = pathEnd {
            complete( 200, Nil, taxonomies.keys.mkString( "\n" ) )
        }

        lazy val getTaxonomy : Route = path( Segment ) { taxonomy =>
            taxonomies.get( taxonomy ) match {
                case None => complete( 404, Nil, "Taxonomy not found" )
                case Some( taxonomy ) =>
                    complete( 200, Nil, taxonomy.marshalJson )
            }
        }

        def route : Route = pathPrefix( "taxonomies" ) {
            AuthRouter.authRoute { _ =>
                getTaxonomy ~ listTaxonomies
            }
        }
    }

}
