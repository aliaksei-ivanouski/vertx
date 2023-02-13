package com.fetocan.vertx

import com.fetocan.vertx.metrics.configureMetricsOptions
import com.fetocan.vertx.util.LoggerDelegate
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.impl.launcher.VertxCommandLauncher
import io.vertx.core.impl.launcher.VertxLifecycleHooks
import io.vertx.core.json.JsonObject

class AppLauncher : VertxCommandLauncher(), VertxLifecycleHooks {

  companion object {
    private val logger by LoggerDelegate()

    @JvmStatic
    fun main(args: Array<String>) {
      logger.info("Arguments: ${args.joinToString(separator = " ") { it }}")
      AppLauncher().dispatch(args)
    }
  }

  override fun beforeStartingVertx(
    options: VertxOptions
  ) {
    logger.info("Configuring metrics options...")
    configureMetricsOptions(options)
  }

  override fun handleDeployFailed(
    vertx: Vertx?,
    mainVerticle: String?,
    deploymentOptions: DeploymentOptions?,
    cause: Throwable?
  ) {
    logger.info("Deploy vertx failed...")
    vertx?.close()
  }

  override fun afterStoppingVertx() {}

  override fun afterConfigParsed(config: JsonObject?) {}

  override fun afterStartingVertx(vertx: Vertx?) {}

  override fun beforeStoppingVertx(vertx: Vertx?) {}

  override fun beforeDeployingVerticle(deploymentOptions: DeploymentOptions?) {}
}


