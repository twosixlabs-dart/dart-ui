package com.twosixtech.dart.scalajs.layout.events

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.raw.SyntheticKeyboardEvent
import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.vdom.{Attr, VdomNode}
import japgolly.scalajs.react.vdom.html_<^._

object KeyHandler {

    private[ events ] case class Classes(
        root : Option[ String ] = None,
    )

    private[ events ] case class Props(
        element : VdomNode,
        onEnter : Option[ Callback ] = None,
        onEscape : Option[ Callback ] = None,
        onKeyPress : Option[ String => Callback ] = None,
        classes : Classes = Classes(),
    )

    private[ events ] val component = ScalaComponent.builder[ Props ]
      .noBackend
      .render_P( props => {
          <.div(
              ^.className := props.classes.root.orNull,
              ^.onKeyDown ==> ( ( e : Attr.Event[ SyntheticKeyboardEvent ]#Event ) => {
                  if ( e.key == "Enter" ) props.onEnter.getOrElse( Callback() )
                  else if ( e.keyCode == 27 ) props.onEscape.getOrElse( Callback() )
                  else props.onKeyPress.getOrElse( ( _ : String ) => Callback() )( e.key )
              } ),
              props.element,
          )
      } )
      .build

    implicit class WithKeyHandler( element : VdomNode ) {

        def withKeyHandler(
            onEnter : Option[ Callback ] = None,
            onEscape : Option[ Callback ] = None,
            onKeyPress : Option[ String => Callback ] = None,
            classes : Classes = Classes(),
        ) : Unmounted[ Props, Unit, Unit ] = component( Props( element, onEnter, onEscape, onKeyPress, classes ) )

        def withOnEnter(
            onEnter : Callback,
            classes : Classes = Classes(),
        )  : Unmounted[ Props, Unit, Unit ] = component( Props( element, Some( onEnter ), classes = classes ) )

    }

    implicit def WithKeyHandlerUnmounted[ A, B, C ]( um : Unmounted[ A, B, C ] ) : WithKeyHandler = {
        WithKeyHandler( um )
    }

}
