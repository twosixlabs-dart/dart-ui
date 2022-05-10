package com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.layouts.wm

import com.twosixtech.dart.scalajs.layout.menu.tabs.{ DartTabs, DartTabsTranslation }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.DartClusterCuratorFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.cluster.curator.layouts.wm.WmDartClusterCuratorFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.DartConceptExplorerFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.frame.layouts.wm.WmDartConceptExplorerFrameLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.{ DartAppWindowDI, DartAppWindowLayoutDeps, DartFrameDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.DartTenantsDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts.GenericDartTenantsLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.DartContextDeps
import japgolly.scalajs.react.CtorType.Props
import japgolly.scalajs.react.component.Js.{ RawMounted, UnmountedWithRawType }
import japgolly.scalajs.react._
import japgolly.scalajs.react.{ Children, JsComponent }
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
trait CorpexProps extends js.Object {
    var docView : Boolean = js.native
    var documentId : String = js.native
}

object CorpexProps {
    def apply( docView : Boolean = false, documentId : String = "" ) : CorpexProps = {
        val cp = ( new js.Object ).asInstanceOf[ CorpexProps ]
        cp.docView = docView
        cp.documentId = documentId
        cp
    }
}

@JSImport( "../jsAppExport.js", "CorpexUi" )
@js.native
object CorpexUIRaw extends js.Object

object CorpexUIDynamic {
    private val cmp = JsComponent[ CorpexProps, Children.None, Null]( CorpexUIRaw )
    def document( docId :String ) = cmp(
        CorpexProps( true, docId )
    )

    def search = cmp( CorpexProps( false, "" ) )
}

object CorpexUI {
    def document( docId : String ) : VdomElement = React.Suspense(
        fallback = "Loading",
        asyncBody = AsyncCallback.fromJsPromise( js.dynamicImport {
            CorpexUIDynamic.document( docId )
        } )
    )

    lazy val search : VdomElement = React.Suspense(
        fallback = "Loading",
        asyncBody = AsyncCallback.fromJsPromise( js.dynamicImport {
            CorpexUIDynamic.search
        } )
    )
}

@JSImport( "../jsAppExport.js", "ForkliftUi" )
@js.native
object ForkliftUIRaw extends js.Object

object ForkliftUIDynamic {
    private val cmp = JsComponent[ js.Object, Children.None, Null ]( ForkliftUIRaw )
    lazy val component = cmp( new js.Object )
}

object ForkliftUI {
    def apply() : VdomElement = React.Suspense(
        fallback = "Loading",
        asyncBody = AsyncCallback.fromJsPromise( js.dynamicImport {
            ForkliftUIDynamic.component
        } )
    )
}

trait WmDartAppWindowLayoutDI
  extends DartAppWindowLayoutDeps {
    this : DartFrameDI
      with DartAppWindowDI
      with DartContextDeps
      with DartComponentDI
      with DartClusterCuratorFrameDI
      with WmDartClusterCuratorFrameLayoutDI
      with DartConceptExplorerFrameDI
      with WmDartConceptExplorerFrameLayoutDI
      with DartTenantsDI
      with GenericDartTenantsLayoutDI =>

    override type DartAppWindowLayoutState = DartAppWindowLayoutBasic.ConceptExplorerView
    // render context is fixed to null at interface so that it is easy to mix
    // in test implementation with guaranteed compatibility.
    // override type DartAppWindowRenderContext = Unit
    override lazy val dartAppWindowLayout = new DartAppWindowLayoutBasic

    class DartAppWindowLayoutBasic extends DartAppWindowLayout {
        import DartAppWindowLayoutBasic._

        override val initialState : ConceptExplorerView = TaxonomyView

        override def render( scope : Scope, conceptView : ConceptExplorerView, props : DartAppWindow.LayoutProps )
          ( implicit renderProps : Unit, context : DartContext ) : VdomElement = {

            props.appChoice match {
                case DartFrame.Test => <.div( "This is a test" )
                case DartFrame.Corpex => CorpexUI.search
                case DartFrame.CorpexDocument( id ) => CorpexUI.document( id )
                case DartFrame.Forklift => ForkliftUI()
                case DartFrame.Tenants => dartTenants( DartTenants.Props().toDartProps )
                case DartFrame.ConceptExplorer =>
                    val app = conceptView match {
                        case TaxonomyView =>
                            dartConceptExplorerFrame( DartConceptExplorerFrame.Props().toDartProps )
                        case ClusterView =>
                            dartClusterCuratorFrame( DartClusterCuratorFrame.Props().toDartProps )
                    }

                    <.div(
                        ^.style := js.Dictionary( "display" -> "flex", "flexFlow" -> "column", "height" -> "100%" ),
                        <.div(
                            DartTabs( DartTabsTranslation.Props(
                                onChange = {
                                    case "cluster" => scope.setState( ClusterView )
                                    case "taxonomy" => scope.setState( TaxonomyView )
                                },
                                value = conceptView match {
                                    case TaxonomyView => "taxonomy"
                                    case ClusterView => "cluster"
                                },
                                tabs = Vector(
                                    DartTabsTranslation.Tab( "Taxonomy", "taxonomy" ),
                                    DartTabsTranslation.Tab( "Clusters", "cluster" ),
                                ),
                            ) ),
                        ),
                        <.div(
                            ^.style := js.Dictionary( "flex" -> "1", "overflow" -> "hidden", "paddingBottom" -> "5px" ),
                            app,
                        ),
                    )
            }

        }
    }

    object DartAppWindowLayoutBasic {
        trait ConceptExplorerView
        case object TaxonomyView extends ConceptExplorerView
        case object ClusterView extends ConceptExplorerView
    }

}
