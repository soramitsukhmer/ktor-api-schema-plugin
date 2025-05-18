package me.learning.api_schema.extension

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import io.ktor.util.StringValuesImpl
import me.learning.api_schema.common.Helper.badRequest
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

fun RoutingCall.pageRequest(): PageRequest {
    val page = this.parameters["page"]?.toLongOrNull() ?: 0
    val size = this.parameters["size"]?.toIntOrNull() ?: 10

    val direction = listOf("asc", "desc")

    val sort: Map<String, String> = this.parameters.getAll("sort")?.associate {
        val values = it.split(",")
        val dir = values.last()
        when (values.size) {
            1 -> values.first() to direction.first()
            2 -> direction.find { d -> d.equals(dir, ignoreCase = true) }?.let { d -> values.first() to d }
                ?: badRequest("Invalid sort property[${values.last()}]")
            else -> badRequest("Invalid sort property[$it]")
        }
    } ?: mapOf()

    return PageRequest(
        page = page,
        size = size,
        sort = sort,
    )
}

fun KType.getDefaultValue(): Any {
    return when (this.classifier) {
        Int::class -> 0
        Long::class -> 0L
        String::class -> ""
        Map::class -> mapOf<Any, Any?>()
        else -> badRequest("Unsupported class type[${this::class.java}]")
    }
}

fun <T : Any> RoutingCall.queryParameter(kClass: KClass<T>): T {
    val properties = kClass.memberProperties.associate { prop ->
        val type = prop.returnType
        val isNullable = type.isMarkedNullable

        val value = this.parameters[prop.name]

        prop.name to when {
            isNullable -> value
            else -> value ?: type.getDefaultValue()
        }
    }

    return properties.ct(kClass.java)
}
