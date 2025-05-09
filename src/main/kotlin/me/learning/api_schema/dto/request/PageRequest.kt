package me.learning.api_schema.dto.request

open class PageRequest(
    val size: Int = 10,
    val page: Long = 0,
    var sort: Map<String, String> = mapOf()
)
