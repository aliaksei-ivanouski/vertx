package com.fetocan.vertx.error

import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

fun handleError(it: RoutingContext) {
  val error = it.failure()
  it.response()
    .setStatusCode(it.statusCode())
    .end(
      Json.encode(
        ErrorModel(
          status = it.statusCode(),
          error = error::class.java.simpleName,
          path = it.normalizedPath(),
          message = error.cause?.message ?: error.localizedMessage
        )
      )
    )
}
