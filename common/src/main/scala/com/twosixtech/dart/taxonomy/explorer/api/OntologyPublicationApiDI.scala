package com.twosixtech.dart.taxonomy.explorer.api

import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.DartSerializationDeps
import upickle.default._

import java.nio.charset.StandardCharsets

trait OntologyPublicationApiDI {
    this : RootApiDeps
      with DartSerializationDeps
      with DartTaxonomyDI =>

    val ONTOLOGY_PUBLICATION_SEGMENT : String = "/ontology"

    def BASE_PATH : String = RootApi.BASE_PATH + ONTOLOGY_PUBLICATION_SEGMENT

    def retrieveTenantOntologiesEndpoint : String = BASE_PATH + "/tenant-versions"

    def retrieveStagedPath : String = BASE_PATH + "/staged"
    def retrieveStagedEndpoint( tenant : String, version : Option[ Int ] = None ) : String =
        retrieveStagedPath + s"/$tenant" + version.map( v => s"/${v}" ).getOrElse( "" )

    def retrievePath : String = BASE_PATH + "/published"
    def retrieveEndpoint( tenant : String, version : Option[ Int ] = None ) : String =
        retrievePath + s"/$tenant" + version.map( v => s"/${v}" ).getOrElse( "" )

    def submitStagedPath : String = BASE_PATH + "/stage"
    def submitStagedEndpoint( tenant : String ) : String = submitStagedPath + s"/$tenant"

    def publishPath : String = BASE_PATH + "/publish"
    def publishEndpoint( tenant : String ) : String = publishPath + s"/$tenant"

    case class TenantVersion(
        publishedVersion : Option[ Int ],
        stagedVersion : Option[ Int ],
    )

    object TenantVersion {
        implicit val rw : ReadWriter[ TenantVersion ] = macroRW
    }

    import DartSerialization._

    implicit class SerializableTenantVersions( tenantVersions : Map[ String, TenantVersion ] ) {
        def tenantVersionsJson : String = write( tenantVersions )
        def tenantVersionsBinary : Array[ Byte ] = writeBinary( tenantVersions )
    }

    implicit class RequestableTaxonomy( taxonomy : DartTaxonomy ) {
        def stageSubmissionJson : String = taxonomy.marshalJson
        def stageSubmissionBinary : Array[ Byte ] = taxonomy.marshalBinary

        def retrievedTaxonomyJson : String = stageSubmissionJson
        def retrievedTaxonomyBinary : Array[ Byte ] = stageSubmissionBinary
    }

    implicit class VersionResponse( version : Int ) {
        def stageResponseJson : String = version.toString
        def stageResponseBinary : Array[ Byte ] = version.toString.getBytes( "UTF-8" )

        def publicationResponseJson : String = version.toString
        def publicationResponseBinary : Array[ Byte ] = version.toString.getBytes( "UTF-8" )
    }

    implicit class ParsableTextResponses( res : String ) {
        def parseStageRequest : DartTaxonomy = res.unmarshalTaxonomy

        def parseStageResponse : Int = res.trim.toInt
        def parsePublishResponse : Int = res.trim.toInt

        def parseRetrieveOntologyResponse : DartTaxonomy = res.unmarshalTaxonomy
        def parseRetrieveStagedOntologyResponse : DartTaxonomy = res.unmarshalTaxonomy

        def parseTenantOntologyResponse : Map[ String, TenantVersion ] = read[ Map[ String, TenantVersion ] ]( res )
    }

    implicit class ParsableBinaryResponses( res : Array[ Byte ] ) {
        def parseStageRequest : DartTaxonomy = res.unmarshalTaxonomy
        def parsePublishRequest : DartTaxonomy = res.unmarshalTaxonomy

        def parseStageResponse : Int = new String( res, StandardCharsets.UTF_8 ).parseStageResponse
        def parsePublishResponse : Int = new String( res, StandardCharsets.UTF_8 ).parsePublishResponse

        def parseRetrieveOntologyResponse : DartTaxonomy = res.unmarshalTaxonomy
        def parseRetrieveStagedOntologyResponse : DartTaxonomy = res.unmarshalTaxonomy

        def parseTenantVersionsResponse : Map[ String, TenantVersion ] = readBinary[ Map[ String, TenantVersion ] ]( res )
    }

}
