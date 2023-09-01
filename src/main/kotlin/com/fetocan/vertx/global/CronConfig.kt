package com.fetocan.vertx.global

import com.fetocan.vertx.requests.ChangesChecker.checkChanges
import com.fetocan.vertx.util.LoggerDelegate
import io.github.zero88.vertx.scheduler.JobData
import io.github.zero88.vertx.scheduler.TaskExecutionContext
import io.github.zero88.vertx.scheduler.impl.CronTaskExecutor
import io.github.zero88.vertx.scheduler.impl.IntervalTaskExecutor
import io.github.zero88.vertx.scheduler.trigger.CronTrigger
import io.github.zero88.vertx.scheduler.trigger.IntervalTrigger
import io.vertx.core.Vertx
import java.util.UUID
import java.util.concurrent.TimeUnit

private val logger = CronConfig.logger

object CronConfig {
  val logger by LoggerDelegate()
}

fun cronConfigure(
  vertx: Vertx
) {
  startCron(vertx = vertx, expression = "0/5 * * ? * * *", jobName = "Every 5 seconds") {
    checkChanges()
  }
  startCron(vertx = vertx, jobName = "Every 1 second", interval = 1, intervalTimeUnit = TimeUnit.SECONDS) {
    logger.info("Hello from second cron job")
  }
  startCron(vertx, "Every 2 seconds", 2, TimeUnit.SECONDS, 6, 4) {
    logger.info("Hello from third cron job. Repeat 4 times only")
  }
  startCron(vertx = vertx, jobName = "Every 3 second", interval = 3, intervalTimeUnit = TimeUnit.SECONDS, repeat = 3) {
    logger.info("Hello from fourth cron job")
  }
}

/**
 * Cron job that will be executed by cron expression
 *
 * @param vertx - vertex
 * @param jobName - name of the cron in the system (by default ${UUID.randomUUID()})
 * @param expression - how many times the job will be executed (example for every 5 sec "0/5 * * ? * * *")
 * @param poolSize - worker scheduler pool size (by default 1)
 * @param job - block with executing job instructions
 */
private fun <T : Any> startCron(
  vertx: Vertx,
  expression: String,
  jobName: String = UUID.randomUUID().toString(),
  poolSize: Int = 1,
  job: () -> T?
) {
  CronTaskExecutor.builder()
    .vertx(vertx)
    .trigger(
      CronTrigger.builder()
        .expression(expression)
        .build()
    )
    .task {
        _: JobData?,
        _: TaskExecutionContext? -> job()
    }
    .build()
    .start(
      vertx.createSharedWorkerExecutor(jobName, poolSize)
    )
}

/**
 * Cron job that will be executed constantly with '@interval'
 * or repeated '@repeat' times only with '@interval'
 *
 * @param vertx - vertx
 * @param jobName - name of the cron in the system (by default ${UUID.randomUUID()})
 * @param interval - how many times the job will be executed
 * @param intervalTimeUnit - per which time unit the job will be executed (by default ${TimeUnit.SECONDS})
 * @param repeat - how many times the job will be repeated after launch
 * @param poolSize - worker scheduler pool size (by default 1)
 * @param job - block with executing job instructions
 */
private fun <T : Any> startCron(
  vertx: Vertx,
  jobName: String = UUID.randomUUID().toString(),
  interval: Long,
  intervalTimeUnit: TimeUnit = TimeUnit.SECONDS,
  poolSize: Int = 1,
  repeat: Long? = null,
  job: () -> T?
) {
  IntervalTaskExecutor.builder()
    .vertx(vertx)
    .trigger(
      IntervalTrigger.builder()
        .interval(interval)
        .intervalTimeUnit(intervalTimeUnit)
        .also {
          if (repeat != null) {
            it.repeat(repeat)
          }
        }.build()
    )
    .task {
        _: JobData?,
        _: TaskExecutionContext? -> job()
    }
    .build()
    .start(
      vertx.createSharedWorkerExecutor(jobName, poolSize)
    )
}
