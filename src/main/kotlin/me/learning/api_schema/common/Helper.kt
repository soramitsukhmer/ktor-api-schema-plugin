package me.learning.api_schema.common

import io.ktor.server.plugins.BadRequestException
import me.learning.api_schema.dto.handler.InvalidAuthException

object Helper {

    fun extractAllPathParameters(path: String): List<String> {
        return Regex("""\{([^}]+)}""")
            .findAll(path)
            .map { it.groupValues[1] }
            .toList()
    }

    fun badRequest(message: String): Nothing = throw BadRequestException(message)

    fun invalidAuthentication(): Nothing = throw InvalidAuthException("invalid authentication")
}
