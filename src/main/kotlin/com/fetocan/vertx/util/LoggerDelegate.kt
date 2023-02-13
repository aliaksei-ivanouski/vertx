package com.fetocan.vertx.util

import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
  override fun getValue(thisRef: R, property: KProperty<*>)
    = getLogger(getClassForLogging(thisRef.javaClass))
}

fun getLogger(forClass: Class<*>): Logger =
  LoggerFactory.getLogger(forClass)

fun <T : Any> getClassForLogging(javaClass: Class<T>) =
  javaClass.enclosingClass?.takeIf {
    it.kotlin.java == javaClass
  } ?: javaClass
