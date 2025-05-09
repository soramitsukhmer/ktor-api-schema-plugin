package me.learning.api_schema.route

import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import me.learning.api_schema.common.Helper.asKType
import me.learning.api_schema.common.Helper.getQueryParamInfo
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Configures a route with optional authentication, pagination, path variables,
 * query parameters, and request/response handling.
 *
 * @param T The type of the response body.
 * @param description A description for the route's response. Default is an empty string.
 * @param hasAuth Indicates whether the route requires authentication. Default is true.
 * @param hasPageRequest Specifies if pagination query parameters should be included. Default is false.
 * @param variable A map of path variable names to their respective types, if any exist. Default is an empty map.
 * @param param An optional class to derive query parameters from its properties. Default is null.
 * @param requestBody An optional type representing the request body. If provided, it will be used to parse incoming requests. Default is null.
 * @return A lambda expression that applies the route configuration to the specified `RouteConfig`.
 */
inline fun <reified T> configBuilder(
    description: String = "",
    hasAuth: Boolean = true,
    hasPageRequest: Boolean = false,
    variable: Map<String, KClass<*>> = mapOf(),
    param: KClass<*>? = null,
    requestBody: KType? = null,
): RouteConfig.() -> Unit = {

    if (hasAuth) securitySchemeNames(SECURITY_BEARER_SCHEMA_NAME)

    request {
        if (variable.isNotEmpty()) {
            variable.forEach { (key, value) ->
                pathParameter(key, value.asKType())
            }
        }

        if (hasPageRequest) {
            queryParameter<Int?>("size")
            queryParameter<Int?>("page")
            queryParameter<String?>("sort")
        }

        param?.getQueryParamInfo()?.forEach { (name, kType) ->
            queryParameter(name, kType) {
                require(kType.isMarkedNullable)
            }
        }

        requestBody?.let(::body)
    }

    response {
        HttpStatusCode.OK to {
            this.description = description
            body<T>()
        }
    }
}
