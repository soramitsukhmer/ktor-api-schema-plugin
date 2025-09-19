package com.skh.api_schema.dto.request

import com.skh.api_schema.common.Direction

abstract class PageRequest(
    open val size: Int = 10,
    open val page: Long = 0,
    open val sort: List<String>
) {
    fun getInvalidSortKeys(): List<String> {
        return sort.filter { it.split(",").size > 2 }
            .takeIf { it.isNotEmpty() }
            ?: sort.filter {
                val list = it.split(",")
                if (list.size != 2) return@filter false
                val dir = list.last()
                !Direction.isValid(dir)
            }
    }

    fun getSortTypes(): List<Sort> {
        return sort.map {
            val list = it.split(",")
            if (list.size == 2) {
                val key = list.first()
                val dir = Direction.findByName(list.last())
                Sort(key, dir)
            } else {
                Sort(it, Direction.ASC)
            }
        }
    }
}

data class Sort(
    val property: String,
    val direction: Direction
)
