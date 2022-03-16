package com.twosixtech.dart.taxonomy.explorer.userdata

import com.twosixtech.dart.taxonomy.explorer.utils.AsyncUtils
import utest._

import scala.concurrent.duration.{Duration, DurationInt}

abstract class UserDataStoreTest[ User, Data ](
    val testStore: UserDataStore[ User, Data ],
) extends TestSuite {

  // Abstract methods and test data
  def clearData() : Unit
  def data1 : Data
  def data2 : Data
  def cmpData( d1 : Data, d2 : Data ) : Boolean
  def user1 : User
  def user2 : User
  def timeout : Duration
  def writeWait : Duration = 0.milliseconds

  // Concrete test data
  final val key1 : String = "key-1"
  final val key2 : String = "key-2"

  val asyncUtils = new AsyncUtils( timeout, writeWait )
  import asyncUtils.AwaitableFuture

  override def utestBeforeEach( path : Seq[ String ] ) : Unit = {
    clearData()
  }

  override val tests = Tests {
    test( "Coherent test data" ) {
      assert( data1 != data2 )
    }

    test( "Reading without saved state" ) {
      test( "get of a non-existent key returns 0" ) {
        assert( testStore.get( user1, key1 ).await.isEmpty )
      }

      test( "keyExists(user,key) returns false" ) {
        assert( !testStore.keyExists( user1, key1 ).await )
      }
    }

    test( "Saving state to empty key" ) {
      test( "able to retrieve state" ) {
        assert( testStore.get( user1, key1 ).await.isEmpty )
        testStore.save( user1, key1, data1 ).awaitWrite
        assert( testStore.get( user1, key1 ).await.contains( data1 ) )
      }

      test( "key exists returns true" ) {
        assert( !testStore.keyExists( user1, key1 ).await )
        testStore.save( user1, key1, data1 ).awaitWrite
        assert( testStore.keyExists( user1, key1 ).await )
      }
    }

    test( "Saving state to non-empty key" ) {
      test( "get retrieves newer state" ) {
        assert( testStore.get( user1, key1 ).await.isEmpty )
        testStore.save( user1, key1, data1 ).awaitWrite
        assert( testStore.get( user1, key1 ).await.contains( data1 ) )
        testStore.save( user1, key1, data2 ).awaitWrite
        assert( testStore.get( user1, key1 ).await.contains( data2 ) )
      }

      test( "key exists returns true" ) {
        assert( !testStore.keyExists( user1, key1 ).await )
        testStore.save( user1, key1, data1 ).awaitWrite
        assert( testStore.keyExists( user1, key1 ).await )
        testStore.save( user1, key1, data2 ).awaitWrite
        assert( testStore.keyExists( user1, key1 ).await )
      }
    }

    test( "Deleting keys" ) {
      test( "returns false if key does not exist" ) {
        assert( !testStore.keyExists( user1, key1 ).await )
        assert( testStore.get( user1, key1 ).await.isEmpty )
        assert( !testStore.deleteKey(user1, key1).awaitWrite )
        assert( !testStore.keyExists( user1, key1 ).await )
        assert( testStore.get( user1, key1 ).await.isEmpty )
      }

      test( "returns true if key exists and get now returns none and keyExists now returns false" ) {
        assert( !testStore.keyExists( user1, key1 ).await )
        assert( testStore.get( user1, key1 ).await.isEmpty )
        assert( testStore.save(user1, key1, data1 ).awaitWrite == {} )
        assert( testStore.keyExists( user1, key1 ).await )
        assert( testStore.get( user1, key1 ).await.contains( data1 ) )
        assert( testStore.deleteKey(user1, key1).awaitWrite )
        assert( !testStore.keyExists( user1, key1 ).await )
        assert( testStore.get( user1, key1 ).await.isEmpty )
      }
    }

    test( "getKeys" ) {
      test( "getKeys(user) returns an empty set when there are no keys" ) {
        assert( testStore.getKeys( user1 ).await == Set.empty[ String ] )
      }

      test( "getKeys(user) returns a set of keys" ) {
        testStore.save( user1, key1, data1 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set( key1 ) )
        testStore.save( user1, key1, data2 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set( key1 ) )

        testStore.save( user1, key2, data1 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set( key1, key2 ) )
        testStore.save( user1, key2, data2 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set( key1, key2 ) )

        testStore.deleteKey( user1, key1 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set( key2 ) )

        testStore.deleteKey( user1, key2 ).awaitWrite
        assert( testStore.getKeys( user1 ).await == Set.empty[ String ] )
      }
    }

  }

}
