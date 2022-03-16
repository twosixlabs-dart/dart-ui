package com.twosixtech.dart.taxonomy.explorer.serialization

import io.circe.Decoder.decodeJson
import io.circe.generic.extras.ConfiguredJsonCodec
//import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json}
import io.circe.generic.extras._

import scala.util.{Failure, Success, Try}

object WmOntologyYmlJson {

    @ConfiguredJsonCodec
    case class WmOntologyNode(
        node : WmOntologyData,
    )

    object WmOntologyNode {
        implicit val useDefaultValues : Configuration = Configuration.default
    }


    @ConfiguredJsonCodec
    case class WmOntologyData(
        name : String,
        examples : Option[ Seq[ String ] ],
        descriptions : Option[ Seq[ String ] ],
        opposite : Option[ String ],
        polarity : Option[ Int ],
        patterns : Option[ Seq[ String ] ],
        @JsonKey("semantic type") semanticType : Option[ String ],
        children : Option[ Seq[ WmOntologyNode ] ]
    )

    object WmOntologyData {
        implicit val useDefaultValues : Configuration = Configuration.default
    }



}
