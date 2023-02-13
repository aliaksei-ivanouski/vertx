package com.fetocan.vertx.post

import com.fetocan.vertx.error.handleError
import com.fetocan.vertx.web.Constants.WEB_PREFIX
import io.github.zero88.jooqx.Jooqx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

fun Router.configurePostRouter(
  jooqx: Jooqx
) {
  val handler = PostsHandler(jooqx)

  this.get("$WEB_PREFIX/posts")
    .produces("application/json")
    .handler { handler.all(it) }
    .failureHandler { handleError(it) }

  this.get("$WEB_PREFIX/posts/:id")
    .produces("application/json")
    .handler { handler.getById(it) }
    .failureHandler { handleError(it) }

  this.post("$WEB_PREFIX/posts")
    .consumes("application/json")
    .handler(BodyHandler.create())
    .handler { handler.save(it) }

  this.put("$WEB_PREFIX/posts/:id")
    .consumes("application/json")
    .handler(BodyHandler.create())
    .handler { handler.update(it) }
    .failureHandler { handleError(it) }

  this.delete("$WEB_PREFIX/posts/:id")
    .handler { handler.delete(it) }
    .failureHandler { handleError(it) }

  this.delete("$WEB_PREFIX/posts-first")
    .handler { handler.deleteFirst(it) }
    .failureHandler { handleError(it) }
}
