package com.fetocan.vertx.global

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.json.jackson.DatabindCodec

fun jacksonConfig() {
  val objectMapper = DatabindCodec.mapper()
  objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
  objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
  val module = JavaTimeModule()
  objectMapper.registerModule(module)
}
