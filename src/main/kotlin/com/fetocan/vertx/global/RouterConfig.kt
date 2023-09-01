package com.fetocan.vertx.global

import com.fetocan.vertx.health.configureHealthRouter
import com.fetocan.vertx.post.configurePostRouter
import com.fetocan.vertx.web.Constants.APPLICATION_JSON_MIME_MAPPING_KEY
import io.github.zero88.jooqx.Jooqx
import io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import io.vertx.core.http.impl.MimeMapping
import io.vertx.ext.web.Router

fun Router.configure(
  jooqx: Jooqx
): Router {
  this.route().handler {
    val response = it.request().response()
    response.putHeader(
      CONTENT_TYPE,
      MimeMapping.getMimeTypeForExtension(APPLICATION_JSON_MIME_MAPPING_KEY)
    )
    it.next()
  }

  // Configure routes
  this.configureHealthRouter()
  this.configureMetrics()
  this.configurePostRouter(jooqx)

  return this
}
