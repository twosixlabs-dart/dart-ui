package com.twosixtech.dart.scalajs.layout

package object types {

    sealed trait Color
    sealed trait ThemeColor extends Color
    case object Primary extends ThemeColor
    case object Secondary extends ThemeColor
    case object Plain extends ThemeColor

    sealed trait Size
    sealed trait BasicSize extends Size
    sealed trait MoreSize extends Size
    case object Tiny extends Size
    case object ExtraSmall extends MoreSize
    case object Small extends BasicSize with MoreSize
    case object Medium extends BasicSize with MoreSize
    case object Large extends BasicSize with MoreSize
    case object ExtraLarge extends MoreSize
    case object Giant extends Size

    sealed trait Alignment
    case object AlignStart extends Alignment
    case object AlignCenter extends Alignment
    case object AlignEnd extends Alignment
    case object AlignStretch extends Alignment

    sealed trait Justification
    case object JustifyStart extends Justification
    case object JustifyEnd extends Justification
    case object JustifyCenter extends Justification
    case object JustifySpacedEvenly extends Justification
    case object JustifySpacedAround extends Justification
    case object JustifySpacedBetween extends Justification

    sealed trait RelativePlacement
    trait Above extends RelativePlacement
    trait Below extends RelativePlacement
    trait Left extends RelativePlacement
    trait Right extends RelativePlacement
    trait Middle extends RelativePlacement
    case object AboveMiddle extends Above with Middle
    case object AboveRight extends Above with Right
    case object RightSide extends Right with Middle
    case object BelowRight extends Below with Right
    case object BelowMiddle extends Below with Middle
    case object BelowLeft extends Below with Left
    case object LeftSide extends Left with Middle
    case object AboveLeft extends Above with Left

    sealed trait Direction
    case object Row extends Direction
    case object Column extends Direction

}
