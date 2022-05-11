package com.twosixtech.dart.taxonomy.explorer.frontend.base.context.js

import com.twosixlabs.dart.auth.groups.{ DartGroup, ProgramManager, TenantGroup }
import com.twosixlabs.dart.auth.tenant.{ CorpusTenant, DartTenant, GlobalCorpus }
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.scalajs.backend.HttpBody.{ BinaryBody, JsFormData, NoBody, TextBody }
import com.twosixtech.dart.scalajs.backend.{ HttpBody, HttpMethod, HttpRequest, HttpResponse }
import com.twosixtech.dart.scalajs.react.ReactComponent
import com.twosixtech.dart.taxonomy.explorer.api.UserDataApiDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.{ DartBackendDeps, KeycloakXhrDartBackendDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.base.circuit.DartCircuitDeps
import com.twosixtech.dart.taxonomy.explorer.frontend.base.router.DartRouterDI
import japgolly.scalajs.react.vdom.{ VdomElement, VdomNode }
import japgolly.scalajs.react.raw.React
import japgolly.scalajs.react.{ Callback, Children, JsComponent }
import org.scalajs.dom.{ FormData, console, window }

import scala.scalajs.js
import scala.scalajs.js.JSConverters.getClass
import scala.scalajs.js.{ JSON, typeOf }
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.typedarray.{ ArrayBuffer, Int8Array, byteArray2Int8Array, int8Array2ByteArray }
import scala.util.{ Failure, Success, Try }

@js.native
trait ReduxProviderProps extends js.Object {
    var stateKey : String = js.native
    var skipInit : Boolean = js.native
    var loader : React.Node = js.native
    var report : js.Function2[ String, js.UndefOr[ String ], Unit ] = js.native
    // Parameters are 1. Key, 2. Data 3. onSuccess, and 4. onFailure
    var save : js.Function4[ String, js.Any, js.Function1[ String, Unit ], js.Function1[ String, Unit ], Unit ]
    // Parameters are 1. Key, 2. onSuccess, and 3. onFailure
    var retrieve : js.Function3[ String, js.Function1[ js.Any, Unit ], js.Function1[ String, Unit ], Unit ]
}

@JSImport( "../jsAppExport.jsx", "ReduxProvider" )
@js.native
object ReduxProviderRaw extends js.Object

object ReduxProvider {
    private val cmp = JsComponent[ ReduxProviderProps, Children.Varargs, Null]( ReduxProviderRaw )
    def component( props : ReduxProviderProps, eles : VdomElement* ) = cmp( props )( eles : _* )
}

@js.native
trait JsLogging extends js.Object {
    var alert : js.Function2[ String, js.UndefOr[ String ], Unit ] = js.native
    var report : js.Function2[ String, js.UndefOr[ String ], Unit ] = js.native
    var log : js.Function1[ String, Unit ] = js.native
}

@js.native
trait JsRouter extends js.Object {
    var documentView : js.Function1[ String, Unit ] = js.native
    var back : js.Function0[ Unit ] = js.native
}

@js.native
trait JsUserData extends js.Object {
    // Parameters are 1. Key, 2. Data 3. onSuccess, and 4. onFailure
    var save : js.Function4[ String, js.Any, js.Function1[ String, Unit ], js.Function1[ String, Unit ], Unit ]
    // Parameters are 1. Key, 2. onSuccess, and 3. onFailure
    var retrieve : js.Function3[ String, js.Function1[ js.Any, Unit ], js.Function1[ String, Unit ], Unit ]
}

@js.native
trait JsDartContext extends js.Object {
    var router : JsRouter = js.native
    var xhrHandler : js.Function10[
        String, // method
        String, // url
        js.Any, // body
        js.Function1[js.Any, js.Object], // startAction
        js.Function1[js.Any, js.Object], // completeAction
        js.Function1[js.Any, js.Object], // errorAction
        js.Function1[js.Object, Unit], // dispatch
        js.UndefOr[ js.Object ], // state
        js.UndefOr[ js.Function1[Double, Unit] ], // setProgressUpload
        js.UndefOr[ Int ], // successStatus
        Unit,
    ] = js.native

    var userData : JsUserData = js.native

    var log : JsLogging = js.native
    var userName : String = js.native
    var tenants : js.Array[ String ] = js.native
    var refreshTenants : js.Function0[ Unit ] = js.native

    var loader : React.Node = js.native

    // For emergency use
    var token : String = js.native
}

@js.native
trait JsDartContextProviderProps extends js.Object {
    var dartContext : JsDartContext = js.native
}

@JSImport( "../jsAppExport.jsx", "JsDartContextProvider" )
@js.native
object JsDartContextProviderRaw extends js.Object


trait JsDartContextProviderDI {
    this : KeycloakXhrDartBackendDI with ErrorHandlerDI with DartCircuitDeps with DartRouterDI with UserDataApiDI =>


    object JsDartContextProvider {
        private val cmp = JsComponent[ js.Object, Children.Varargs, Null]( JsDartContextProviderRaw )
        private def component( context : JsDartContextProviderProps )( eles : VdomNode* ) =
            cmp( context )( eles : _* )

        private def defaultBack( set : DartRouter.DartRoute => Callback )() : Unit = {
            val history = window.history
            if ( history.length <= 1 ) set( DartRouter.CorpexRoute ).runNow()
            else history.back()
        }

        case class Props(
            backend : DartBackend.Cx,
            log : ErrorHandler.Logger,
            report : ErrorHandler.PublicReporter,
            set : DartRouter.DartRoute => Callback,
            loader : VdomNode,
            tenants : Seq[ DartTenant ],
            refreshTenants : Callback,
            child : VdomNode,
        ) {
            def buildJsContext : JsDartContext = {
                val ctx = ( new js.Object ).asInstanceOf[ JsDartContext ]
                backend.token.foreach( ctx.token = _ )
                ctx.loader = loader.rawNode
                ctx.router = {
                    val rtr = ( new js.Object ).asInstanceOf[ JsRouter ]
                    rtr.documentView = ( id : String ) => set( DartRouter.DocumentRoute( id ) ).runNow()
                    rtr.back = () => defaultBack( set )()
                    rtr
                }
                ctx.log = {
                    val jsLog = ( new js.Object ).asInstanceOf[ JsLogging ]
                    jsLog.log = (msg : String ) => log( msg )
                    jsLog.alert = (alertMsg : String, logMsg : js.UndefOr[ String ]) =>
                      if ( logMsg == js.undefined ) report.alert( alertMsg ).runNow()
                      else report.logAlert( alertMsg, logMsg.getOrElse( "" ) ).runNow()
                    jsLog.report = (reportMsg : String, logMsg : js.UndefOr[ String ]) =>
                        if ( logMsg == js.undefined ) report.message( reportMsg ).runNow()
                        else report.logMessage( reportMsg, logMsg.getOrElse( "" ) ).runNow()
                    jsLog
                }
                ctx.userName = backend.user.map( _.userName ).orNull
                import js.JSConverters._
                ctx.tenants = ( tenants map {
                    case GlobalCorpus => DartTenant.globalId
                    case CorpusTenant( id, _ ) => id
                } ).toJSArray
                ctx.refreshTenants = () => refreshTenants.runNow()
                ctx.userData = {
                    import scalajs.concurrent.JSExecutionContext.Implicits.queue
                    val ud = ( new js.Object ).asInstanceOf[ JsUserData ]


                    ud.save = ( key, data, onSuccess, onFailure ) => {
                        val payload =
                            if ( typeOf( data ) == "string" ) data.asInstanceOf[ String ]
                            else Try( JSON.stringify( data ) ).getOrElse( data.asInstanceOf[ String ] )

                        backend.authClient.submit(
                            HttpMethod.Post,
                            HttpRequest(
                                UserDataApi.userDataPath( key ),
                                body = HttpBody.TextBody( payload )
                            )
                        ) onComplete {
                            case Success( HttpResponse( _, 201, TextBody( jsonRes ) ) ) =>
                                onSuccess( jsonRes )
                            case Success( HttpResponse( _, status, TextBody( body ) ) ) =>
                                onFailure( s"User data submission failed with status $status: $body" )
                            case Failure( e ) =>
                                onFailure( s"User data submission failed with exception: ${e.getMessage}" )
                        }
                    }
                    ud.retrieve = ( key, onSuccess, onFailure ) => {
                        backend.authClient.submit(
                            HttpMethod.Get,
                            HttpRequest(
                                UserDataApi.userDataPath( key ),
                            )
                        ) onComplete {
                            case Success( HttpResponse( _, 200, TextBody( jsonRes ) ) ) =>
                                onSuccess( jsonRes )
                            case Success( HttpResponse( _, status, TextBody( body ) ) ) =>
                                onFailure( s"User data retrieval failed with status $status: $body" )
                            case Failure( e ) =>
                                onFailure( s"User data retrieval failed with exception: ${e.getMessage}" )
                        }
                    }
                    ud
                }
                ctx.xhrHandler = {
                    import scalajs.concurrent.JSExecutionContext.Implicits.queue
                    (
                        method : String,
                        url : String,
                        body : js.Any,
                        startAction : js.Function1[js.Any, js.Object], // startAction
                        completeAction : js.Function1[js.Any, js.Object], // completeAction
                        errorAction : js.Function1[js.Any, js.Object], // errorAction
                        dispatch : js.Function1[js.Object, Unit], // dispatch
                        state : js.UndefOr[ js.Object ], // state
                        setProgressUpload : js.UndefOr[ js.Function1[Double, Unit] ], // setProgressUpload
                        successStatus : js.UndefOr[ Int ], // successStatus
                    ) =>
                        val cleanMethod = method.trim.toLowerCase
                        val httpBody = body match {
                            case text if js.typeOf( text ) == "string" => TextBody( text.asInstanceOf[ String ] )
                            case data : ArrayBuffer => BinaryBody( int8Array2ByteArray ( new Int8Array( data ) ) )
                            case formData : FormData => JsFormData( formData )
                            case _ => NoBody
                        }

                        val progressUploadHandler : js.Function1[ Double, Unit ] = {
                            // Mapped in this awkward, redundant way so that some side effect like
                            // logging can be inserted if necessary
                            setProgressUpload.toOption
                              .map( ( fn : js.Function1[ Double, Unit ] ) => {
                                  val newFn : js.Function1[ Double, Unit ] = ( progress : Double ) => {
                                      fn.apply( progress )
                                  }
                                  newFn
                              } )
                              .getOrElse( ( ( _ : Double ) => () ) : js.Function1[ Double, Unit ] )
                        }

                        dispatch( startAction )
                        backend.authClient.submit(
                            cleanMethod match {
                                case "get" => HttpMethod.Get
                                case "post" => HttpMethod.Post
                                case "put" => HttpMethod.Put
                                case "delete" => HttpMethod.Delete
                            },
                            HttpRequest(
                                url = url,
                                body = httpBody,
                            ),
                            progressUploadHandler,
                        ) onComplete {
                            case Success( HttpResponse( _, status, resBody ) ) =>
                                val jsResBody = resBody match {
                                    case JsFormData( data ) => data : js.Any
                                    case TextBody( text ) => text : js.Any
                                    case BinaryBody( data ) => byteArray2Int8Array( data ).buffer : js.Any
                                    case NoBody => null
                                }
                                val finalBody = Try( JSON.parse( jsResBody.asInstanceOf[ String ] ) )
                                  .getOrElse( jsResBody )

                                if ( status != successStatus.getOrElse( 200 ) ) {
                                    if ( finalBody == null ) dispatch( errorAction( status ) )
                                    else dispatch( errorAction( finalBody ) )
                                } else dispatch( completeAction( finalBody ) )

                            case Failure( exception ) =>
                                errorAction( exception.getMessage )
                        }
                }
                ctx
            }
        }

        val ContextBuilder : ReactComponent[ Props, Unit ] = ReactComponent.functional[ Props ] {
            case props@Props( _, _, _, _, _, _, _, child ) =>
                val jsContext = props.buildJsContext
                val jsProps = {
                    val jp = ( new js.Object ).asInstanceOf[ JsDartContextProviderProps ]
                    jp.dartContext = jsContext
                    jp
                }
                val rpProps = {
                    val rp = ( new js.Object ).asInstanceOf[ ReduxProviderProps ]
                    rp.stateKey = s"dart-ui-user-data"
                    rp.skipInit = false
                    rp.loader = jsContext.loader
                    rp.save = jsContext.userData.save
                    rp.retrieve = jsContext.userData.retrieve
                    rp.report = jsContext.log.report
                    rp
                }
                ReduxProvider.component(
                    rpProps,
                    JsDartContextProvider.component( jsProps )(
                        child
                    )
                )
        }
    }
}

