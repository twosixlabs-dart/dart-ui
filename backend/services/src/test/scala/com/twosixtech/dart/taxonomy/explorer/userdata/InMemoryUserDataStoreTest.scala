package com.twosixtech.dart.taxonomy.explorer.userdata

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}

object TestStore extends InMemoryUserDataStore[ String, String ]

object InMemoryUserDataStoreTest
  extends UserDataStoreTest[ String, String ](
    TestStore,
  ) {
  override def clearData(): Unit = TestStore.userMap.clear()

  override def data1: String = "test-data-1"

  override def data2: String = "test-data-2"

  override def cmpData(d1: String, d2: String): Boolean = d1 == d2

  override def user1: String = "test-user-1"

  override def user2: String = "test-user-2"

  override def timeout: Duration = 1.second
}
