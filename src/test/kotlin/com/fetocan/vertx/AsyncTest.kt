package com.fetocan.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.junit5.VertxExtension
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.reflect.KClass


@ExtendWith(VertxExtension::class)
abstract class AsyncTest(
  protected val vertx: Vertx,
  private val verticleClass: KClass<out Verticle>,
  private val deploymentOptions: DeploymentOptions = DeploymentOptions()
) {

  protected val httpClient: HttpClient = vertx.createHttpClient()
  private lateinit var deploymentId: String

  open fun deployVerticle(): String = runBlocking(vertx.dispatcher()) {
    vertx.deployVerticle(verticleClass.java, deploymentOptions).await()
  }

  @BeforeEach
  fun assignDeploymentId() {
    deploymentId = deployVerticle()
  }

  @AfterEach
  fun undeployVerticle(): Unit = runBlocking(vertx.dispatcher()) {
    vertx.undeploy(deploymentId).await()
  }

  //for integration test
  protected fun runTest(block: suspend () -> Unit): Unit = runBlocking(vertx.dispatcher()) {
    block()
  }

  companion object {
    const val TEST_HOST = "localhost"
    const val TEST_PORT = 16699
  }
}
