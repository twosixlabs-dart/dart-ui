package com.twosixtech.dart.taxonomy.explorer.userdata

import scala.concurrent.duration.{Duration, DurationInt}

object InMemoryVersionedVersionedUserDataStoreTest
  extends VersionedUserDataStoreTest[ String, String ](
      new InMemoryVersionedUserDataStore[ String, String ],
  ) {
    override def clearData( ) : Unit =
        testStore
          .asInstanceOf[ InMemoryVersionedUserDataStore[ String, String ] ]
          .userMap
          .clear()

    override def data1 : String = "data-1"

    override def data2 : String = "data-2"

    override def cmpData( d1: String, d2: String ): Boolean = d1 == d2

    override def user1 : String = "user-1"

    override def user2 : String = "user-2"

    override def timeout : Duration = 5.seconds
}

