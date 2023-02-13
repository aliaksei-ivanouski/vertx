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

fun configureMeters(): MeterRegistry {
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
