package com.twosixtech.dart.taxonomy.explorer.serialization

import better.files.Resource
import com.twosixtech.dart.taxonomy.explorer.models.{DartTaxonomyDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class OntologyWriterTest
  extends AnyFlatSpecLike
    with Matchers
    with OntologyReaderDI
    with WmOntologyWriterDI
    with DartTaxonomyDI
    with UUIDTaxonomyIdDI
    with UUIDTaxonomyIdSerializationDI
    with WmDartConceptDI {

    behavior of "OntologyWriter"

    it should "write an ontology to a file, and the taxonomy read back from that should be the same as the original" in {

        val ontology = Resource.getAsString( "wm-ontology.yml" )

        val t1 : DartTaxonomy = OntologyReader.ymlToOntology( ontology ).get
        val t2 : DartTaxonomy = OntologyReader.ymlToOntology( OntologyWriter.taxonomyYaml( t1 ) ).get

        def compareConcepts( s1 : Set[ DartConcept ], s2 : Set[ DartConcept ] ) : Unit = {
            s1.size shouldBe s2.size
            s1.foreach { v =>
                s2.find( _.name == v.name ) match {
                    case None => fail( s"Names not identical:\n${s1.toSeq.sortBy(_.name)}\n${s2.toSeq.sortBy(_.name)}" )
                    case Some( c ) =>
                        v.metadata shouldBe c.metadata
                        compareConcepts( v.children, c.children )
                }
            }
        }

        compareConcepts( t1.rootConcepts, t2.rootConcepts )

        // Entries won't be the same...
       t1.rootConcepts shouldBe t2.rootConcepts

    }

}
