package com.twosixtech.dart.taxonomy.explorer.userdata

import com.twosixtech.dart.taxonomy.explorer.utils.AsyncUtils
import utest._

import scala.concurrent.duration.{Duration, DurationInt}

abstract class VersionedUserDataStoreTest[ User, Data ](
   val testStore: VersionedUserDataStore[ User, Data ],
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
            test( "countVersions of a non-existent key returns 0" ) {
                assert( testStore.countVersions( user1, key1 ).await == 0 )
            }

            test( "versionExists(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                assert {
                    intercept[ IllegalArgumentException ]( testStore.versionExists( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "getVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                assert {
                    intercept[ IllegalArgumentException ]( testStore.getVersion( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "versionExists(user,key, ?) returns false for any non-zero integer version argument" ) {
                ( Range( 1, 20 ) ++ Range( 20, 200, 20 ) ) foreach { i =>
                    assert( !testStore.versionExists( user1, key1, i ).await )
                }
            }

            test( "getVersion(user,key, ?) returns None for any non-zero integer version argument" ) {
                ( Range( 1, 20 ) ++ Range( 20, 200, 20 ) ) foreach { i =>
                    assert( testStore.getVersion( user1, key1, i ).await.isEmpty )
                }
            }

            test( "getOldestVersion(user,key) returns None" ) {
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
            }

            test( "getLatestVersion(user,key) returns None" ) {
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
            }

            test( "keyExists(user,key) returns false" ) {
                assert( !testStore.keyExists( user1, key1 ).await )
            }

        }

        test( "Saving single version of state" ) {
            test( "saveState(user,key, state) returns 1") {
                assert( testStore.save( user1, key1, data1 ).await == 1 )
            }

            test( "countVersions returns 1" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.countVersions( user1, key1 ).await == 1 )
            }

            test( "versionExists(user,key,1) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.versionExists( user1, key1, 1 ).await )
            }

            test( "getVersion(user,key,1) returns saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.getVersion( user1, key1, 1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "versionExists(user,key,-1) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.versionExists( user1, key1, -1 ).await )
            }

            test( "getVersion(user,key,-1) returns saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.getVersion( user1, key1, -1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "versionExists(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ]( testStore.versionExists( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "getVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ]( testStore.getVersion( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "versionExists(user,key,?) returns false for all integers other than 1, 0, or -1" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                ( Range( 2, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( !testStore.versionExists( user1, key1, i ).await )
                }
            }

            test( "getVersion(user,key,?) returns None for all integers other than 1, 0, or -1" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                ( Range( 2, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( testStore.getVersion( user1, key1, i ).await.isEmpty )
                }
            }

            test( "getOldestVersion(user,key) returns saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "getLatestVersion(user,key) returns saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }
        }

        test( "Saving two versions of state" ) {
            test( "saveState(user,key, state1) returns 1 and then saveState(user,key, state2) returns 2") {
                assert( testStore.save( user1, key1, data1 ).awaitWrite == 1 )
                assert( testStore.save( user1, key1, data2 ).await == 2 )
            }

            test( "countVersions returns 2" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.countVersions( user1, key1 ).await == 2 )
            }

            test( "versionExists(user,key,1) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.versionExists( user1, key1, 1 ).await )
            }

            test( "getVersion(user,key,1) returns first saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getVersion( user1, key1, 1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "versionExists(user,key,2) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.versionExists( user1, key1, 2 ).await )
            }

            test( "getVersion(user,key,2) returns second saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getVersion( user1, key1, 2 ).await.exists( v => cmpData( v, data2 ) ) )
            }

            test( "versionExists(user,key,-1) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.versionExists( user1, key1, -1 ).await )
            }

            test( "getVersion(user,key,-1) returns second saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getVersion( user1, key1, -1 ).await.exists( v => cmpData( v, data2 ) ) )
            }

            test( "versionExists(user,key,-2) returns true" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.versionExists( user1, key1, -2 ).await )
            }

            test( "getVersion(user,key,-2) returns first saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getVersion( user1, key1, -2 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "versionExists(user,key,?) returns false for all integers less than -2 and more than 2" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                ( Range( 3, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( !testStore.versionExists( user1, key1, i ).await )
                }
            }

            test( "getVersion(user,key,?) returns None for all integers less than -2 and more than 2" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                ( Range( 3, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( testStore.getVersion( user1, key1, i ).await.isEmpty )
                }
            }

            test( "versionExists(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ]( testStore.versionExists( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "getVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ]( testStore.getVersion( user1, key1, 0 ).await )
                      .getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "getOldestVersion(user,key) returns first saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "getLatestVersion(user,key) returns second saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ) )
            }

            test( "saving identical data twice does not increment version (does nothing)" ) {
                assert( testStore.save( user1, key1, data1 ).awaitWrite == 1 )
                val resMap1 : Map[ String, Int ] = testStore.getKeys( user1 ).await
                assert( resMap1 == Map( key1 -> 1 ) )
                assert( testStore.save( user1, key1, data1 ).awaitWrite == 1 )
                val resMap2 : Map[ String, Int ] = testStore.getKeys( user1 ).await
                assert( resMap2 == Map( key1 -> 1 ) )
            }
        }

        test( "Trying to delete a version of a non-existent key" ) {
            test( "deleteVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer" ) {
                assert {
                    intercept[ IllegalArgumentException ](
                        testStore.deleteVersion( user1, key1, 0 ).await,
                    ).getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
            }

            test( "deleteVersion(user,key, ?) returns false for any positive or negative integer" ) {
                ( Range( 1, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( !testStore.deleteVersion( user1, key1, i ).await )
                }
            }

            test( "deleteOldestVersion(user,key) returns false" ) {
                assert( !testStore.deleteOldestVersion( user1, key1 ).await )
            }

            test( "deleteLatestVersion(user,key) returns false" ) {
                assert( !testStore.deleteLatestVersion( user1, key1 ).await )
            }
        }

        test( "Deleting a version of a key where only one version exists" ) {
            test( "deleteVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer, and then getLatestVersion(user,key) and getOldestVersion(user,key) return saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ](
                        testStore.deleteVersion( user1, key1, 0 ).await
                    ).getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteVersion(user,key,1) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.deleteVersion( user1, key1, 1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }

            test( "deleteVersion(user,key,-1) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.deleteVersion( user1, key1, -1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }

            test( "deleteVersion(user,key, ?) for version > 1 and < -1 returns false and then getLatestVersion(user,key) and getOldestVersion(user,key) return saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                ( Range( 2, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( !testStore.deleteVersion( user1, key1, i ).await )
                }
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteLatestVersion(user,key) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.deleteLatestVersion( user1, key1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }

            test( "deleteOldestVersion(user,key) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.deleteOldestVersion( user1, key1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }
        }

        test( "Deleting a version of a saved state where two versions exist" ) {
            test( "deleteVersion(user,key,0) returns IllegalArgumentException with a message explaining that version must be positive or negative integer, and then getLatestVersion(user,key) and getOldestVersion(user,key) return saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert {
                    intercept[ IllegalArgumentException ](
                        testStore.deleteVersion( user1, key1, 0 ).await
                    ).getMessage.contains( "version must be non-zero, positive or negative integer" )
                }
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteVersion(user,key,1) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return *second* saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.deleteVersion( user1, key1, 1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ) )
            }

            test( "deleteVersion(user,key,-1) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return *first* saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.deleteVersion( user1, key1, -1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteVersion(user,key, ?) for version > 1 and < -1 returns false and then getLatestVersion(user,key) returns second saved state and getOldestVersion(user,key) returns first saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                ( Range( 3, 20 ) ++ Range( 20, 200, 20 ) ).flatMap( v => Seq( v, 0 - v ) ) foreach { i =>
                    assert( !testStore.deleteVersion( user1, key1, i ).await )
                }
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteLatestVersion(user,key) returns true and then getLatestVersion(user,key) and getOldesVersion(user,key) return first saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.deleteLatestVersion( user1, key1 ).await )
                val res = testStore.getLatestVersion( user1, key1 ).await
                assert( res == Some( data1 ) )
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data1 ) ) )
            }

            test( "deleteOldestVersion(user,key) returns true and then getLatestVersion(user,key) and getOldestVersion(user,key) return second saved state" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.deleteOldestVersion( user1, key1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ))
                assert( testStore.getOldestVersion( user1, key1 ).await.exists( v => cmpData( v, data2 ) ))
            }
        }

        test( "Deleting keys" ) {
            test( "deleteKey(user,key) for non-existent key returns false" ) {
                assert( !testStore.deleteKey( user1, key1 ).await )
            }

            test( "deleteKey(user,key) for key with one version returns true and getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.deleteKey( user1, key1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }

            test( "deleteKey(user,key) for key with multiple versions returns true and getLatestVersion(user,key) and getOldestVersion(user,key) return None and keyExists(user,key) returns false" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.deleteKey( user1, key1 ).await )
                assert( testStore.getLatestVersion( user1, key1 ).await.isEmpty )
                assert( testStore.getOldestVersion( user1, key1 ).await.isEmpty )
                assert( !testStore.keyExists( user1, key1 ).await )
            }
        }

        test( "getKeys" ) {
            test( "getKeys(user) returns an empty map when there are no versions of any key" ) {
                assert( testStore.getKeys( user1 ).await == Map.empty[ String, Int ] )
            }

            test( "getKeys(user) returns a map of keys to the number of versions in that key" ) {
                testStore.save( user1, key1, data1 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 1 ) )
                testStore.save( user1, key1, data2 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 2 ) )
                testStore.save( user1, key2, data1 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 2, key2 -> 1 ) )
                testStore.save( user1, key2, data2 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 2, key2 -> 2 ) )
                testStore.deleteLatestVersion( user1, key1 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 1, key2 -> 2 ) )
                testStore.deleteKey( user1, key2 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map( key1 -> 1 ) )
                testStore.deleteLatestVersion( user1, key1 ).awaitWrite
                assert( testStore.getKeys( user1 ).await == Map.empty[ String, Int ] )
            }
        }

    }

}
