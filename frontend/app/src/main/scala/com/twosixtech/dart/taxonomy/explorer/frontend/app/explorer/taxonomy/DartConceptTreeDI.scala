package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.taxonomy

import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.{DartComponentDI, DartStateDI}
import com.twosixtech.dart.taxonomy.explorer.models.Taxonomy.ConceptPath
import com.twosixtech.dart.taxonomy.explorer.models.{DartConceptDeps, DartTaxonomyDI}
import japgolly.scalajs.react.vdom.VdomElement

import scala.collection.immutable
import scala.language.postfixOps
import scala.util.Try

trait DartConceptTreeDI {
    this : DartComponentDI
      with DartConceptTreeLayoutDeps
      with DartContextDeps
      with DartConceptExplorerDI
      with DartStateDI
      with DartConceptBranchDI
      with DartConceptDeps
      with DartTaxonomyDI =>

    lazy val dartConceptTree : DartConceptTree = new DartConceptTree

    class DartConceptTree
      extends ViewedDartComponent[ DartConceptTree.Props, DartConceptTreeRenderContext, DartConceptExplorer.State ] {
        override def stateView( coreState : CoreState ) : DartConceptExplorer.State = coreState.conceptState

        override def render( props : DartConceptTree.Props, stateView : DartConceptExplorer.State )(
            implicit renderContext : DartConceptTreeRenderContext,
            stateContext : DartContext ) : VdomElement = {

            import stateView._

            def getEntriesPath( entry : DartTaxonomyEntry ) : Seq[ DartTaxonomyEntry ] = {
                taxonomy.parentEntry( entry.id ) match {
                    case None => Seq( entry )
                    case Some( parentEntry ) =>
                        getEntriesPath( parentEntry ) :+ entry
                }
            }

            val choiceEntry : Option[ DartTaxonomyEntry ] = conceptChoice.flatMap( taxonomy.idEntry )

            val choiceEntriesPath : Seq[ DartTaxonomyEntry ] = choiceEntry.map( getEntriesPath ).toList.flatten

            val optionalChoiceEntries = choiceEntriesPath.map( Some.apply )

            val choiceEntriesWithPrevious = (None +: optionalChoiceEntries).zip( choiceEntriesPath )

            val choiceEntriesWithPreviousAndNext =
                choiceEntriesWithPrevious
                  .zip( (optionalChoiceEntries :+ None).tail )
                  .map( outerTup => (outerTup._1._1, outerTup._1._2, outerTup._2) )

            val childrenBranchProps =
                choiceEntriesWithPreviousAndNext
                  .map( entryTup => {
                      val (prevEntryOpt, entry, nextEntryOpt) = entryTup

                      val childrenMap = entry.concept.children.map( childConcept => {
                          val childPath = entry.path :+ childConcept.name
                          val childEntry = taxonomy.pathEntry( childPath ).get
                          ( childEntry.id -> childConcept )
                      } ).toMap

                      DartConceptBranch.Props(
                          concepts = childrenMap,
                          Some( entry.id ),
                          terminal = nextEntryOpt.isEmpty,
                          choice = nextEntryOpt.map( _.id ),
                      )
                  } )

            val rootBranchProps = {
                DartConceptBranch.Props(
                    concepts = taxonomy.rootConcepts.map( concept => {
                        val path = Seq( concept.name )
                        val id = taxonomy.pathEntry( path ).get.id
                        id -> concept
                    } ).toMap,
                    None,
                    choiceEntriesPath.headOption.map( _.id ),
                    conceptChoice.isEmpty,
                )
            }

            val branchProps = rootBranchProps +: childrenBranchProps

            dartConceptTreeLayout( DartConceptTree.LayoutProps( branchProps ).toDartProps )
        }
    }

    object DartConceptTree {
        case class Props()
        case class LayoutProps(
            branchProps : Seq[ DartConceptBranch.Props ]
        )
    }
}

trait DartConceptTreeLayoutDeps { this : DartConceptTreeDI with DartComponentDI =>
    type DartConceptTreeRenderContext
    type DartConceptTreeLayoutState
    val dartConceptTreeLayout : DartConceptTreeLayout

    trait DartConceptTreeLayout
      extends DartLayoutComponent[
        DartConceptTree.LayoutProps,
        DartConceptTreeRenderContext,
        DartConceptTreeLayoutState
      ]
}