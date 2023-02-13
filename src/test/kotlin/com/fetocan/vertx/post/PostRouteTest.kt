package com.fetocan.vertx.post

import com.fetocan.vertx.MainVerticle
import com.fetocan.vertx.TestBase
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.kotlin.coroutines.await
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class PostRouteTest(vertx: Vertx) : TestBase(vertx, MainVerticle::class) {

  @Test
  fun `get all posts`() {
    val promise = Promise.promise<Any>()
    runTest {
      val request = httpClient.request(
        HttpMethod.POST,
        TEST_PORT,
        TEST_HOST,
        "/api/v1/posts"
      ).await()
      val response = request
        .putHeader("Content-Type", "application/json")
        .send(
          """
          {
              "title": "That is a title",
              "content": "This is an article content"
          }
        """.trimIndent()
        ).await()
      promise.complete(response.statusCode() == 201)
    }
    promise.future()
      .onComplete {
        runTest {
          val request = httpClient.request(
            HttpMethod.GET,
            TEST_PORT,
            TEST_HOST,
            "/api/v1/posts"
          ).await()
          val response = request.send().await()
          val responseJson = response.body().await().toJsonObject()

          val content = responseJson.getJsonArray("content")
          assertEquals(1, content.size())
          assertEquals(
            "That is a title",
            content.getJsonObject(0).getString("title")
          )
          assertEquals(
            "This is an article content",
            content.getJsonObject(0).getString("content")
          )
        }
      }
  }
}
