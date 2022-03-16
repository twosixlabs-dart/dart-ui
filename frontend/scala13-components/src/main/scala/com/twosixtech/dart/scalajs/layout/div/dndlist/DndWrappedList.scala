package com.twosixtech.dart.scalajs.layout.div.dndlist

import com.twosixtech.dart.scalajs.layout.div.dndlist.DndWrappedListContext.{Context, ListId}
import com.twosixtech.dart.scalajs.layout.div.dndlist.utils.{ListUtils, MeasureUtils}
import japgolly.scalajs.react.Ref.Simple
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import org.scalajs.dom.html.Element

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

object DndWrappedList {
    type Key = String
    type ListClass = String

    case class Dimensions(
        h : Int,
        w : Int,
    )

    case class State(
        elemDimensions : Map[ Key, Dimensions ],
        dragging : Boolean,
        lists : Seq[ Seq[ Key ] ],
    )

    case class RendererParameters(
        key : Key,
        dragHandleProps : TagMod,
        containerWidth : Int,
        index : Int,
    )

    trait JsRenderParams extends js.Object {
        val key : String
        val dragHandleProps : js.Object
        val containerWidth : Int
        val index : Int
    }

    case class Props(
        listId : ListId,
        list : Seq[ Key ],
        context : DndWrappedListContext.Context,
        renderer : RendererParameters => VdomElement,
        maxWidth : Int,
        listClass : ListClass = "DEFAULT"
    )

    trait JsProps extends js.Object {
        val listId : String
        val list : js.Array[ String ]
        val context : Context
        val renderer : js.Function1[JsRenderParams, raw.React.Element]
        val maxWidth : Int
        val listClass : String
    }

    class Backend( $: BackendScope[ Props, State] ) {
        val refs = mutable.Map[ Key, Simple[ Element ] ]()

        def remeasure( key : Key ) : Callback = {
            MeasureUtils.getDimensions( key, refs( key ) ).flatMap { newDimensions =>
                $.asInstanceOf[ BackendScope[ Props, State ] ].modState( (state : State) => State( state.elemDimensions + (key -> newDimensions), state.dragging, state.lists ) )
            }
        }

        def render( props : Props, state : State ) : VdomElement = {
            props.list.foreach( k => if ( !refs.contains( k ) ) refs( k ) = Ref[ html.Element ] )

            DndWrappedListOuter( DndWrappedListOuter.Props(
                props.listId,
                state.lists,
                props.renderer,
                props.maxWidth,
                refs.toMap,
                props.listClass,
            ) )
        }
    }

    private val build = ScalaComponent.builder[ Props ]
      .initialState( State( Map[ Key, Dimensions ](), dragging = false, Seq() ) )
      .renderBackend[ Backend ]
      .componentDidMount( cdm => {
          cdm.props.context.updateLists(
              cdm.props.listId, ListUtils.breakUpLists( cdm.props.list, cdm.state.elemDimensions, cdm.props.maxWidth )
          )
      } )
      .componentDidUpdate( cdu => {
          ( cdu.currentProps.list.toIterable.foldLeft( CallbackTo( Map[ Key, Dimensions ]() ) ) { (agg, key) =>
            agg.flatMap { dimMap =>
                MeasureUtils.getDimensions( key, cdu.backend.refs( key ) ).map( newDim => dimMap + (key -> newDim) )
            }
          } flatMap { newDimMap =>
              val newLists = ListUtils.breakUpLists( cdu.currentProps.list, newDimMap, cdu.currentProps.maxWidth )
              if ( newDimMap != cdu.currentState.elemDimensions || newLists != cdu.currentState.lists ) {
                  cdu.modState( s => State( newDimMap, s.dragging, newLists ) ) >> {
                      if ( newLists != cdu.currentState.lists ) {
                          cdu.modState( state => State( newDimMap, state.dragging, newLists ) ) >> {
                              cdu.currentProps.context.updateLists( cdu.currentProps.listId, newLists )
                          }
                      } else Callback()
                  }
              } else Callback()
          } )
      } )
      .build

    def apply( props : Props ) : Unmounted[Props, State, Backend ] = build( props )

    private def convertProps( jsProps : JsProps ) : Props = {
        def renderer( renderParameters: RendererParameters ) : VdomElement = {
            val jsRenderParams = new JsRenderParams {
                override val key : String = renderParameters.key
                override val dragHandleProps : js.Object = renderParameters.dragHandleProps.toJs.props
                override val containerWidth : Int = renderParameters.containerWidth
                override val index : Int = renderParameters.index
            }
            val reactEle = jsProps.renderer( jsRenderParams )
            VdomElement( reactEle )
        }

        val res = Props( jsProps.listId, jsProps.list.toList, jsProps.context, renderer, jsProps.maxWidth, jsProps.listClass )
        res
    }

    @JSExportTopLevel( "DndWrappedList" )
    val exportJsComponent =
        build
          .cmapCtorProps[ JsProps ]( convertProps ) // Change props from JS to Scala
          .toJsComponent // Create a new, real JS component
          .raw // Leave the nice Scala wrappers behind and obtain the underlying JS value
}
