package com.fetocan.vertx.requests

import com.fetocan.vertx.util.LoggerDelegate


object ChangesChecker {

  private val logger by LoggerDelegate()

  fun checkChanges() {
    logger.info("Checking some changes")
  }
}

