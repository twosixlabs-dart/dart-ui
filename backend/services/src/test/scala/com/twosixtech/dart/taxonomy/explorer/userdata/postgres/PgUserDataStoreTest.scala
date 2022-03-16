//package com.twosixtech.dart.taxonomy.explorer.userdata.postgres
//
//import com.twosixtech.dart.taxonomy.explorer.userdata.UserDataStoreTest
//import org.scalatest.flatspec.AnyFlatSpecLike
//import PgSlickProfile.api._
//import com.mchange.v2.c3p0.ComboPooledDataSource
//import com.twosixtech.dart.taxonomy.explorer.userdata.postgres.Schema.userDataTableQuery
//import com.typesafe.config.{Config, ConfigFactory}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration.{Duration, DurationInt}
//import scala.language.postfixOps
//import scala.util.Try
//
//object TestStoreParameters {
//      private val config: Config = ConfigFactory.load( "test.conf" )
//
//      private val ds = new ComboPooledDataSource()
//      ds.setDriverClass( config.getString( "postgres.driver.class" ) )
//      private val pgHost = config.getString( "postgres.host" )
//      private val pgPort = config.getInt( "postgres.port" )
//      private val pgDb = config.getString( "postgres.database" )
//      ds.setJdbcUrl( s"jdbc:postgresql://$pgHost:$pgPort/$pgDb" )
//      ds.setUser( config.getString( "postgres.user" ) )
//      ds.setPassword( config.getString( "postgres.password" ) )
//      Try( config.getInt( "postgres.minPoolSize" )  ).foreach( v => ds.setMinPoolSize( v ) )
//      Try( config.getInt( "postgres.acquireIncrement" )  ).foreach( v => ds.setAcquireIncrement( v ) )
//      Try( config.getInt( "postgres.maxPoolSize" )  ).foreach( v => ds.setMaxPoolSize( v ) )
//
//      private val maxConns = Try( config.getInt( "postgres.max.connections" ) ).toOption
//
//      val testDb : Database = Database.forDataSource( ds, maxConns )
//}
//
//object TestStore
//  extends PgUserDataStore[ String, String ]( "test-service", TestStoreParameters.testDb ) {
//  override def marshalUser(user: String): String = user
//  override def unmarshalUser(user: String): String = user
//  override def marshalData(data: String): String = data
//  override def unmarshalData(data: String): String = data
//}
//
//object PgUserDataStoreTest
//  extends UserDataStoreTest[ String, String ](
//    TestStore
//  ) {
//  import asyncUtils.AwaitableFuture
//
//  override def clearData(): Unit = {
//    TestStoreParameters.testDb.run(
//      userDataTableQuery.schema.truncate
//    ).awaitWrite
//  }
//  override def data1: String = "test-data-1"
//  override def data2: String = "test-data-2"
//  override def cmpData( d1: String, d2: String ): Boolean = d1 == d2
//  override def user1: String = "unique-test-user-1-dafgjiskgjegewfn"
//  override def user2: String = "unique-test-user-2-ufnwkjefeirfjsld"
//  override def timeout: Duration = 10 seconds
//}
