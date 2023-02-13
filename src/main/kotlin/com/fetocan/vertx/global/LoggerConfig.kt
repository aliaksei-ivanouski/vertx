package com.fetocan.vertx.global

import io.vertx.core.logging.SLF4JLogDelegateFactory

fun loggerConfigure() {
  System.getProperty("org.vertx.logger-delegate-factory-class-name")
    ?: System.setProperty(
      "org.vertx.logger-delegate-factory-class-name",
      SLF4JLogDelegateFactory::class.java.name
    )
}
