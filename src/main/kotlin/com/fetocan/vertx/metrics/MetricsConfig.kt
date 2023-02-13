package com.fetocan.vertx.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.vertx.core.VertxOptions
import io.vertx.micrometer.Label
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.micrometer.VertxPrometheusOptions
import java.util.EnumSet

fun configureMetricsOptions(options: VertxOptions) {
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

private fun configureMeters(): MeterRegistry {
  val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  registry.config().meterFilter(InternalMeterFilter())
  registry.config().meterFilter(MeterFilter.renameTag("", "path", "uri"))
  registry.config().meterFilter(MeterFilter.renameTag("", "code", "status"))

  JvmMemoryMetrics().bindTo(registry)
  JvmGcMetrics().bindTo(registry)
  ProcessorMetrics().bindTo(registry)
  JvmThreadMetrics().bindTo(registry)
  LogbackMetrics().bindTo(registry)

  return registry
}
