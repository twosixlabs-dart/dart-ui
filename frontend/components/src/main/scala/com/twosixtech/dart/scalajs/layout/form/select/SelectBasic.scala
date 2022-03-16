package com.twosixtech.dart.scalajs.layout.form.select

import com.twosixtech.dart.scalajs.layout.styles.ClassNameConversions.optionToCombinableClass
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

class SelectBasic[ ValType ] extends Select[ ValType, Unit ] {
    override type BackendType = Unit

    val component = ScalaComponent.builder[ Select.Props[ ValType ] ]
      .initialState()
      .render_P { case Select.Props( value, items, onChange, disabled, style, classes ) =>
          <.select(
              classes.root.cName,
              ^.value := value.toString,
              ^.onChange ==> ( ( evt : ReactEventFromInput ) => onChange( evt.target.value.asInstanceOf[ ValType ] ) ),
              ^.disabled := disabled,
              items.map( item => {
                  <.option(
                      item.classes.root.cName,
                      ^.value := item.value.toString,
                      ^.key := item.value.toString,
                      item.element,
                  )
              } ).toVdomArray
          )
      }
      .build

    override def apply( props : Select.Props[ ValType ] ) : Unmounted[ Select.Props[ ValType ], Unit, Unit ] =
        component( props )
}
