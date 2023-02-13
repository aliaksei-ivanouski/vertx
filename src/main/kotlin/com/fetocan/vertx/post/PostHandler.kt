package com.fetocan.vertx.post

import com.fetocan.vertx.util.LoggerDelegate
import com.fetocan.vertx.error.BadRequestException
import com.fetocan.vertx.error.NotFoundException
import com.fetocan.vertx.page.Tables.POSTS
import com.fetocan.vertx.page.paged
import com.fetocan.vertx.page.tables.pojos.Posts
import com.fetocan.vertx.page.pageRequest
import com.fetocan.vertx.web.SuccessResponse
import io.github.zero88.jooqx.DSLAdapter
import io.github.zero88.jooqx.Jooqx
import io.micrometer.core.annotation.Timed
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import org.springframework.data.domain.PageImpl
import java.lang.Exception
import java.util.UUID

class PostsHandler(
  private val jooqx: Jooqx
) {

  private val logger by LoggerDelegate()

  @Timed
  fun all(rc: RoutingContext) {
    logger.info("calling find all posts")
    val params = rc.queryParams()
    val pageable = pageRequest(params)

    jooqx.execute(
      jooqx.dsl().selectCount().from(POSTS),
      DSLAdapter.fetchCount()
    )
      .onSuccess { count ->
        jooqx.execute(
          jooqx.dsl().select(
            POSTS.asterisk()
          ).from(POSTS)
            .paged(pageable, POSTS),
          DSLAdapter.fetchMany(POSTS, listOf(POSTS.ID, POSTS.TITLE, POSTS.CONTENT, POSTS.CREATED_AT))
        )
          .onSuccess { fields ->
            val list = fields.map {
              it.into(Posts::class.java)
            }
            rc.response().end(Json.encode(PageImpl(list, pageable, count.toLong())))
          }
          .onFailure {
            logger.error("error occurred during fetching all posts", it)
            rc.fail(it)
          }
      }
      .onFailure {
        logger.error("error occurred during fetching posts count", it)
        rc.fail(it)
      }
  }

  @Timed
  fun getById(rc: RoutingContext) {
    val params = rc.pathParams()
    val uuid = try {
      UUID.fromString(params["id"])
    } catch (e: Exception) {
      rc.fail(BadRequestException("Invalid UUID"))
      return
    }

    jooqx.execute(
      jooqx.dsl()
        .select(POSTS.asterisk())
        .from(POSTS)
        .where(POSTS.ID.eq(uuid)),
      DSLAdapter.fetchOne(POSTS, listOf(POSTS.ID, POSTS.TITLE, POSTS.CONTENT, POSTS.CREATED_AT))
    )
      .onSuccess { record ->
        when (record) {
          null -> rc.fail(NotFoundException("Post id: $uuid not found."))
          else -> {
            val post = record.into(Posts::class.java)
            rc.response().end(Json.encode(post))
          }
        }
      }
      .onFailure {
        logger.error("post not found with id: $uuid", it)
        rc.fail(it)
      }
  }

  @Timed
  fun save(rc: RoutingContext) {
    val body = rc.body()
    logger.info("request body: ${body.asJsonObject()}")
    val (title, content) = body.asJsonObject().mapTo(CreatePostCommand::class.java)
    jooqx.execute(
      jooqx.dsl()
        .insertInto(POSTS, POSTS.TITLE, POSTS.CONTENT)
        .values(title, content),
      DSLAdapter.fetchOne(POSTS)
    )
      .onSuccess {
        rc.response()
          .setStatusCode(201)
          .end(Json.encode(SuccessResponse()))
      }
      .onFailure {
        logger.error("post was not saved", it)
        rc.fail(it)
      }
  }

  @Timed
  fun update(rc: RoutingContext) {
    val params = rc.pathParams()
    val uuid = try {
      UUID.fromString(params["id"])
    } catch (e: Exception) {
      rc.fail(BadRequestException("Invalid UUID"))
      return
    }

    val body = rc.body()
    logger.info("\npath param id: $uuid, \nrequest body: ${body.asJsonObject()}")
    val (title, content) = body.asJsonObject().mapTo(CreatePostCommand::class.java)

    jooqx.execute(
      jooqx.dsl()
        .select(POSTS.asterisk())
        .from(POSTS)
        .where(POSTS.ID.eq(uuid)),
      DSLAdapter.fetchOne(POSTS, listOf(POSTS.ID, POSTS.TITLE, POSTS.CONTENT, POSTS.CREATED_AT))
    )
      .onSuccess { record ->
        when (record) {
          null -> rc.fail(NotFoundException("Post id: $uuid not found."))
          else -> {
            jooqx.execute(
              jooqx.dsl().update(POSTS)
                .set(POSTS.TITLE, title)
                .set(POSTS.CONTENT, content)
                .where(POSTS.ID.eq(uuid)),
              DSLAdapter.fetchOne(POSTS)
            )
              .onSuccess {
                rc.response()
                  .setStatusCode(204)
                  .end(Json.encode(SuccessResponse()))
              }
              .onFailure {
                logger.error("post was not updated id: $uuid", it)
                rc.fail(it)
              }
          }
        }
      }
      .onFailure {
        logger.error("post was not updated id: $uuid", it)
        rc.fail(it)
      }
  }

  @Timed
  fun delete(rc: RoutingContext) {
    val params = rc.pathParams()
    val uuid = try {
      UUID.fromString(params["id"])
    } catch (e: Exception) {
      rc.fail(BadRequestException("Invalid UUID"))
      return
    }

    jooqx.execute(
      jooqx.dsl()
        .select(POSTS.asterisk())
        .from(POSTS)
        .where(POSTS.ID.eq(uuid)),
      DSLAdapter.fetchOne(POSTS, listOf(POSTS.ID, POSTS.TITLE, POSTS.CONTENT, POSTS.CREATED_AT))
    )
      .onSuccess { record ->
        if (record != null) {
          jooqx.execute(
            jooqx.dsl().delete(POSTS)
              .where(POSTS.ID.eq(uuid)),
            DSLAdapter.fetchOne(POSTS)
          )
            .onSuccess {
              rc.response()
                .setStatusCode(204)
                .end(Json.encode(SuccessResponse()))
            }
            .onFailure {
              logger.error("post was not updated", it)
              rc.fail(it)
            }
        }
      }
      .onFailure {
        logger.error("post not found with id: $uuid", it)
        rc.fail(404, it)
      }
  }

  @Timed
  fun deleteFirst(rc: RoutingContext) {
    jooqx.execute(
      jooqx.dsl()
        .select(POSTS.asterisk())
        .from(POSTS)
        .limit(1),
      DSLAdapter.fetchOne(POSTS, listOf(POSTS.ID, POSTS.TITLE, POSTS.CONTENT, POSTS.CREATED_AT))
    )
      .onSuccess { record ->
        val post = record.map { it.into(Posts::class.java) }
        jooqx.execute(
          jooqx.dsl().delete(POSTS)
            .where(POSTS.ID.eq(post.id)),
          DSLAdapter.fetchOne(POSTS)
        )
          .onSuccess {
            rc.response()
              .setStatusCode(204)
              .end(Json.encode(SuccessResponse()))
          }
          .onFailure {
            logger.error("first post was not deleted", it)
            rc.fail(it)
          }
      }
      .onFailure {
        logger.error("first post was not retrieved", it)
        rc.fail(it)
      }
  }
}
