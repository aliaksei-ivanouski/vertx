package com.fetocan.vertx.global

import io.vertx.ext.web.Router
import io.vertx.micrometer.PrometheusScrapingHandler

fun Router.configureMetrics() = this.route("/metrics").handler(PrometheusScrapingHandler.create())
