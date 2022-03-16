package com.twosixtech.dart.scalajs.layout.form.search

import com.twosixtech.dart.scalajs.layout.form.textinput.TextInput
import com.twosixtech.dart.scalajs.react.ReactComponent
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.VdomNode

trait SearchField[ State ] extends ReactComponent[ SearchField.Props, State ] {
    def apply(
        textInput: TextInput.Props,
        results : Option[ Vector[ SearchField.Result ] ],
        onSelect : Callback = Callback(),
        classes : SearchField.Classes = SearchField.Classes(),
    ) : Unmounted[ SearchField.Props, State, BackendType ] = {
        apply( SearchField.Props( textInput, results, onSelect, classes ) )
    }
}

object SearchField {
    case class ResultClasses(
        root : Option[ String ] = None,
    )

    case class Result(
        value : Either[ VdomNode, String ],
        onSelect : Callback = Callback(),
        key : Option[ String ] = None,
        classes : ResultClasses = ResultClasses(),
    )

    case class Classes(
        root : Option[ String ] = None,
        menu : Option[ String ] = None,
        results : ResultClasses = ResultClasses(),
    )

    case class Props(
        textInput : TextInput.Props,
        results : Option[ Vector[ Result ] ],
        onSelect : Callback = Callback(),
        classes : Classes = Classes(),
    )
}
