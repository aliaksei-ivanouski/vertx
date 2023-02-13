package com.fetocan.vertx.global

import io.github.zero88.jooqx.Jooqx
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions
import org.flywaydb.core.Flyway
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration

fun pgPool(
  vertx: Vertx,
  config: JsonObject
): PgPool {
  val dbConfig = config.getJsonObject("db")
  val connectOptions = PgConnectOptions()
    .setPort(dbConfig.getInteger("port"))
    .setHost(dbConfig.getString("host"))
    .setDatabase(dbConfig.getString("database"))
    .setUser(dbConfig.getString("username"))
    .setPassword(dbConfig.getString("password"))

  // Pool Options
  val poolOptions = PoolOptions().setMaxSize(10)

  // Create the pool from the data object
  return PgPool.pool(vertx, connectOptions, poolOptions)
}

fun jooqx(
  vertx: Vertx,
  config: JsonObject
) = Jooqx.builder()
  .setVertx(vertx)
  .setDSL(
    DSL.using(
      DefaultConfiguration()
        .set(SQLDialect.POSTGRES)
    )
  )
  .setSqlClient(pgPool(vertx, config))
  .build()

fun flyway(
  config: JsonObject
): Flyway {
  val dbConfig = config.getJsonObject("db")
  return Flyway.configure()
    .dataSource(
      dbConfig.getString("url"),
      dbConfig.getString("username"),
      dbConfig.getString("password")
    )
    .locations("classpath:db/migration")
    .baselineOnMigrate(true)
    .load()
}


