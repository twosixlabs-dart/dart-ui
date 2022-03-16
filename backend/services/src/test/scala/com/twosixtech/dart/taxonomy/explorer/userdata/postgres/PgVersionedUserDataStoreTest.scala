//package com.twosixtech.dart.taxonomy.explorer.userdata.postgres
//
//import com.mchange.v2.c3p0.ComboPooledDataSource
//import com.twosixlabs.dart.auth.user.DartUser
//import com.twosixtech.dart.taxonomy.explorer.api.{RootApiDI, StateAccessApiDI}
//import com.twosixtech.dart.taxonomy.explorer.models.wm.WmDartClusterConceptBridgeDI
//import com.twosixtech.dart.taxonomy.explorer.models.{CuratedClusterDI, DartClusterDI, DartTaxonomyDI, DartTaxonomyTestDataDI, UUIDTaxonomyIdDI, UUIDTaxonomyIdSerializationDI, WmDartConceptDI}
//import com.twosixtech.dart.taxonomy.explorer.serialization.WmDartSerializationDI
//import com.typesafe.config.{Config, ConfigFactory}
//import PgSlickProfile.api._
//import Schema.userDataTableQuery
//import com.twosixtech.dart.taxonomy.explorer.userdata.UserDataStoreTest
//
//import java.util.concurrent.TimeUnit
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration.Duration
//import scala.util.Try
//
//
//object PgDataStoreProvider
//  extends PgUserDataStoreDI
//    with StateAccessApiDI
//    with DartTaxonomyDI
//    with CuratedClusterDI
//    with WmDartClusterConceptBridgeDI
//    with WmDartConceptDI
//    with DartClusterDI
//    with WmDartSerializationDI
//    with UUIDTaxonomyIdDI
//    with UUIDTaxonomyIdSerializationDI
//    with DartTaxonomyTestDataDI
//    with RootApiDI {
//
//    private val config: Config = ConfigFactory.load( "test.conf" )
//
//    private val ds = new ComboPooledDataSource()
//    ds.setDriverClass( config.getString( "postgres.driver.class" ) )
//    private val pgHost = config.getString( "postgres.host" )
//    private val pgPort = config.getInt( "postgres.port" )
//    private val pgDb = config.getString( "postgres.database" )
//    ds.setJdbcUrl( s"jdbc:postgresql://$pgHost:$pgPort/$pgDb" )
//    ds.setUser( config.getString( "postgres.user" ) )
//    ds.setPassword( config.getString( "postgres.password" ) )
//    Try( config.getInt( "postgres.minPoolSize" )  ).foreach( v => ds.setMinPoolSize( v ) )
//    Try( config.getInt( "postgres.acquireIncrement" )  ).foreach( v => ds.setAcquireIncrement( v ) )
//    Try( config.getInt( "postgres.maxPoolSize" )  ).foreach( v => ds.setMaxPoolSize( v ) )
//
//    private val maxConns = Try( config.getInt( "postgres.max.connections" ) ).toOption
//
//    val testDb : Database = Database.forDataSource( ds, maxConns )
//
//}
//
//import PgDataStoreProvider.{testDb, PgUserDataStore, StateAccessApi, DartTaxonomyData}
//
//object PgUserDataStoreTest
//  extends UserDataStoreTest[ DartUser, StateAccessApi.ConceptsState ](
//      new PgUserDataStore( "test-service", testDb )
//  ) {
//
//    Await.result( testDb.run( userDataTableQuery.schema.dropIfExists ), Duration.Inf )
//    Await.result( testDb.run( userDataTableQuery.schema.create ), Duration.Inf )
//
//    import StateAccessApi.ConceptsState
//
//    override def clearData( ): Unit = Await.result( testDb.run( userDataTableQuery.schema.truncate ), Duration.Inf )
//
//    override def data1: ConceptsState = ConceptsState( DartTaxonomyData.taxonomy, None )
//
//    private val tax2 = DartTaxonomyData.taxonomy
//      .removeConcept( Seq( DartTaxonomyData.concept1.name, DartTaxonomyData.concept1b.name, DartTaxonomyData.concept1b1.name ) ).get
//      .removeConcept( Seq( DartTaxonomyData.concept3.name ) ).get
//
//    override def data2: ConceptsState = ConceptsState( tax2, None )
//
//    override def cmpData(
//        d1: PgDataStoreProvider.StateAccessApi.ConceptsState,
//        d2: PgDataStoreProvider.StateAccessApi.ConceptsState
//    ): Boolean = d1.marshalJson == d2.marshalJson
//
//    override def user1: DartUser = DartUser( "user-1", Set.empty )
//
//    override def user2: DartUser = DartUser( "user-2", Set.empty )
//
//    override def writeWait: Duration = Duration( 50, TimeUnit.MILLISECONDS )
//
//    override def timeout: Duration = Duration( 10, TimeUnit.SECONDS )
//}
