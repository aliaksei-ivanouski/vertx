package com.fetocan.vertx.page

import com.fetocan.vertx.error.BadRequestException
import io.vertx.core.MultiMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

fun pageRequest(params: MultiMap): PageRequest {
  var sort = Sort.unsorted()
  params.filter {
    it.key == "sort"
  }
    .map {
      it.value.split(",")
        .takeIf { array ->
          array.size == 2
        }
        .also { array ->
          array ?: throw BadRequestException("sorting params must not be null")
          sort = sort.and(Sort.by(Sort.Direction.fromString(array[1]), array[0]))
        }
        ?: throw BadRequestException("sort params specified incorrectly, " +
          "should be an array of comma divided sequential <field_name>,<sort_direction>")
    }
  return PageRequest.of(
    params["page"]?.toInt() ?: 0,
    params["size"]?.toInt() ?: 20,
    sort
  )
}
