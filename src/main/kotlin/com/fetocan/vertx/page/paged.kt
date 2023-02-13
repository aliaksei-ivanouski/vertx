package com.fetocan.vertx.page

import net.pearx.kasechange.toSnakeCase
import org.jooq.OrderField
import org.jooq.Record
import org.jooq.SelectForUpdateStep
import org.jooq.SelectOrderByStep
import org.jooq.Table
import org.jooq.impl.DSL
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun <R : Record> SelectOrderByStep<R>.paged(
    pageable: Pageable,
    table: Table<in R>
): SelectForUpdateStep<R> {
    val orderStep = if (pageable.sort.isSorted) {
        this.orderBy(pageable.sort.map<OrderField<*>?> {
            val field = table.field(it.property.toSnakeCase())
                ?: DSL.field(it.property.toSnakeCase())

            var sort = if(it.direction.isAscending)
                field.asc()
            else
                field.desc()

            sort = when (it.nullHandling) {
                Sort.NullHandling.NULLS_FIRST -> sort.nullsFirst()
                Sort.NullHandling.NULLS_LAST -> sort.nullsLast()
                Sort.NullHandling.NATIVE -> sort
            }

            sort
        }.filterNotNull())
    } else this

    val limitStep = if (pageable.isPaged) {
        orderStep.limit(pageable.pageSize).offset(pageable.offset)
    } else orderStep

    return limitStep
}
