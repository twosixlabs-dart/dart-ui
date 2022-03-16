package com.twosixtech.dart.taxonomy.explorer.models

case class NodeSimilarity(
    path : Seq[ String ],
    score : Double,
)

case class Cluster(
    id : String,
    score : Double,
    recommendedName : String,
    rankedWords : Seq[ String ],
    similarNodes : Seq[ NodeSimilarity ]
)
