package com.skh.api_schema.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper<T>(
    val status: Status,
    val data: T?,
    val requestId: String?
)

@Serializable
data class Status(
    val code: Int,
    val message: String?,
)

object ErrorCode {
    const val SUCCESS = 0
    const val NOT_FOUND = 3
    const val BAD_REQUEST = 4
    const val ERROR = 5
}

data class Pagination(
    val currentPage: Long,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Long,
    val first: Boolean,
    val last: Boolean
)

data class PageResponse<T>(
    val content: List<T>,
    val pagination: Pagination
)
