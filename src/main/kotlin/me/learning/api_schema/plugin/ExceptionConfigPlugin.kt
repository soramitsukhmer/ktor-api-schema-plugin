package me.learning.api_schema.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import me.learning.api_schema.config.exception.ExceptionConfig
import me.learning.api_schema.dto.handler.InvalidAuthException
import me.learning.api_schema.dto.handler.UnauthorizedAuthException
import me.learning.api_schema.extension.badReq
import me.learning.api_schema.extension.invalidAuth
import me.learning.api_schema.extension.notFound
import me.learning.api_schema.extension.unauthorized
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

val logger: Logger = LoggerFactory.getLogger("Exception Plugin")

fun Application.exceptionConfigPlugin(config: ExceptionConfig) {
    if (config.enabled && this.pluginOrNull(StatusPages) == null) {
        install(StatusPages) {
            exception<InvalidAuthException> { call, cause ->
                logger.error("Auth", cause)
                call.invalidAuth()
            }
            exception<UnauthorizedAuthException> { call, cause ->
                logger.error("Auth", cause)
                call.unauthorized()
            }
            exception<BadRequestException> { call, cause ->
                logger.error("Bad Request from development", cause)
                call.badReq(cause.message)
            }
            exception<NotFoundException> { call, cause ->
                logger.error("Not Found", cause)
                call.notFound(cause.message)
            }
            exception<IllegalArgumentException> { call, cause ->
                logger.error("IllegalArgumentException: ", cause)
                call.badReq(cause.message)
            }

            config.property.exceptions.forEach { (exceptionClass, handler) ->
                @Suppress("UNCHECKED_CAST")
                exception(exceptionClass as KClass<Throwable>, handler)
            }

            config.property.statuses.forEach { (statusCode, handler) ->
                status(statusCode) { handler }
            }
        }
    }
}
