package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, PathMatcher, PathMatchers, Route}
import com.twosixtech.dart.taxonomy.explorer.api.OntologyPublicationApiDI
import com.twosixtech.dart.taxonomy.explorer.models.DartTaxonomyDI
import com.twosixtech.dart.taxonomy.explorer.publication.OntologyPublicationServiceDeps

import scala.util.{Failure, Success}

trait OntologyPublicationRoutesDI {
    this : OntologyPublicationApiDI
      with AuthRouterDI
      with DartTaxonomyDI
      with OntologyPublicationServiceDeps =>

    object OntologyPublicationRoutes {
        private lazy val stageRoute = ignoreTrailingSlash {
            path( separateOnSlashes( submitStagedPath.stripPrefix( "/" ) ) / PathMatchers.Segment ) { ( tenant : String ) =>
                AuthRouter.authRoute { _ =>
                    post {
                        decodeRequest {
                            entity( as[ String ] ) { str =>
                                val taxonomy = str.parseStageRequest

                                onComplete( OntologyPublicationService.stage( tenant, taxonomy ) ) {
                                    case Success( newVersion ) => complete( 201, Nil, newVersion.stageResponseJson )
                                    case Failure( exception ) => complete( 500, Nil, exception.getMessage )
                                }
                            }
                        }
                    }
                }
            }
        }

        private lazy val publishRoute = ignoreTrailingSlash {
            path( separateOnSlashes( publishPath.stripPrefix( "/" ) ) / PathMatchers.Segment ) { ( tenant : String ) =>
                AuthRouter.authRoute { _ =>
                    post {
                        onComplete( OntologyPublicationService.publishStaged( tenant ) ) {
                            case Success( Some( newVersion ) ) => complete( 201, Nil, newVersion.stageResponseJson )
                            case Success( None ) => complete( 404, Nil, s"no staged version exists for tenant $tenant" )
                            case Failure( exception ) => complete( 500, Nil, exception.getMessage )
                        }
                    }
                }
            }
        }

        private lazy val retrieveStagedRoute : Route = ignoreTrailingSlash {
            path( separateOnSlashes( retrieveStagedPath.stripPrefix( "/" ) ) / PathMatchers.Segment / PathMatchers.IntNumber.? ) {
                ( tenant, versionOpt ) =>
                    AuthRouter.authRoute { _ =>
                        get {
                            onComplete( OntologyPublicationService.retrieveStaged( tenant, versionOpt ) ) {
                                case Success( Some( taxonomy ) ) =>
                                    complete( 200, Nil, taxonomy.retrievedTaxonomyJson )
                                case Success( None ) =>
                                    complete( 404, Nil, s"no staged version exists for tenant $tenant" )
                                case Failure( e ) =>
                                    complete( 500, Nil, e.getMessage )
                            }
                        }
                    }
            }
        }

        private lazy val retrievePublishedRoute : Route = ignoreTrailingSlash {
            path( separateOnSlashes( retrievePath.stripPrefix( "/" ) ) / PathMatchers.Segment / PathMatchers.IntNumber.? ) {
                ( tenant, versionOpt ) =>
                    AuthRouter.authRoute { _ =>
                        get {
                            onComplete( OntologyPublicationService.retrieve( tenant, versionOpt ) ) {
                                case Success( Some( taxonomy ) ) =>
                                    complete( 200, Nil, taxonomy.retrievedTaxonomyJson )
                                case Success( None ) =>
                                    complete( 404, Nil, s"no published version exists for tenant $tenant" )
                                case Failure( e ) =>
                                    complete( 500, Nil, e.getMessage )
                            }
                        }
                    }
            }
        }

        private lazy val allTenantsRoute : Route = ignoreTrailingSlash {
            path( separateOnSlashes( retrieveTenantOntologiesEndpoint.stripPrefix( "/" ) ) ) {
                AuthRouter.authRoute { _ =>
                    get {
                        onComplete( OntologyPublicationService.allTenants ) {
                            case Success( tenantVersions: Map[ String, TV ] ) =>
                                complete(
                                    200,
                                    Nil,
                                    tenantVersions
                                      .mapValues(
                                          tv => {
                                              TenantVersion( tv.publishedVersions, tv.stagedVersions )
                                          }
                                      )
                                      .tenantVersionsJson,
                                )
                            case Failure( e ) =>
                                complete( 500, Nil, e.getMessage )
                        }
                    }
                }
            }
        }

        def route : Route =
            allTenantsRoute ~ stageRoute ~ publishRoute ~ retrieveStagedRoute ~ retrievePublishedRoute
    }

}
