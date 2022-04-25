package com.twosixtech.dart.taxonomy.explorer.publication

import com.twosixlabs.dart.auth.tenant.{ CorpusTenantIndex, DartTenant }
import com.twosixlabs.dart.ontologies.OntologyUpdatesNotifier
import com.twosixlabs.dart.ontologies.api.{ OntologyArtifact, OntologyRegistry }
import com.twosixtech.dart.taxonomy.explorer.models.DartTaxonomyDI
import com.twosixtech.dart.taxonomy.explorer.serialization.{ OntologyReaderDeps, OntologyWriterDeps }

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

trait OntologyRegistryOntologyPublicationServiceDI
  extends OntologyPublicationServiceDeps {
    this : DartTaxonomyDI
      with OntologyWriterDeps
      with OntologyReaderDeps =>

    case class OntologyPublicationServiceParams(
        executionContext : ExecutionContext,
        tenantIndex : CorpusTenantIndex,
        ontologyRegistry : OntologyRegistry,
        ontologyNotifier : OntologyUpdatesNotifier
    )

    val ontologyPublicationServiceParams : OntologyPublicationServiceParams

    object OntologyRegistryOntologyPublicationService extends OntologyPublicationService {

        implicit lazy val ec : ExecutionContext = ontologyPublicationServiceParams.executionContext

        import ontologyPublicationServiceParams.{ontologyNotifier, ontologyRegistry, tenantIndex}

        override def stage( tenant : String, ontology : DartTaxonomy ) : Future[ Int ] = {
            val ontologyYml = OntologyWriter.taxonomyYaml( ontology )

            Future.fromTry( ontologyRegistry.stageOntology(
                OntologyArtifact(
                    UUID.randomUUID().toString,
                    tenant,
                    -1,
                    -1,
                    ontologyYml,
                )
            ) map ( _.stagingVersion ) )
        }

        override def publishStaged( tenant : String ) : Future[ Option[ Int ] ] = {
            Future.fromTry( ( for {
                artifactOption <- ontologyRegistry.latestStaged( tenant )
                artifact <- Try( artifactOption.getOrElse( throw new NoSuchElementException( s"No staged version in tenant $tenant" ) ) )
                publishedArtifact <- ontologyRegistry.commitOntology( artifact )
                _ <- ontologyNotifier.update( tenant, publishedArtifact.id ).flatMap( res => {
                    if ( res ) Success( () )
                    else Failure( new Exception( "Failed to notify changes" ) )
                } )
            } yield publishedArtifact.version ).map( Some.apply ) )
        }

        override def retrieve( tenant : String, version : Option[ Int ] = None ) : Future[ Option[ DartTaxonomy ] ] = {
            Future.fromTry {
                val versionTry = version match {
                    case None => ontologyRegistry.latest( tenant )
                    case Some( vers ) => ontologyRegistry.byVersion( tenant, vers )
                }

                versionTry.map( _.flatMap( v => OntologyReader.ymlToOntology( v.ontology ).toOption ) )
            }
        }

        /****  ## WARNING: specific version retrieval not implemented! (Just returns latest staged) ### ****/
        override def retrieveStaged( tenant : String, version : Option[ Int ] = None ) : Future[ Option[ DartTaxonomy ] ] = {
            Future.fromTry {
                val versionTry = version match {
                    case None => ontologyRegistry.latestStaged( tenant )
                    case Some( _ ) => ontologyRegistry.latestStaged( tenant )
                }

                versionTry.map( _.flatMap( v => OntologyReader.ymlToOntology( v.ontology ).toOption ) )
            }
        }

        override def allTenants : Future[ Map[ String, TV ] ] = {
            val nonGlobalTenantsFuture = tenantIndex.allTenants transformWith {
                case Failure( e ) => Future.failed( e )
                case Success( tenants ) =>
                    Future.sequence( tenants.map( tenant => {
                        Future.fromTry( for {
                            publishedArtifactOpt <- ontologyRegistry.latest( tenant.id )
                            stagedArtifactOpt <- ontologyRegistry.latestStaged( tenant.id )
                        } yield {
                            tenant.id -> TV(
                                publishedArtifactOpt.map( _.version ),
                                stagedArtifactOpt.map( _.stagingVersion ),
                            )
                        } )
                    } ) )
            }

            val globalTenantFuture = Future.fromTry( for {
                publishedArtifactOpt <- ontologyRegistry.latest( DartTenant.globalId )
                stagedArtifactOpt <- ontologyRegistry.latestStaged( DartTenant.globalId )
            } yield {
                DartTenant.globalId -> TV(
                    publishedArtifactOpt.map( _.version ),
                    stagedArtifactOpt.map( _.stagingVersion ),
                )
            } )

            for {
                nonGlobalTenants <- nonGlobalTenantsFuture
                globalTenant <- globalTenantFuture
            } yield ( ( globalTenant +: nonGlobalTenants ).toMap )
        }
    }

    override val OntologyPublicationService : OntologyRegistryOntologyPublicationService.type = OntologyRegistryOntologyPublicationService

}
