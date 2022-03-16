package com.twosixtech.dart.taxonomy.explorer.userdata.postgres

import PgSlickProfile.api._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.SqlType

import java.sql.Timestamp
import java.time.{LocalDateTime, OffsetDateTime}


object Schema {

    case class UserDataRow( id : Int, service : String, userName : String, key : String, data : String, timestamp : OffsetDateTime )

    class UserDataTable( tag : Tag ) extends Table[ UserDataRow ]( tag : Tag,"user_data" ) {

        def id : Rep[ Int ] = column[ Int ]( "id", O.PrimaryKey, O.AutoInc )

        def service : Rep[ String ] = column[ String ]( "service" )

        def userName : Rep[ String ] = column[ String ]( "user_name" )

        def key : Rep[ String ] = column[ String ]( "key" )

        def data : Rep[ String ] = column[ String ]( "data" )

        def timestamp : Rep[ OffsetDateTime ] = column[ OffsetDateTime ]( "ts", SqlType("timestamp not null default CURRENT_TIMESTAMP") )

        override def * : ProvenShape[ UserDataRow ] =
            (id, service, userName, key, data, timestamp) <>
              (UserDataRow.tupled, UserDataRow.unapply)

    }

    val userDataTableQuery: TableQuery[ UserDataTable ] = TableQuery[ UserDataTable ]

    val userDataTableInsert = userDataTableQuery.map( t => (t.service, t.userName, t.key, t.data) )
}


