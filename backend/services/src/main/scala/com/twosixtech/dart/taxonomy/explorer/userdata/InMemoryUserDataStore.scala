package com.twosixtech.dart.taxonomy.explorer.userdata

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class InMemoryUserDataStore[ User, Data ]( implicit ec : ExecutionContext )
  extends UserDataStore[ User, Data ] {

  val userMap : mutable.Map[ User, mutable.Map[ String, Data ] ] =
    mutable.Map[ User, mutable.Map[ String, Data ] ]()

  /**
   * Save data under a given key.
   *
   * @param data updated user state
   * @param key  user-defined identifier
   * @return version number (1 if first version)
   */
  override def save( user: User, key: String, data: Data ): Future[ Unit ] =
    Future( userMap.get( user ) match {
      case None => userMap( user ) = mutable.Map[ String, Data ]( key -> data )
      case Some( keyMap ) => keyMap( key ) = data
    } )

  /**
   * @param user user-defined identifier
   * @return set of keys
   */
  override def getKeys( user: User ): Future[ Set[ String ] ] = Future {
    userMap.get( user ).toSet.flatMap( ( v : mutable.Map[ String, Data ] ) => v.keySet )
  }

  /**
   * Check whether data exists under a given key
   *
   * @param key user-defined identifier
   * @return boolean whether key exists
   */
  override def keyExists(user: User, key: String): Future[ Boolean ] = Future {
    userMap.exists( _._2.exists( _._1 == key ) )
  }

  /**
   * Retrieve data stored in a key
   *
   * @param key user-defined string identifier
   * @return Some( data ) if key exists otherwise None
   */
  override def get( user: User, key: String ): Future[ Option[ Data ] ] = Future {
    userMap.get( user ).flatMap( _.get( key ) )
  }

  /**
   * Delete data stored in a key
   *
   * @param key user-defined string identifier
   * @return boolean whether or not key existed and was deleted
   */
  override def deleteKey(user: User, key: String): Future[ Boolean ] = Future {
    userMap.get( user ) match {
      case None => false
      case Some( keyMap ) => keyMap.get( key ) match {
        case None => false
        case Some( _ ) =>
          keyMap -= key
          true
      }
    }
  }
}
