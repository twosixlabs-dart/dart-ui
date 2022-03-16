package com.twosixtech.dart.scalajs.layout.div.dndlist

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedList.{Key, ListClass, RendererParameters}
import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedListContext.ListId
import japgolly.scalajs.react.Ref.Simple
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html
import react.beautifuldnd.{Direction, Draggable, Droppable}

object DndWrappedListInner {

    case class Props(
        id : ListId,
        list : Seq[ Key ],
        renderer : RendererParameters => VdomElement,
        maxWidth : Int,
        refs : Map[ Key, Simple[ html.Element ]],
        listClass : ListClass,
    )

    def getItemStyle( isDragging: Boolean, draggableStyle: TagMod ): TagMod = TagMod(
        draggableStyle,
    )

    class Backend( $ : BackendScope[ Props, Unit ] ) {
        def render( props : Props ) : VdomElement = {
            def getListStyle( isDraggingOver: Boolean ): TagMod = TagMod(
                ^.width := s"${props.maxWidth}px",
                ^.margin := "10px",
                ^.display := "flex",
                ^.overflow := "hidden",
            )

            Droppable( props.id, direction = Direction.Horizontal.asInstanceOf[ Direction ], tpe = props.listClass ) { case ( droppableProvided, droppableSnapshot ) =>
                <.div(
                    droppableProvided.innerRef,
                    droppableProvided.droppableProps,
                    getListStyle( droppableSnapshot.isDraggingOver )
                )(
                    props.list.zipWithIndex.toTagMod { case ( key : Key, index : Int ) =>
                        Draggable( key, index ) { case (draggapleProvided, draggableSnapshot, rubric) =>
                            <.div(
                                draggapleProvided.innerRef,
                                draggapleProvided.draggableProps,
                                getItemStyle( draggableSnapshot.isDragging, draggapleProvided.draggableStyle ),
                            )(
                                <.div( props.renderer( RendererParameters( key, draggapleProvided.dragHandleProps, props.maxWidth, index ) ) )
                                  .withRef( props.refs( key ) )
                            )
                        }
                    },
                    droppableProvided.placeholder
                )
            }
        }
    }

    private val build = ScalaComponent.builder[ Props ]
      .renderBackend[ Backend ]
      .build

    def apply( props : Props ) : Unmounted[Props, Unit, Backend ] = build( props )
}
