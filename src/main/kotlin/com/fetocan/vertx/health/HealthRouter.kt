package com.fetocan.vertx.health

import com.fetocan.vertx.web.Constants.WEB_PREFIX
import io.vertx.ext.web.Router

fun Router.configureHealthRouter() {

  val healthHandler = HealthHandler()

  this.get("$WEB_PREFIX/healthy")
    .produces("text/plain")
    .handler { healthHandler.healthCheck(it) }

}
