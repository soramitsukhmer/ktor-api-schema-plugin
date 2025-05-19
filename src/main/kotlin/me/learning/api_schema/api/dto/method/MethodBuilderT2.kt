package me.learning.api_schema.api.dto.method

import io.ktor.server.routing.Route
import me.learning.api_schema.api.common.MethodEnum
import me.learning.api_schema.api.common.RoutePropertyEnum
import me.learning.api_schema.api.common.throwOnMethodGetRequestBody
import me.learning.api_schema.api.common.throwOnMultipleProp
import kotlin.reflect.KClass

data class MethodBuilderT2<T1 : Any, T2 : Any>(
    val route: Route,
    val method: MethodEnum,
    val path: String,
    var hasAuth: Boolean,
    val p1: Pair<KClass<T1>, RoutePropertyEnum>,
    val p2: Pair<KClass<T2>, RoutePropertyEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second)

        method.throwOnMethodGetRequestBody(path, properties)
        method.throwOnMultipleProp(path, properties, RoutePropertyEnum.AUTH, "auth")
        method.throwOnMultipleProp(path, properties, RoutePropertyEnum.REQUEST_BODY, "request body")

        if (!hasAuth) hasAuth = properties.any { it == RoutePropertyEnum.AUTH }
    }
}
