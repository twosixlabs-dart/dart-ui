import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp

object Main extends DartApp.Wm {

    def main( args : Array[ String ] ) : Unit = run()

}

//object Main
//  extends DartBackendDI
//    with GenericDartConfigDI {
//
//    private def getDiv( divId : String ) : Div = {
//        val queryResult = document.querySelector( s"#$divId" )
//        queryResult match {
//            case elem : Div => elem
//            case _ =>
//                val targetEle : Div = div(
//                    id := divId,
//                    style := "height: 100%",
//                ).render
//                document.body.appendChild( targetEle )
//                targetEle
//        }
//    }
//
//    def main( args : Array[ String ] ) : Unit = {
//        DartBackend.keycloakContextComponent(
//            client = XhrBackendClient,
//            render = { backendContext =>
//                println( backendContext )
//                <.div( "hello" )
//            }
//        ).renderIntoDOM( getDiv( "app" ) )
//    }
//
//}
