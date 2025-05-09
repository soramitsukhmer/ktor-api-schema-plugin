package me.learning.api_schema.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper<T>(
    val status: Status,
    val data: T,
    val requestId: String? = null
)

@Serializable
data class Status(
    val errorCode: Int,
    val errorMessage: String,
)

object ErrorCode {
    const val SUCCESS = 0
    const val NOT_FOUND = 3
    const val BAD_REQUEST = 4
    const val ERROR = 5
}
