package com.fetocan.vertx

import com.fetocan.vertx.global.configure
import com.fetocan.vertx.global.cronConfigure
import com.fetocan.vertx.global.flyway
import com.fetocan.vertx.global.jacksonConfig
import com.fetocan.vertx.global.jooqx
import com.fetocan.vertx.global.loggerConfigure
import com.fetocan.vertx.util.LoggerDelegate
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle


class MainVerticle : CoroutineVerticle() {

  private val logger by LoggerDelegate()

  override suspend fun start() {
    logger.info("Starting HTTP server...")
    loggerConfigure()
    jacksonConfig()

    val router = Router.router(vertx)
      .configure(jooqx(vertx, config))

    // Create the HTTP server
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(config.getJsonObject("server").getInteger("http.port"))
      .onSuccess {
        flyway(config).migrate()
//        cronConfigure(vertx)
        println("HTTP server started on port ${it.actualPort()}")
      }
      .onFailure {
        println("Failed to start HTTP server: ${it.message}")
      }
  }
}
