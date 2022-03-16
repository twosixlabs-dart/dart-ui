package com.twosixtech.dart.taxonomy.explorer.userdata.postgres

import com.twosixlabs.dart.auth.user.DartUser
import com.twosixtech.dart.taxonomy.explorer.api.StateAccessApiDI

import scala.concurrent.{ExecutionContext, Future}
import PgSlickProfile.api._
import Schema.{UserDataRow, userDataTableInsert, userDataTableQuery}
import com.twosixtech.dart.taxonomy.explorer.userdata.VersionedUserDataStore
import upickle.default._

import scala.util.Try

trait PgVersionedUserDataStoreDI {
    this: StateAccessApiDI =>

    class PgVersionedUserDataStore(
        service: String,
        db : Database,
    )(
        implicit
        ec : ExecutionContext,
    ) extends VersionedUserDataStore[ DartUser, StateAccessApi.ConceptsState ] {

        private def versionCountQuery( user : DartUser, key : String ) = {
            userDataTableQuery.filter(
                v => v.service === service && v.userName === user.userName && v.key === key
            ).length
        }

        /**
         * Save new version of data under a given key. Version number
         * auto-increments by 1, starting from 1.
         *
         * @param data updated user state
         * @param key  user-defined identifier
         * @return version number (1 if first version)
         */
        override def save(
            user: DartUser,
            key: String,
            data: StateAccessApi.ConceptsState
        ): Future[ Int ] = {
            import StateAccessApi._

            def saveFn : Future[ Int ] = Future.fromTry( Try( data.marshalJson ) ) flatMap { dataJson =>
                db.run( ( userDataTableInsert += ((service, user.userName, key, data.marshalJson)) ).flatMap( _ =>
                    versionCountQuery( user, key ).result
                ) )
            }

            getLatestVersion( user, key ) flatMap {
                case None => saveFn
                case Some( `data` ) =>
                    countVersions( user, key )
                case _ => saveFn
            }
        }

        /**
         * @param user user-defined identifier
         * @return map of keys to number of versions
         */
        override def getKeys(
            user: DartUser
        ): Future[ Map[ String, Int ] ] = {
            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service )
                  .map( _.key )
                  .result
            ) map ( _.groupBy( v => v ).mapValues( _.size ) )
        }

        /**
         * Checks whether any versions of data exist in key. (Equivalent
         * to versionExists(key, 1)
         *
         * @param key user-defined identifier
         * @return boolean whether any versions exist in key
         */
        override def keyExists(
            user: DartUser,
            key: String
        ): Future[ Boolean ] = {
            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service )
                  .exists
                  .result
            )
        }

        /**
         * @param key user-defined string identifier
         * @return number of versions (latest version is res)
         */
        override def countVersions(
            user: DartUser,
            key: String
        ): Future[ Int ] = {
            db.run( versionCountQuery( user, key ).result )
        }

        /**
         * Check existence of version of data stored in a key
         *
         * @param key     user-defined string identifier
         * @param version data version
         * @return whether or not version exists for key
         */
        override def versionExists(
            user: DartUser,
            key: String,
            version: Int
        ): Future[ Boolean ] = {
            versionQuery( user, key, version ) flatMap { sortedQuery =>
                db.run(
                    sortedQuery
                      .take( Math.abs( version ) )
                      .length
                      .result
                ).map( v => {
                    v == Math.abs( version )
                }  )
            }
        }

        private def versionQuery(
            user: DartUser,
            key: String,
            version: Int
        ): Future[Query[Schema.UserDataTable, UserDataRow, Seq]] = Future {
            val filterQuery =
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )

            if ( version > 0 ) filterQuery.sortBy( _.timestamp.asc )
            else if ( version < 0 ) filterQuery.sortBy( _.timestamp.desc )
                 else throw new IllegalArgumentException( "version must be non-zero, positive or negative integer" )
        }

        /**
         * Retrieve version of data stored in a key
         *
         * @param key     user-defined string identifier
         * @param version data version
         * @return version of data belonging to key
         */
        override def getVersion(
            user: DartUser,
            key: String,
            version: Int
        ): Future[ Option[ StateAccessApi.ConceptsState ] ] = {
            import StateAccessApi._

            versionQuery( user, key, version ).flatMap { sortQuery =>
                db.run(
                    sortQuery
                      .drop( Math.abs( version ) - 1 )
                      .take( 1 )
                      .map( _.data )
                      .result
                ).map( _.headOption.map( _.unmarshalConceptsState ) )
            }
        }

        /**
         * Retrieve highest version of data stored in a key
         *
         * @param key user-defined string identifier
         * @return Some( data ) if version exists otherwise None
         */
        override def getLatestVersion(
            user: DartUser,
            key: String
        ): Future[ Option[ StateAccessApi.ConceptsState ] ] = {
            import StateAccessApi._

            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )
                  .sortBy( _.timestamp.desc )
                  .take( 1 )
                  .map( _.data )
                  .result
            ).map( _.headOption.map( _.unmarshalConceptsState ) )
        }

        /**
         * Retrieve version 1 of data stored in a key
         *
         * @param key user-defined string identifier
         * @return Some( data ) if version exists otherwise None
         */
        override def getOldestVersion(
            user: DartUser,
            key: String
        ): Future[ Option[ StateAccessApi.ConceptsState ] ] = {
            import StateAccessApi._

            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )
                  .sortBy( _.timestamp.asc )
                  .take( 1 )
                  .map( _.data )
                  .result
            ).map( _.headOption.map( _.unmarshalConceptsState ) )
        }

        /**
         * Delete single version of data stored in a key
         *
         * @param key     user-defined string identifier
         * @param version data version
         * @return boolean whether or not version existed (and was deleted)
         */
        override def deleteVersion(
            user: DartUser,
            key: String,
            version: Int
        ): Future[ Boolean ] = {

            versionQuery( user, key, version ).flatMap { sortQuery =>
                db.run(
                    sortQuery
                      .drop( Math.abs( version ) - 1 )
                      .map( _.id )
                      .result
                ).flatMap( rows => {
                    rows.headOption match {
                        case None => Future.successful( false )
                        case Some( id ) =>
                            db.run( userDataTableQuery.filter( _.id === id ).delete )
                              .map( _ == 1 )
                    }
                } )
            }
        }

        override def deleteOldestVersion(
            user: DartUser,
            key: String
        ): Future[ Boolean ] = {
            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )
                  .sortBy( _.timestamp.asc )
                  .take( 1 )
                  .map( _.id )
                  .result
            ).flatMap( rows => {
                rows.headOption match {
                    case None => Future.successful( false )
                    case Some( id ) =>
                        db.run( userDataTableQuery.filter( _.id === id ).delete )
                          .map( _ == 1 )
                }
            } )
        }

        override def deleteLatestVersion(
            user: DartUser,
            key: String
        ): Future[ Boolean ] = {
            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )
                  .sortBy( _.timestamp.desc )
                  .take( 1 )
                  .result
            ).flatMap( rows => {
                rows.headOption match {
                    case None => Future.successful( false )
                    case Some( UserDataRow( id, _, _, _, _, _ ) ) =>
                        db.run( userDataTableQuery.filter( _.id === id ).delete )
                          .map( _ == 1 )
                }
            } )
        }

        /**
         * Delete all version of data stored in a key
         *
         * @param key user-defined string identifier
         * @return boolean whether or not key existed (and was deleted)
         */
        override def deleteKey(
            user: DartUser,
            key: String
        ): Future[ Boolean ] = {
            db.run(
                userDataTableQuery
                  .filter( v => v.userName === user.userName && v.service === service && v.key === key )
                  .delete
            ).map( _ > 0 )
        }
    }

}
