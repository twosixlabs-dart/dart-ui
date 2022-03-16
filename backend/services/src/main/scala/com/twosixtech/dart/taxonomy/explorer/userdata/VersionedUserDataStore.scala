package com.twosixtech.dart.taxonomy.explorer.userdata

import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartTaxonomyDI}

import scala.concurrent.Future

trait VersionedUserDataStore[ User, Data ] {

    /**
     * Save new version of data under a given key. Version number
     * auto-increments by 1, starting from 0.
     * @param data updated user state
     * @param key user-defined identifier
     * @return version number (1 if first version)
     */
    def save( user : User, key : String, data : Data ) : Future[ Int ]

    /**
     * @param user user-defined identifier
     * @return map of keys to number of versions
     */
    def getKeys( user : User ) : Future[ Map[ String, Int ] ]

    /**
     * Checks whether any versions of data exist in key. (Equivalent
     * to versionExists(key, 1)
     * @param key user-defined identifier
     * @return boolean whether any versions exist in key
     */
    def keyExists( user : User, key : String ) : Future[ Boolean ]

    /**
     * @param key user-defined string identifier
     * @return number of versions (latest version is res)
     */
    def countVersions( user : User, key : String ) : Future[ Int ]

    /**
     * Check existence of version of data stored in a key
     * @param key user-defined string identifier
     * @param version data version
     * @return whether or not version exists for key
     */
    def versionExists( user : User, key : String, version : Int ) : Future[ Boolean ]

    /**
     * Retrieve version of data stored in a key
     * @param key user-defined string identifier
     * @param version data version
     * @return version of data belonging to key
     */
    def getVersion( user : User, key : String, version : Int ) : Future[ Option[ Data ] ]

    /**
     * Retrieve highest version of data stored in a key
     * @param key user-defined string identifier
     * @return Some( data ) if version exists otherwise None
     */
    def getLatestVersion( user : User, key : String ) : Future[ Option[ Data ] ]

    /**
     * Retrieve version 1 of data stored in a key
     * @param key user-defined string identifier
     * @return Some( data ) if version exists otherwise None
     */
    def getOldestVersion( user : User, key : String ) : Future[ Option[ Data ] ]

    /**
     * Delete single version of data stored in a key
     * @param key user-defined string identifier
     * @param version data version
     * @return boolean whether or not version existed (and was deleted)
     */
    def deleteVersion( user : User, key : String, version : Int ) : Future[ Boolean ]

    def deleteOldestVersion( user : User, key : String ) : Future[ Boolean ]

    def deleteLatestVersion( user : User, key : String ) : Future[ Boolean ]

    /**
     * Delete all version of data stored in a key
     * @param key user-defined string identifier
     * @return boolean whether or not key existed (and was deleted)
     */
    def deleteKey( user : User, key : String ) : Future[ Boolean ]

    private val outer = this

    implicit class UserWithDataStore( user : User ) {
        def save( key : String, data : Data ) : Future[ Int ] = outer.save( user, key, data )

        def keyExists( key : String ) : Future[ Boolean ] = outer.keyExists( user, key )

        def countVersions( key : String ) : Future[ Int ] = outer.countVersions( user, key )

        def versionExists( key : String, version : Int ) : Future[ Boolean ] =
            outer.versionExists( user, key, version )

        def getVersion( key : String, version : Int ) : Future[ Option[ Data ] ] =
            outer.getVersion( user, key, version )

        def getLatestVersion( key : String ) : Future[ Option[ Data ] ] =
            outer.getLatestVersion( user, key )

        def getOldestVersion( key : String ) : Future[ Option[ Data ] ] =
            outer.getOldestVersion( user, key )

        def deleteVersion( key : String, version : Int ) : Future[ Boolean ] =
            outer.deleteVersion( user, key, version )

        def deleteOldestVersion( key : String ) : Future[ Boolean ] =
            outer.deleteOldestVersion( user, key )

        def deleteLatestVersion( key : String ) : Future[ Boolean ] =
            outer.deleteLatestVersion( user, key )

        def deleteKey( key : String ) : Future[ Boolean ] =
            outer.deleteKey( user, key )
    }
}
