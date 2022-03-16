//package com.twosixtech.dart.scalajs.layout.css.palette
//
//import com.twosixtech.dart.scalajs.layout.css.builder.WithStyles
//import com.twosixtech.dart.scalajs.layout.css.palette.DartStylesPalette.Classes
//import japgolly.scalajs.react.component.Scala.{Unmounted, builder}
//import scalacss.DevDefaults._
//import scalacss.internal.mutable.StyleSheet
//
//trait DartStylesSheet extends StyleSheet.Inline {
//    val fullHeight : StyleA
//    val fullWidth : StyleA
//    val thinPadding : StyleA
//    val medPadding : StyleA
//    val thickPadding : StyleA
//    val thinMargin : StyleA
//    val medMargin : StyleA
//    val thickMargin : StyleA
//}
//
//class DartStylesPaletteProvider( dartStyles : DartStylesSheet )
//    extends DartStylesPalette[ Unit ]
//      with WithStyles {
//    override type BackendType = Unit
//
//    override val styles : StyleSheet.Inline = dartStyles
//
//    val classes = Classes(
//        dartStyles.fullHeight.htmlClass,
//        dartStyles.fullWidth.htmlClass,
//        dartStyles.thinPadding.htmlClass,
//        dartStyles.medPadding.htmlClass,
//        dartStyles.thickPadding.htmlClass,
//        dartStyles.thinMargin.htmlClass,
//        dartStyles.medMargin.htmlClass,
//        dartStyles.thickMargin.htmlClass,
//    )
//
//    val component = builder[ DartStylesPalette.Props ]
//      .render_P( props => {
//          withStyles {
//              props.render( classes )
//          }
//      } )
//      .build
//
//    override def apply( props : DartStylesPalette.Props ) : Unmounted[ DartStylesPalette.Props, Unit, Unit ] = {
//        component( props )
//    }
//}
