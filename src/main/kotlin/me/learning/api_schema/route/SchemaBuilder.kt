package me.learning.api_schema.route

import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.HttpStatusCode
import me.learning.api_schema.extension.asKType
import me.learning.api_schema.extension.getQueryParamInfo
import kotlin.reflect.KClass

/**
 * Constructs a configuration block for a route to define authentication, path variables, request body, and response type.
 *
 * @param T The type of the response body.
 * @param hasAuth Indicates whether the route requires authentication. Defaults to true.
 * @param pathVariable A map of path variable names to their respective types. Can be null if no path variables are required.
 * @param requestBody The type of the request body. Can be null if no request body is required.
 * @return A lambda configuration block that applies the specified settings to the route.
 */
inline fun <reified T> configBuilder(
    hasAuth: Boolean = true,
    pathVariable: Map<String, KClass<*>>? = null,
    requestBody: KClass<*>? = null,
): RouteConfig.() -> Unit = {

    if (hasAuth) securitySchemeNames(SECURITY_BEARER_SCHEMA_NAME)

    request { pathVariable?.let { it.forEach { (key, value) -> pathParameter(key, value.asKType()) } } }

    request { requestBody?.asKType()?.let(::body) }

    response {
        HttpStatusCode.OK to {
            body<T>()
        }
    }
}
