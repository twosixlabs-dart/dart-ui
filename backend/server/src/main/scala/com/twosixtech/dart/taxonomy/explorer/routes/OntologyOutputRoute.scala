package com.twosixtech.dart.taxonomy.explorer.routes

import akka.event.Logging.{InfoLevel, LogLevel}
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LoggingMagnet}
import akka.stream.Materializer
import akka.util.ByteString
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, OntologyReaderDI, OntologyWriterDeps}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait OntologyOutputRouteDI {
    this : DartConceptDeps
      with OntologyWriterDeps
      with DartSerializationDeps
      with OntologyReaderDI =>

    object OntologyOutputRoute {

        import DartSerialization._

        private def logRequestEntity(route: Route, level: LogLevel)
          (implicit m: Materializer, ex: ExecutionContext) = {

            def requestEntityLoggingFunction(loggingAdapter: LoggingAdapter)(req: HttpRequest): Unit = {
                val timeout = 900.millis
                val bodyAsBytes: Future[ByteString] = req.entity.toStrict(timeout).map(_.data)
                val bodyAsString: Future[String] = bodyAsBytes.map(_.utf8String)
                bodyAsString.onComplete {
                    case Success(body) =>
                        val logMsg = s"$req\nRequest body: $body"
                        loggingAdapter.log(level, logMsg)
                    case Failure(t) =>
                        val logMsg = s"Failed to get the body for: $req"
                        loggingAdapter.error(t, logMsg)
                }
            }
            DebuggingDirectives.logRequest(LoggingMagnet(requestEntityLoggingFunction(_)))(route)
        }

        private lazy val postTaxonomyRoute : Route = pathEndOrSingleSlash{
            post {
                decodeRequest {
                    entity( as[ String ] ) { str =>
                        val ontologyYml = OntologyWriter.taxonomyYaml( str.unmarshalTaxonomy )
                        complete( 200, List( `Content-Type`( `text/plain(UTF-8)` ) ), ontologyYml )
                    }
                }
            }
        }

        private lazy val postOntology : Route = path( "parse" ) {
            decodeRequest {
                entity( as[ String ] ) { str =>
                    val taxonomyTry = OntologyReader.ymlToOntology( str )
                    taxonomyTry match {
                        case Success( taxonomy ) =>
                            complete( 200, Nil, taxonomy.marshalJson )
                        case Failure( e ) =>
                            complete( status = 500, Nil, e.getMessage )
                    }
                }
            }
        }

        import scala.concurrent.ExecutionContext.Implicits.global

        def route : Route = pathPrefix( "ontology" )(
            toStrictEntity( 5.seconds ) (
                extractMaterializer { implicit mat : Materializer =>
                    logRequestEntity(
                        postTaxonomyRoute ~ postOntology,
                        InfoLevel
                    )
                }
            )
        )
    }

}
