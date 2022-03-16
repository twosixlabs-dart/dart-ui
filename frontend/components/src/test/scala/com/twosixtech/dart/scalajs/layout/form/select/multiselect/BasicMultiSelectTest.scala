package com.twosixtech.dart.scalajs.layout.form.select.multiselect
import org.scalajs.dom.{Element, MouseEvent}
import org.scalajs.dom.raw.{HTMLElement, MouseEventInit}

import scala.scalajs.js

trait BasicMultiSelectTestStateConfig extends MultiSelectTestStateConfig {
    override type Obs = BasicObservation

    class BasicObservation( override val ele : HTMLElement ) extends Observation {
        override def toggleSelectAction( value : String ) : Unit = {
            val entry : Option[Element ] = getEntryElement( value )
            val mouseEvent = new MouseEvent(
                "mousedown",
                {
                    val mi = ( new js.Object ).asInstanceOf[ MouseEventInit ]
                    mi.bubbles = true
                    mi
                },
            )
            entry.get.dispatchEvent( mouseEvent )
        }

        override def multiSelectAction( value : String ) : Unit = {
            val entry : Option[Element ] = getEntryElement( value )
            val mouseEvent = new MouseEvent(
                "mousedown",
                {
                    val mi = ( new js.Object ).asInstanceOf[ MouseEventInit ]
                    mi.bubbles = true
                    mi.metaKey = true
                    mi
                },
            )
            entry.foreach( _.dispatchEvent( mouseEvent ) )
        }

        override def extendSelectAction( value : String ) : Unit = {
            val entry : Option[Element ] = getEntryElement( value )
            val mouseEvent = new MouseEvent(
                "mousedown",
                {
                    val mi = ( new js.Object ).asInstanceOf[ MouseEventInit ]
                    mi.bubbles = true
                    mi.shiftKey = true
                    mi
                },
            )
            entry.foreach( _.dispatchEvent( mouseEvent ) )
        }
    }

    override def genObs( ele : HTMLElement ) : BasicObservation = new BasicObservation( ele )
}

object BasicMultiSelectTest
  extends MultiSelectTest( new BasicMultiSelect[ String ] )
    with BasicMultiSelectTestStateConfig {
}

