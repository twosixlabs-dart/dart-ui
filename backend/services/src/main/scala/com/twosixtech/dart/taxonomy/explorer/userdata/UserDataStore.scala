package com.twosixtech.dart.taxonomy.explorer.userdata

import scala.concurrent.Future

trait UserDataStore[ User, Data ] {

  /**
   * Save data under a given key.
   * @param data updated user state
   * @param key user-defined identifier
   * @return unit
   */
  def save( user : User, key : String, data : Data ) : Future[ Unit ]

  /**
   * @param user user-defined identifier
   * @return set of keys
   */
  def getKeys( user : User ) : Future[ Set[ String ] ]

  /**
   * Check whether data exists under a given key
   * @param key user-defined identifier
   * @return boolean whether key exists
   */
  def keyExists( user : User, key : String ) : Future[ Boolean ]

  /**
   * Retrieve data stored in a key
   * @param key user-defined string identifier
   * @return Some( data ) if key exists otherwise None
   */
  def get( user : User, key : String ) : Future[ Option[ Data ] ]

  /**
   * Delete data stored in a key
   * @param key user-defined string identifier
   * @return boolean whether or not key existed and was deleted
   */
  def deleteKey( user : User, key : String ) : Future[ Boolean ]

  private val outer = this

  implicit class UserWithDataStore( user : User ) {
    def save( key : String, data : Data ) : Future[ Unit ] = outer.save( user, key, data )

    def getKeys : Future[ Set[ String ] ] = outer.getKeys( user )

    def keyExists( key : String ) : Future[ Boolean ] = outer.keyExists( user, key )

    def get( key : String ) : Future[ Option[ Data ] ] =
      outer.get( user, key )

    def deleteKey( key : String ) : Future[ Boolean ] =
      outer.deleteKey( user, key )
  }
}

