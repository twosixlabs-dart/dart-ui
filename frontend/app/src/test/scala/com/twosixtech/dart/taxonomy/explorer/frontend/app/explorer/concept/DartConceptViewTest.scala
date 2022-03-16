//package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept
//
//import better.files.{File, Resource}
//import com.twosixtech.dart.taxonomy.explorer.frontend.app.DartRootDI
//import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.DartTestRenderer
//import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.concept.layouts.wm.WmDartConceptFrameLayoutDI
//import com.twosixtech.dart.taxonomy.explorer.frontend.base.{ConnectedAppDI, DartCircuitDeps, DartComponentDI, DartContextDI, DartStateDI}
//import com.twosixtech.dart.taxonomy.explorer.frontend.control.DartRouteConfigDI
//import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
//import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI
//import japgolly.scalajs.react.vdom.VdomElement
//import utest.TestSuite
//
//trait DartConceptViewTestStateData {
//    this: DartTaxonomyDI
//      with WmDartConceptDI
//      with UUIDTaxonomyIdDI
//      with UUIDTaxonomyIdSerializationDI
//      with WmDartSerializationDI =>
//
//    import DartSerialization._
//
//    private val ontologyJson = Resource.getAsString( "wm-ontology.json" )
//    val taxonomy = ontologyJson.unmarshalTaxonomy
//
//    private val socialStructurePath = Seq( "wm", "concept", "clusters", "social_structure" )
//    val socialStructureEntry = taxonomy.pathEntry( socialStructurePath ).get
//
//}
//
//class DartConceptViewTestRenderer
//  extends DartTestRenderer {
//    this : DartRootDI
//      with DartConceptViewTestStateData
//      with DartConceptFrameDI
//      with WmDartConceptFrameLayoutDI
//      with DartRouteConfigDI
//      with DartComponentDI
//      with ConnectedAppDI
//      with DartCircuitDeps with DartStateDI
//      with DartContextDI =>
//
//    override def renderComponent( implicit context : DartContext ) : VdomElement = {
//        dartConceptFrame( DartConceptFrame.Props(
//        ) )
//        ???
//    }
//
//    override val initState : DartState = ???
//    override val coreHandlers : Seq[ CoreHandler[ _ ] ] = ???
//    override val layoutHandlers : Seq[ LayoutHandler[ _ ] ] = ???
//    override val dartCircuit : DartCircuit = ???
//}
//
