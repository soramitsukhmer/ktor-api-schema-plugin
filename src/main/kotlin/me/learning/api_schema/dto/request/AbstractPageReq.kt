package me.learning.api_schema.dto.request

abstract class AbstractPageReq(
    open val size: Int = 10,
    open val page: Int = 0,
    open val sort: List<String> = emptyList()
)
