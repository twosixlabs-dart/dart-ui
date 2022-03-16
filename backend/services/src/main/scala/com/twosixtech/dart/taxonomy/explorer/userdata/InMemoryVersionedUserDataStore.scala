package com.twosixtech.dart.taxonomy.explorer.userdata

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InMemoryVersionedUserDataStore[ User, Data ]
  extends VersionedUserDataStore[ User, Data ] {

    val userMap : mutable.Map[ User, mutable.Map[ String, ListBuffer[ Data ] ] ] =
        mutable.Map[ User, mutable.Map[ String, ListBuffer[ Data ] ] ]()

    implicit class VersionedListBuffer[ T ]( lb : ListBuffer[ T ] ) {
        def vOpt( i : Int ) : Option[ T ] = {
            if ( i == 0 ) throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
            else if ( i > 0 ) lb.lift( i - 1 )
            else {
                val newI = lb.length + i
                lb.lift( newI )
            }
        }

        def viOpt( i : Int ) : Option[ Int ] = {
            if ( i == 0 ) throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
            else if ( i > 0 && i <= lb.length ) Some( i - 1 )
            else if ( i > 0 ) None
            else if ( i < 0 && 0 - i <= lb.length ) Some( lb.length + i )
            else None
        }
    }

    /**
     * Save new version of data under a given key. Version number
     * auto-increments by 1, starting from 0.
     *
     * @param data updated user state
     * @param key  user-defined identifier
     * @return version number (1 if first version)
     */
    override def save( user : User, key : String, data : Data ) : Future[ Int ] = Future.successful {

        val dataMap = userMap.getOrElseUpdate( user, mutable.Map[ String, ListBuffer[ Data ] ]() )
        val dataList = dataMap.getOrElseUpdate( key, ListBuffer() )
        dataList.lastOption match {
            case None => dataList.append( data )
            case Some( latest ) if latest != data =>
                dataList.append( data )
            case _ => // do nothing if data has not changed since last version
        }
        dataList.length
    }


    /**
     * @param user user-defined identifier
     * @return map of keys to number of versions
     */
    override def getKeys( user : User ) : Future[ Map[ String, Int ] ] = Future.successful {
        userMap.get( user )
          .map( _.map { case (key, dataList) => key -> dataList.size } )
          .getOrElse( Map.empty )
          .filter( _._2 > 0 )
          .toMap
    }

    /**
     * Checks whether any versions of data exist in key. (Equivalent
     * to versionExists(key, 1)
     *
     * @param key user-defined identifier
     * @return boolean whether any versions exist in key
     */
    override def keyExists( user : User, key : String ) : Future[ Boolean ] = Future.successful {
        userMap.get( user ).exists( _.get( key ).exists( _.nonEmpty ) )
    }

    /**
     * @param key user-defined string identifier
     * @return number of versions (latest version is res)
     */
    override def countVersions( user : User, key : String ) : Future[ Int ] = Future.successful {
        userMap.get( user ).flatMap( _.get( key ).map( _.length ) ).getOrElse( 0 )
    }

    /**
     * Check existence of version of data stored in a key
     *
     * @param key     user-defined string identifier
     * @param version data version
     * @return whether or not version exists for key
     */
    override def versionExists( user : User, key : String, version : Int ) : Future[ Boolean ] = Future.successful {
        if ( version == 0 ) throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
        userMap.get( user ).flatMap( _.get( key ).map( _.vOpt( version ).isDefined ) ).getOrElse( false )
    }

    /**
     * Retrieve version of data stored in a key
     *
     * @param key     user-defined string identifier
     * @param version data version
     * @return version of data belonging to key
     */
    override def getVersion( user : User, key : String, version : Int ) : Future[ Option[ Data ] ] = Future {
        if ( version == 0 ) throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
        userMap.get( user ).flatMap( _.get( key ).flatMap( _.vOpt( version ) ) )
    }

    /**
     * Retrieve highest version of data stored in a key
     *
     * @param key user-defined string identifier
     * @return Some( data ) if version exists otherwise None
     */
    override def getLatestVersion( user : User, key : String ) : Future[ Option[ Data ] ] = Future {
        userMap.get( user ).flatMap( _.get( key ).flatMap( _.lastOption ) )
    }

    /**
     * Retrieve version 1 of data stored in a key
     *
     * @param key user-defined string identifier
     * @return Some( data ) if version exists otherwise None
     */
    override def getOldestVersion( user : User, key : String ) : Future[ Option[ Data ] ] = Future {
        userMap.get( user ).flatMap( _.get( key ).flatMap( _.headOption ) )
    }

    /**
     * Delete single version of data stored in a key
     *
     * @param key     user-defined string identifier
     * @param version data version
     * @return boolean whether or not version existed (and was deleted)
     */
    override def deleteVersion( user : User, key : String, version : Int ) : Future[ Boolean ] = Future {
        if ( version == 0 ) throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
        userMap.get( user ).flatMap( _.get( key ) ) match {
            case None => false
            case Some( lb ) =>
                lb.viOpt( version ) match {
                    case None =>
                        false
                    case Some( i ) =>
                        lb.remove( i )
                        true
                }
        }
    }

    override def deleteLatestVersion( user : User, key : String ) : Future[Boolean ] = Future {
        userMap.get( user ).flatMap( _.get( key ).map( lb => {
            if ( lb.isEmpty ) false
            else {
                lb.remove( lb.length - 1 )
                true
            }
        } ) ).getOrElse( false )
    }

    override def deleteOldestVersion( user : User, key : String ) : Future[ Boolean ] = Future {
        userMap.get( user ).flatMap( _.get( key ).map( lb => {
            if ( lb.isEmpty ) false
            else {
                lb.remove( 0 )
                true
            }
        } ) ).getOrElse( false )
    }

    /**
     * Delete all version of data stored in a key
     *
     * @param key user-defined string identifier
     * @return boolean whether or not key existed (and was deleted)
     */
    override def deleteKey( user : User, key : String ) : Future[ Boolean ] = Future {
        userMap.get( user ).exists( dataMap => {
            dataMap.remove( key ) match {
                case None => false
                case _ => true
            }
        } )
    }

}
