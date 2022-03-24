package com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.backend

import com.twosixlabs.dart.auth.groups.ProgramManager
import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.scalajs.backend.{ BackendClient, BackendComponent, XhrBackendClient }
import com.twosixtech.dart.scalajs.test.{ BackendMocks, MockBackendClientComponent, RequestMocker, TestBackendClient }
import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.test.base.configuration.DartTestConfigDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.backend.DartBackendDeps
import japgolly.scalajs.react.{ Callback, ScalaComponent }
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.{ VdomElement, VdomNode }

import java.util.UUID

trait MockedBackendDI extends DartBackendDeps {
    this : DartTestConfigDI with ErrorHandlerDI =>

    trait MockedBackend extends DartBackend {
        override def client : BackendClient = XhrBackendClient

        case class TestBackendContext(
            client : BackendClient,
            authClient : BackendClient,
            user : Option[ DartUser ],
            mockContext : MockBackendClientComponent.MockBackendContext,
        ) extends Context

        override type Cx = TestBackendContext
        override type St = Unit

        override def genContext(
            authClient : BackendClient, user : Option[ DartUser ]
        ) : TestBackendContext = TestBackendContext(
            client,
            client,
            user,
            new MockBackendClientComponent.MockBackendContext( UUID.randomUUID().toString, BackendMocks.NoResponse(), _ => Callback() ),
        )

        override def emptyState : Unit = ()

        private val mockBackendClientComponent = new MockBackendClientComponent

        override lazy val enabledContextComponent : BackendComponent[ TestBackendContext, Unit ] =
            new BackendComponent[ TestBackendContext, Unit ] {
                override type BackendType = Unit

                val component = ScalaComponent.builder[ TestBackendContext => VdomNode ]
                  .initialState()
                  .render_P( renderer => {
                      mockBackendClientComponent { mockClientContext =>
                          val mockedClient = mockClientContext.client
                          val cx = TestBackendContext(
                              mockedClient,
                              mockedClient,
                              Some( DartUser( "program-manager", Set( ProgramManager ) ) ),
                              mockClientContext,
                          )
                          renderer( cx )
                      }
                  } )
                  .build

                override def apply(
                    props : TestBackendContext => VdomNode,
                ) : Unmounted[ TestBackendContext => VdomNode, Unit, Unit ] = component( props )
            }
    }

    override val DartBackend : MockedBackend = new MockedBackend {}
}
