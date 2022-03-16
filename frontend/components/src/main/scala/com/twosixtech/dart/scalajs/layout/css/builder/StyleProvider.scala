package com.twosixtech.dart.scalajs.layout.css.builder

import com.twosixtech.dart.scalajs.react.{LifecycleReactComponent, ReactComponent}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.VdomElement
import scalacss.internal.mutable.StyleSheet
import scalacss.DevDefaults._

class StyleProvider( styles : StyleSheet.Inline )
  extends LifecycleReactComponent[ VdomElement ] {

    override protected def componentDidMount(
        cdm : Lifecycle.ComponentDidMount[VdomElement, SnapshotType, SnapshotType] ) : Callback = Callback {
//        println( styles.render( cssStringRenderer, cssEnv ) )
    }

    override protected def render( props : VdomElement ) : VdomElement = props
}

trait WithStyles { this : ReactComponent[ _, _ ] =>

    val styles : StyleSheet.Inline

    val withStyles : StyleProvider = new StyleProvider( styles )

}
