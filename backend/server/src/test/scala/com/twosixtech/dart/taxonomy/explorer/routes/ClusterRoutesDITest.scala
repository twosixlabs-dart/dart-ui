package com.twosixtech.dart.taxonomy.explorer.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKit
import com.twosixlabs.dart.auth.controllers.SecureDartController
import com.twosixtech.dart.taxonomy.explorer.api.ClusteringApiDI
import com.twosixtech.dart.taxonomy.explorer.clustering.TestClusteringServiceDI
import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import com.twosixtech.dart.taxonomy.explorer.serialization.{DartSerializationDeps, WmDartSerializationDI}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.server.Directives._

class ClusterRoutesDITest
  extends AnyFlatSpecLike
    with Matchers
    with ScalatestRouteTest
    with ClusterRoutesDI
    with TestClusteringServiceDI
    with AuthRouterDI
    with ClusteringApiDI
    with WmDartSerializationDI
    with DartTaxonomyDI
    with WmDartConceptDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI
    with WmDartClusterConceptBridgeDI {
    override val authDependencies : SecureDartController.Dependencies =
        SecureDartController.deps( "test-name", None, true )

    behavior of "ClusterRoutes.StringPathMatcher"

    it should "match a string with a leading slash" in {
        val pm = "/test-path"

        import ClusterRoutes.StringPathMatcher

        Get( "/test-path" ) ~> rawPathPrefix( pm.pathMatch )( pathEndOrSingleSlash( complete( StatusCodes.OK ) ) ) ~> check(
            status shouldBe StatusCodes.OK
        )
    }

    it should "match a string without a leading slash" in {
        val pm = "test-path"

        import ClusterRoutes.StringPathMatcher

        Get( "/test-path" ) ~> pathPrefix( pm.pathMatch )( pathEndOrSingleSlash( complete( StatusCodes.OK ) ) ) ~> check(
            status shouldBe StatusCodes.OK
        )
    }

    it should "match multiple segments with a leading slash" in {
        val pm = "/test-path/segment-one/segment-two/endpoint"

        import ClusterRoutes.StringPathMatcher

        Get( "/test-path/segment-one/segment-two/endpoint" ) ~> rawPathPrefix( pm.pathMatch )( pathEndOrSingleSlash( complete( StatusCodes.OK ) ) ) ~> check(
            status shouldBe StatusCodes.OK
        )
    }

    it should "match multiple segments without a leading slash" in {
        val pm = "test-path/segment-one/segment-two/endpoint"

        import ClusterRoutes.StringPathMatcher

        Get( "/test-path/segment-one/segment-two/endpoint" ) ~> pathPrefix( pm.pathMatch )( pathEndOrSingleSlash( complete( StatusCodes.OK ) ) ) ~> check(
            status shouldBe StatusCodes.OK
        )
    }

}
