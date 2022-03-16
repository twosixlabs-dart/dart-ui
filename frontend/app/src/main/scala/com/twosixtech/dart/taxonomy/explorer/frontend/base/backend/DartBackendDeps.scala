package com.twosixtech.dart.taxonomy.explorer.frontend.base.backend

import com.twosixlabs.dart.auth.groups.ProgramManager
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.scalajs.backend.{BackendClient, BackendComponent, BackendContext}
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.DartConfigDeps
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomElement

import scala.scalajs.js

trait DartBackendDeps {
    this : DartConfigDeps
      with ErrorHandlerDI =>

    trait DartBackend {
        def client : BackendClient

        trait Context extends BackendContext {
            def client : BackendClient
            def authClient : BackendClient
            def user : Option[ DartUser ]
        }

        type Cx <: Context
        type St
        def genContext( authClient : BackendClient, user : Option[ DartUser ] ) : Cx
        def emptyState : St

        def enabledContextComponent : BackendComponent[ Cx, St ]

        final lazy val disabledContextComponent : BackendComponent[ Cx, St ] = {
            new BackendComponent[ Cx, St ] {
                override type BackendType = Unit

                val component = ScalaComponent.builder[ Cx => VdomElement ]
                  .initialState( emptyState )
                  .render_P( renderer => {
                      renderer( genContext( client, Some( DartUser( "program-manager", Set( ProgramManager ) ) ) ) )
                  } )
                  .build

                override def apply(
                    renderer : Cx => VdomElement
                ) : Unmounted[ Cx => VdomElement, St, Unit ] = component( renderer )
            }
        }

        final lazy val ContextBuilder : BackendComponent[ Cx, St ] = {
            if ( dartConfig.disableAuth ) disabledContextComponent
            else enabledContextComponent
        }

    }

    val DartBackend : DartBackend

}
