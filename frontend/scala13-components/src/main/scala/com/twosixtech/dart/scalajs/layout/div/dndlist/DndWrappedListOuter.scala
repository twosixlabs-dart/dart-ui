package com.twosixtech.dart.scalajs.layout.div.dndlist

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.{Key, ListClass, RendererParameters}
import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedListContext.ListId
import com.twosixtech.dart.scalajs.layout.div.dndlist.utils.IdUtils
import japgolly.scalajs.react.Ref.Simple
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html


object DndWrappedListOuter {

    case class Props(
        listId : ListId,
        lists : Seq[ Seq[ Key ] ],
        renderer : RendererParameters => VdomElement,
        maxWidth : Int,
        refs : Map[ Key, Simple[ html.Element ]],
        listClass : ListClass
    )

    case class State(
        isDragging : Boolean,
    )

    class Backend( $ : BackendScope[ Props, State ] ) {
        def render( props : Props, state : State ) : VdomElement = {
            <.div {
                val eleList : Seq[html_<^.VdomElement ] = ( DndWrappedListInner( DndWrappedListInner.Props(
                    IdUtils.generateDroppableIdLast( props.listId ),
                    Seq(),
                    props.renderer,
                    props.maxWidth,
                    props.refs,
                    props.listClass,
                ) ).vdomElement +: props.lists.filter( _.nonEmpty ).zipWithIndex.foldLeft( Seq[ VdomElement ]() )( ( vdEles : Seq[ VdomElement ], listIndex : (Seq[ Key ], Int) ) => listIndex match {
                    case (list, i) => {
                        DndWrappedListInner( DndWrappedListInner.Props(
                            IdUtils.generateDroppableId( props.listId, i ),
                            list,
                            props.renderer,
                            props.maxWidth,
                            props.refs,
                            props.listClass,
                        ) ).vdomElement +: DndWrappedListInner( DndWrappedListInner.Props(
                            IdUtils.generateDroppableIdBefore( props.listId, i ),
                            Seq(),
                            props.renderer,
                            props.maxWidth,
                            props.refs,
                            props.listClass,
                        ) ).vdomElement +: vdEles
                    }
                    case _ => vdEles
                } ) ).reverse
                eleList.toVdomArray
            }
        }
    }

    private val build = ScalaComponent.builder[ Props ]
      .initialState( State( false ) )
      .renderBackend[ Backend ]
      .build

    def apply( props : Props ) : Unmounted[Props, State, Backend ] = build( props )
}
