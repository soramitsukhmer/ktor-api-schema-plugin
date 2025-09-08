package com.skh.api_schema.extension

import com.skh.api_schema.common.Helper.badRequest
import com.skh.api_schema.common.Helper.extractAllPathParameters
import com.skh.api_schema.common.Helper.invalidAuthentication
import com.skh.api_schema.dto.response.ErrorCode
import com.skh.api_schema.dto.response.ResponseWrapper
import com.skh.api_schema.dto.response.Status
import com.skh.api_schema.dto.route.inline.RouteProp
import com.skh.api_schema.dto.route.inline.impl.Auth
import com.skh.api_schema.dto.route.inline.impl.PathVariable
import com.skh.api_schema.dto.route.inline.impl.RequestBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import kotlin.reflect.KClass

suspend inline fun <reified T> ApplicationCall.ok(data: T, withWrapper: Boolean = true) {
    response.status(HttpStatusCode.OK)
    when (withWrapper) {
        true -> respond(
            ResponseWrapper(
                Status(ErrorCode.SUCCESS, "Success"),
                data,
                request.headers[HttpHeaders.XRequestId]
            )
        )
        false -> respond(data as Any)
    }
}

inline fun <reified T : Any> ApplicationCall.auth(): T {
    return try {
        val principal = principal<JWTPrincipal>()?.getClaim("data", Map::class)
        principal?.ct(T::class.java)  ?: invalidAuthentication()
    } catch (e: Exception) {
        invalidAuthentication()
    }
}

fun <T : Any> ApplicationCall.auth(clazz: KClass<T>): T {
    return try {
        val principal = principal<JWTPrincipal>()?.getClaim("data", Map::class)
        principal?.ct(clazz.java)  ?: invalidAuthentication()
    } catch (e: Exception) {
        invalidAuthentication()
    }
}

suspend inline fun ApplicationCall.response(http: HttpStatusCode, code: Int, message: String?, data: Any? = null) {
    response.status(http)
    respond(ResponseWrapper(Status(code, message), data, request.headers[HttpHeaders.XRequestId]))
}

suspend inline fun ApplicationCall.invalidAuth(
    code: Int = ErrorCode.BAD_REQUEST,
    message: String = "Token is invalid or expired",
    http: HttpStatusCode = HttpStatusCode.Forbidden
) {
    response.status(http)
    respond(ResponseWrapper(Status(code, message), null, request.headers[HttpHeaders.XRequestId]))
}

suspend inline fun ApplicationCall.unauthorized(
    code: Int = ErrorCode.BAD_REQUEST,
    message: String = "Access Denied",
    http: HttpStatusCode = HttpStatusCode.Forbidden
) {
    response.status(http)
    respond(ResponseWrapper(Status(code, message), null, request.headers[HttpHeaders.XRequestId]))
}

suspend inline fun ApplicationCall.badReq(message: String?) {
    response.status(HttpStatusCode.BadRequest)
    respond(
        ResponseWrapper(
            Status(ErrorCode.BAD_REQUEST, message ?: HttpStatusCode.BadRequest.description),
            null,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.notFound(message: String?) {
    response.status(HttpStatusCode.NotFound)
    respond(
        ResponseWrapper(
            Status(ErrorCode.NOT_FOUND, message ?: HttpStatusCode.NotFound.description),
            null,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun ApplicationCall.accessDenied() {
    response.status(HttpStatusCode.Forbidden)
    respond(
        ResponseWrapper(
            Status(ErrorCode.BAD_REQUEST, "Access Denied"),
            null,
            request.headers[HttpHeaders.XRequestId]
        )
    )
}

suspend inline fun <reified T : Any> ApplicationCall.requestBody(): T {
    return receiveRequestBody(T::class.simpleName) { receive<T>() }
}

suspend fun <T : Any> ApplicationCall.requestBody(clazz: KClass<T>): T {
    return receiveRequestBody(clazz.simpleName) { receive(clazz) }
}

inline fun <reified T : Any> ApplicationCall.getPathVariable(param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return T::class.getDefaultValue(value, param)
}

fun <T : Any> ApplicationCall.getPathVariable(clazz: KClass<T>, param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return clazz.getDefaultValue(value, param)
}

suspend inline fun <reified T : Any, reified I : RouteProp<T>> ApplicationCall.prop(path: String, pathVarIndex: Int = 0): Pair<I, Int> {
    var idx = pathVarIndex
    return when (I::class) {
        Auth::class -> Auth(this.auth<T>())
        RequestBody::class -> RequestBody(this.requestBody<T>())
        PathVariable::class -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            val value = getPathVariable<T>(param)
            idx++
            PathVariable(value)
        }
        else -> throw IllegalArgumentException("Unsupported RouteProp type: ${I::class}")
    } as I to idx
}
