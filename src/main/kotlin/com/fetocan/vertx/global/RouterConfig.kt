package com.fetocan.vertx.global

import com.fetocan.vertx.health.configureHealthRouter
import com.fetocan.vertx.post.configurePostRouter
import io.github.zero88.jooqx.Jooqx
import io.vertx.ext.web.Router

fun Router.configure(
  jooqx: Jooqx
) {
  this.route().handler {
    val response = it.request().response()
    response.putHeader("Content-Type", "application/json")
    it.next()
  }

  // Configure routes
  this.configureHealthRouter()
  this.configureMetrics()
  this.configurePostRouter(jooqx)
}
