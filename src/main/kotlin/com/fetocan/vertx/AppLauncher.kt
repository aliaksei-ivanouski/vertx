package com.fetocan.vertx

import com.fetocan.vertx.metrics.configureMeters
import com.fetocan.vertx.util.LoggerDelegate
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.impl.launcher.VertxCommandLauncher
import io.vertx.core.impl.launcher.VertxLifecycleHooks
import io.vertx.core.json.JsonObject
import io.vertx.micrometer.Label
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.micrometer.VertxPrometheusOptions
import java.util.EnumSet

class AppLauncher : VertxCommandLauncher(), VertxLifecycleHooks {

  companion object {
    val logger by LoggerDelegate()

    @JvmStatic
    fun main(args: Array<String>) {
      logger.info("Arguments: ${args.joinToString(separator = " ") { it }}")
      AppLauncher().dispatch(args)
    }
  }

  override fun beforeStartingVertx(options: VertxOptions) {
    options.metricsOptions = MicrometerMetricsOptions()
      .setMicrometerRegistry(configureMeters())
      .setPrometheusOptions(VertxPrometheusOptions().setEnabled(true))
      .setLabels(
        EnumSet.of(
          Label.HTTP_PATH,
          Label.HTTP_METHOD,
          Label.HTTP_CODE
        )
      )
      .setEnabled(true)
  }

  override fun handleDeployFailed(
    vertx: Vertx?,
    mainVerticle: String?,
    deploymentOptions: DeploymentOptions?,
    cause: Throwable?
  ) {
    vertx?.close()
  }

  override fun afterStoppingVertx() {}

  override fun afterConfigParsed(config: JsonObject?) {}

  override fun afterStartingVertx(vertx: Vertx?) {}

  override fun beforeStoppingVertx(vertx: Vertx?) {}

  override fun beforeDeployingVerticle(deploymentOptions: DeploymentOptions?) {}
}


