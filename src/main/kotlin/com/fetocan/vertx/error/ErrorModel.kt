package com.fetocan.vertx.error

import java.time.Instant

data class ErrorModel(
  val status: Int,
  val path: String,
  val error: String,
  val message: String,
  val time: Instant = Instant.now()
)
