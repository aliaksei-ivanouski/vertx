package com.fetocan.vertx

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestMainVerticle(vertx: Vertx) : TestBase(vertx, MainVerticle::class) {

  @Test
  fun `service is up`() = runTest {
    val request = httpClient.request(
      HttpMethod.GET,
      TEST_PORT,
      TEST_HOST,
      "/api/v1/healthy"
    ).await()
    val response = request.send().await()
    val responseString = response.body().await().toString()

    assertEquals("up", responseString)
  }
}
