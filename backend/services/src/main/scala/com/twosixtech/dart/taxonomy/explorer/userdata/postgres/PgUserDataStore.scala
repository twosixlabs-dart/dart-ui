package com.twosixtech.dart.taxonomy.explorer.userdata.postgres

import com.twosixtech.dart.taxonomy.explorer.userdata.UserDataStore

import scala.concurrent.{ExecutionContext, Future}
import PgSlickProfile.api._
import Schema.{UserDataRow, userDataTableInsert, userDataTableQuery}

abstract class PgUserDataStore[ User, Data ](
  service: String,
  db : Database,
)(
  implicit ec : ExecutionContext
) extends UserDataStore[ User, Data ] {

  def marshalUser( user : User ) : String
  def unmarshalUser( user : String ) : User

  def marshalData( data : Data ) : String
  def unmarshalData( data : String ) : Data

  /**
   * Save data under a given key.
   *
   * @param data updated user state
   * @param key  user-defined identifier
   * @return unit
   */
  override def save( user: User, key: String, data: Data): Future[ Unit ] = {
    for {
      _ <- deleteKey( user, key )
      _ <- db.run( ( userDataTableInsert += ((service, marshalUser( user ), key, marshalData( data ))) ) )
    } yield {}
  }

  /**
   * @param user user-defined identifier
   * @return set of keys
   */
  override def getKeys(user: User): Future[Set[String]] = {
    db.run( userDataTableQuery.map( ( res: Schema.UserDataTable ) => res.key ).result )
      .map( _.toSet )
  }

  /**
   * Check whether data exists under a given key
   *
   * @param key user-defined identifier
   * @return boolean whether key exists
   */
  override def keyExists(user: User, key: String): Future[Boolean] = {
    db.run( userDataTableQuery.filter( v => v.key === key && v.userName === marshalUser( user ) ).exists.result )
  }

  /**
   * Retrieve data stored in a key
   *
   * @param key user-defined string identifier
   * @return Some( data ) if key exists otherwise None
   */
override def get(user: User, key: String): Future[Option[Data]] = {
  db.run(
    userDataTableQuery
    .filter( _.key === key )
    .filter( _.userName === marshalUser( user ) )
    .sortBy( _.timestamp.desc )
    .take( 1 )
    .map( _.data )
    .result
  ).map( _.headOption.map( v => unmarshalData( v ) ) )
}

  /**
   * Delete data stored in a key
   *
   * @param key user-defined string identifier
   * @return boolean whether or not key existed and was deleted
   */
  override def deleteKey(user: User, key: String): Future[Boolean] = {
    db.run(
      userDataTableQuery
        .filter( _.key === key )
        .filter( _.userName === marshalUser( user ) )
        .delete
    ).map( _ > 0 )
  }
}
