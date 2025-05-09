package me.learning.api_schema.common.extension

import me.learning.api_schema.dto.response.ErrorCode
import me.learning.api_schema.dto.response.ResponseWrapper
import me.learning.api_schema.dto.response.Status
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.principal
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.ok(message: T) {
    response.status(HttpStatusCode.OK)
    respond(
        ResponseWrapper(
            Status(ErrorCode.SUCCESS, "Success"),
            message,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

inline fun <reified T : Any> ApplicationCall.auth(): T {
    return try {
        principal<T>() ?: invalidAuthentication()
    } catch (e: Exception) {
        invalidAuthentication()
    }
}

suspend inline fun ApplicationCall.invalidAuth() {
    response.status(HttpStatusCode.Unauthorized)
    respond(
        ResponseWrapper(
            Status(ErrorCode.BAD_REQUEST, "Token is invalid or expired"),
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.unauthorized() {
    response.status(HttpStatusCode.Forbidden)
    respond(
        ResponseWrapper(
            Status(ErrorCode.BAD_REQUEST, "Access Denied"),
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.badReq(message: String?, data: Any? = null) {
    response.status(HttpStatusCode.BadRequest)
    respond(
        ResponseWrapper(
            Status(ErrorCode.BAD_REQUEST, message ?: HttpStatusCode.BadRequest.description),
            data,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.notFound(message: String?, data: Any? = null) {
    response.status(HttpStatusCode.NotFound)
    respond(
        ResponseWrapper(
            Status(ErrorCode.NOT_FOUND, message ?: HttpStatusCode.NotFound.description),
            data,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.error(message: String?) {
    response.status(HttpStatusCode.InternalServerError)
    respond(
        ResponseWrapper(
            Status(ErrorCode.ERROR, message ?: HttpStatusCode.InternalServerError.description),
            null,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.response(statusCode: Int, message: String?, data: Any? = null) {
    response.status(HttpStatusCode.BadRequest)
    respond(
        ResponseWrapper(
            Status(statusCode, message ?: HttpStatusCode.BadRequest.description),
            data,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}
