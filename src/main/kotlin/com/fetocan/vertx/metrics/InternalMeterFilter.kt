package com.fetocan.vertx.metrics

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig

class InternalMeterFilter : MeterFilter {

  companion object {
    val SERVICE_SLAS_IN_MS = listOf(
      0.1,
      1.0,
      5.0,
      10.0,
      25.0,
      50.0,
      100.0,
      250.0,
      500.0,
      1000.0,
      2500.0,
      5000.0,
      10000.0,
      20000.0,
      300000.0
    )
  }

  override fun map(id: Meter.Id): Meter.Id {
    return when (id.name) {
      "vertx.http.server.responseTime" -> id.withName("http.server.requests")
      "vertx.http.server.requestCount" -> id.withName("http.requests.total")
      else -> super.map(id)
    }
  }

  override fun configure(id: Meter.Id, config: DistributionStatisticConfig): DistributionStatisticConfig? {
    return if (id.type == Meter.Type.TIMER) {
      DistributionStatisticConfig
        .builder()
        .serviceLevelObjectives(
          *SERVICE_SLAS_IN_MS.map { it * 1_000_000 }.toDoubleArray()
        )
        .build()
        .merge(config)
    } else {
      super.configure(id, config)
    }
  }
}
