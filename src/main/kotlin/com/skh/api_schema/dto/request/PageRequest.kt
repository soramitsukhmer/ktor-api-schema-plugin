package com.skh.api_schema.dto.request

import com.skh.api_schema.common.Constant.SORTABLE_PATTERN
import com.skh.api_schema.common.Constant.SORT_DIRECTIONS

open class PageRequest(
    open val size: Int = 10,
    open val page: Long = 0,
    open val sort: List<String>
) {

    fun throwWhenInvalidSort() {
        sort
            .filter { it.trim().isEmpty() }
            .takeIf { it.isNotEmpty() }
            ?.let { throw IllegalArgumentException("The sort field(s) must not be empty value") }

        sort
            .filter { !SORTABLE_PATTERN.matches(it.trim()) }
            .takeIf { it.isNotEmpty() }
            ?.let { throw IllegalArgumentException("The sort key(s) ${if (it.size > 1) "are" else "is"} are invalid") }
    }

    fun sortables(): List<Sort> {
        return sort.mapNotNull {
            val list = it.split(",")
            Sort(it, true)
                .takeIf { list.size != 2 }
                ?: SORT_DIRECTIONS[list.last().trim().lowercase()]?.let { dir -> Sort(list.first(), dir) }
        }
    }
}

data class Sort(
    val property: String,
    val isAsc: Boolean
)
