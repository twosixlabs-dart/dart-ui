package com.twosixtech.dart.taxonomy.explorer.models.wm

import com.twosixtech.dart.taxonomy.explorer.models.ConceptDeps

import scala.util.matching.Regex

sealed trait Polarity
case object Positive extends Polarity
case object Negative extends Polarity

sealed trait SemanticType
case object Entity extends SemanticType
case object Event extends SemanticType
case object Property extends SemanticType

case class WmConceptMetadata(
    examples : Set[ String ],
    patterns : Set[ String ],
    opposite : Seq[ String ],
    descriptions : Seq[ String ],
    polarity : Polarity,
    semanticType: SemanticType,
)

trait WmConceptDI extends ConceptDeps {
    override type ConceptMetadataType = Option[ WmConceptMetadata ]
}
