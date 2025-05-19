package me.learning.api_schema.extension

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import io.ktor.util.StringValuesImpl
import me.learning.api_schema.api.common.RoutePropertyEnum
import me.learning.api_schema.common.Helper.badRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.dto.request.PageRequest
import kotlin.collections.joinToString
import kotlin.collections.mapOf
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.getDefaultValue(value: String, param: String): T {
    return when (this) {
        Long::class -> value.toLongOrNull()
            ?: throw BadRequestException("Invalid Long value for parameter: $param")
        Int::class -> value.toIntOrNull()
            ?: throw BadRequestException("Invalid Int value for parameter: $param")
        String::class -> value
        else -> badRequest("Unsupported type $this for path parameters")
    } as T
}

suspend fun <T : Any> receiveRequestBody(clazzName: String?, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        when (e) {
            is RequestValidationException -> badRequest(e.reasons.minOf { it })
            else -> {
                e.cause?.cause.isMismatchException()
                e.cause.isMismatchException()

                print(">>> Invalid body request: : $clazzName")
                badRequest("The body request is invalid")
            }
        }
    }
}

inline fun <reified T : Any> RoutingCall.getPathVariable(param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return T::class.getDefaultValue(value, param)
}

fun <T : Any> RoutingCall.getPathVariable(clazz: KClass<T>, param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return clazz.getDefaultValue(value, param)
}

fun Throwable?.isMismatchException() = when (this) {
    is MismatchedInputException -> {
        val field = path.map { it.fieldName }.let { fields ->
            if (fields.size > 1) fields.joinToString(", ", "[", "]") { it }
            else fields.firstOrNull() ?: ""
        }
        badRequest("$field is missing")
    }
    else -> {}
}

suspend inline fun <reified T : Any> RoutingCall.requestBody(): T {
    return receiveRequestBody(T::class.simpleName) { receive<T>() }
}

suspend fun <T : Any> RoutingCall.requestBody(clazz: KClass<T>): T {
    return receiveRequestBody(clazz.simpleName) { receive(clazz) }
}

suspend fun <T : Any> RoutingCall.prop(pair: Pair<KClass<T>, RoutePropertyEnum>, path: String = "", pathVarIndex: Int = 0): Pair<T, Int> {
    return when (pair.second) {
        RoutePropertyEnum.AUTH -> auth(pair.first) to pathVarIndex
        RoutePropertyEnum.REQUEST_BODY -> requestBody(pair.first) to pathVarIndex
        RoutePropertyEnum.PATH_VARIABLE -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            getPathVariable(pair.first, param) to pathVarIndex.plus(1)
        }
    }
}
