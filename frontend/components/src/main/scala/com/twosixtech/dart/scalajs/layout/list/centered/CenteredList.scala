package com.twosixtech.dart.scalajs.layout.list.centered

import com.twosixtech.dart.scalajs.layout.events.{ClickOffHandler, ClickOffHandlerProps, IdleHandler}
import com.twosixtech.dart.scalajs.layout.form.field.Field.FieldStyles.&
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.{BackendScope, Callback, CallbackTo, Ref, ScalaComponent}
import org.scalajs.dom.raw.CSSStyleDeclaration
import org.scalajs.dom.{html, window}

import scala.scalajs.js

object CenteredList extends ReactComponent[ CenteredListProps, CenteredListState ] {
    override type BackendType = Backend

    def style( height : Int ) : js.Dictionary[ String ] = {
        js.Dictionary( "height" -> s"${height}px" )
    }

    import scalacss.DevDefaults._
    import scalacss.ScalaCssReact._
    object CenteredListStyle extends StyleSheet.Inline {
        import dsl._

        val fullHeight = style( height( 100 %% ) )

        val fullHeightScrollable = style(
            height( 100 %% ),
            overflow.auto,
            Pseudo.Custom( "::-webkit-scrollbar", PseudoType.Element ) (
                display.none,
            )
        )


    }

    CenteredListStyle.addToDocument()

    class Backend( scope : BackendScope[ CenteredListProps, CenteredListState ] ) {

        val outerRef : Ref.Simple[ html.Div ] = Ref[ html.Div ]
        var refs : Vector[ Ref.Simple[ html.Element ] ] = Vector.empty
        var lastHeight : Int = 0

        def render( props : CenteredListProps ): VdomElement = {
            if ( refs.length != props.items.length ) refs = props.items.map( _ => Ref[ html.Element ] )

            <.div(
                ^.style := style( props.height ),
                IdleHandler( IdleHandler.Props(
                    150,
                    800,
                    scope.modState( s => s.copy( center = s.lastCenteredItem.nonEmpty ) ),
                    className = Some( CenteredListStyle.fullHeight.htmlClass ),
                ) )(
                    ClickOffHandler( ClickOffHandlerProps(
                        onClick = _ => scope.modState( state => {
                            state.copy( center = state.lastCenteredItem.nonEmpty )
                        } ),
                        className = Some( CenteredListStyle.fullHeight.htmlClass ),
                        element =
                          <.div(
                            CenteredListStyle.fullHeightScrollable,
                            <.div( ^.style := js.Dictionary( "height" -> s"${Math.round( props.height / 2 )}px" ) ),
                            ( props.items.zipWithIndex.map {
                                case (item, i) =>
                                    <.div(
                                        ^.style := js.Dictionary( "padding" -> "0px", "margin" -> "0px", "overflow" -> "hidden" ),
                                        ^.key := item.key.getOrElse( s"centered-list-$i" ),
                                        item.element,
                                    ).withRef( refs( i ) )
                            } ).toVdomArray,
                            <.div( ^.style := js.Dictionary( "height" -> s"${Math.round( props.height / 2 ) - lastHeight}px" ) ),
                        ).withRef( outerRef ),
                    ) )
                ),
            )
        }

    }

    private lazy val component = ScalaComponent.builder[ CenteredListProps ]
      .initialState( CenteredListState( None, center = false ) )
      .renderBackend[ BackendType ]
      .getDerivedStateFromProps( (props, state) => {
          if ( props.centeredItem != state.lastCenteredItem )
              state.copy(
                  lastCenteredItem = props.centeredItem,
                  center = props.centeredItem.isDefined,
              )
          else state
      } )
      .componentDidUpdate( cdu => {
          if ( cdu.currentState.center && cdu.currentState.lastCenteredItem.isDefined ) {
              val centeredIndex = cdu.currentState.lastCenteredItem.get
              ( for {
                  heights <- cdu.backend.refs.foldLeft( CallbackTo( Vector.empty[ Int ] ) )( (heightsCb, refCb) => {
                      heightsCb.flatMap( heights =>
                          refCb.get.map( ref => {
                              val styles : CSSStyleDeclaration = window.getComputedStyle( ref )
                              val margins = styles.marginTop.stripSuffix( "px" ).toFloat +
                                            styles.marginBottom.stripSuffix( "px" ).toFloat
                              heights :+ ( ref.offsetHeight + margins ).toInt
                          } ).getOrElse( heights )
                      )
                  } )
                  _ <- {
                      if ( heights.nonEmpty && centeredIndex < heights.length && centeredIndex >= 0 ) {
                          val priorHeight = heights.take( centeredIndex ).sum
                          val centeredPosition = {
                              priorHeight
                          }
                          cdu.backend.outerRef.foreach( _.scrollTop = centeredPosition )
                      } else Callback()
                  }
                  _ <- {
                      if ( heights.nonEmpty && heights.last != cdu.backend.lastHeight ) {
                          cdu.backend.lastHeight = heights.last
                          cdu.forceUpdate
                      } else Callback()
                  }
              } yield () )
                .>>( cdu.modState( _.copy( center = false ) ) )
          } else Callback()
      } )
      .build

    override def apply(
        props : CenteredListProps ) : Unmounted[CenteredListProps, CenteredListState, Backend] = {
        component( props )
    }
}

case class ListItem(
    key : Option[ String ],
    element : VdomNode,
)

case class CenteredListProps(
    items : Vector[ ListItem ],
    centeredItem : Option[ Int ],
    height : Int,
)

case class CenteredListState(
    lastCenteredItem : Option[ Int ],
    center : Boolean,
)
