package com.twosixtech.dart.scalajs.layout.div.modal

import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions.optionToCombinableClass
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

object DartModalBasic extends DartModal[ Unit ] {
    override type BackendType = Unit

    private val component = ScalaComponent.builder[ DartModal.Props ]
      .initialState()
      .render_P( props => {
          if ( props.open ) {
              val modalWindow = <.div(
                  props.classes.root.cName,
                  ^.position := "absolute",
                  ^.left := "50%",
                  ^.top := "50%",
                  ^.transform := "translate(-50%, -50%)",
                  ^.width := "600px",
                  ^.minHeight := "100",
                  ^.pointerEvents := "auto",
                  ^.zIndex := "20001",
                  props.element,
              )

              <.div(
                  ^.position := "fixed",
                  ^.top := "0px",
                  ^.bottom := "0px",
                  ^.left := "0px",
                  ^.right := "0px",
                  ^.zIndex := "20000",
                  props.overlay match {
                      case DartModal.NoOverlay => ^.pointerEvents := "None"
                      case DartModal.TransparentBlocking =>
                          TagMod( ^.backgroundColor := "transparent" )
                      case DartModal.GreyBlocking =>
                          TagMod( ^.backgroundColor := "rgba(0,0,0,0.65)" )
                      case DartModal.GreyNonBlocking =>
                          TagMod( ^.pointerEvents := "None", ^.backgroundColor := "rgba(0,0,0,0.65)" )
                  },
                  modalWindow
              )

          } else EmptyVdom
      } )
      .build

    override def apply( props : DartModal.Props ) : Unmounted[ DartModal.Props, Unit, Unit ] =
        component( props )
}
