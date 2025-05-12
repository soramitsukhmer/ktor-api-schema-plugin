package me.learning.api_schema.common.extension

import io.ktor.server.plugins.BadRequestException
import me.learning.api_schema.config.schema.RouteSchemaConfig
import me.learning.api_schema.utils.JacksonObjMapper.objectMapper
import me.learning.api_schema.utils.fromMapToClass
import me.learning.api_schema.dto.handler.InvalidAuthException

inline fun <reified T> Map<*, *>.ct(): T = objectMapper.fromMapToClass<T>(this)

fun <T> Map<*, *>.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun badRequest(message: String): Nothing = throw BadRequestException(message)

fun invalidAuthentication(): Nothing = throw InvalidAuthException("invalid authentication")

fun String.cleanRoute() = when (endsWith("/")) {
    true -> substringBeforeLast("/")
    false -> this
}

fun String.mergeRoute(baseRoute: String, defaultPath: String): String {
    val path = cleanRoute().takeIf { it.trim().isNotEmpty() } ?: defaultPath
    val base = baseRoute.cleanRoute()

    return when (path.startsWith("/")) {
        true -> path
        false -> "/$path"
    }.let(base::plus)
}
