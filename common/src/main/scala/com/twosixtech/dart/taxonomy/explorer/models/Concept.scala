package com.twosixtech.dart.taxonomy.explorer.models

case class Concept[ T ](
    name : String,
    children : Set[ Concept[ T ] ],
    metadata : T
)
