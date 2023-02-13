package com.fetocan.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.reflect.KClass


private const val DB_NAME = "test"
private const val DB_USERNAME = "user"
private const val DB_PASSWORD = "password"

@Testcontainers
abstract class TestBase(
  vertx: Vertx,
  private val verticleClass: KClass<out Verticle>,
  private val deploymentOptions: DeploymentOptions = DeploymentOptions()
) : AsyncTest(vertx, verticleClass, deploymentOptions) {

  companion object {
    @Container
    private var postgresqlContainer: PostgreSQLContainer<Nothing> =
      PostgreSQLContainer<Nothing>("postgres:latest")
        .apply {
          withDatabaseName(DB_NAME)
          withUsername(DB_USERNAME)
          withPassword(DB_PASSWORD)
        }
  }

  override fun deployVerticle() = runBlocking(vertx.dispatcher()) {
    val config = JsonObject(
      mapOf(
        "server" to mapOf(
          "http.port" to TEST_PORT
        ),
        "db" to mapOf(
          "url" to postgresqlContainer.jdbcUrl,
          "host" to postgresqlContainer.host,
          "port" to postgresqlContainer.firstMappedPort,
          "database" to postgresqlContainer.databaseName,
          "username" to postgresqlContainer.username,
          "password" to postgresqlContainer.password,
        )
      )
    )
    deploymentOptions.config = config
    vertx.deployVerticle(
      verticleClass.java,
      deploymentOptions
    ).await()
  }
}
