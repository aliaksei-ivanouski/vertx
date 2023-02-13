package com.fetocan.vertx.health

import io.vertx.ext.web.RoutingContext

class HealthHandler {
  fun healthCheck(rc: RoutingContext) {
    rc.response().end("up")
  }
}
