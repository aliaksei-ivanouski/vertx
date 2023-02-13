package com.fetocan.vertx.post

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class Post(
  var id: UUID? = null,
  var title: String? = null,
  var content: String? = null,
  var createdAt: LocalDateTime? = LocalDateTime.now()
): Serializable

data class CreatePostCommand(
  val title: String? = null,
  val content: String? = null
)
