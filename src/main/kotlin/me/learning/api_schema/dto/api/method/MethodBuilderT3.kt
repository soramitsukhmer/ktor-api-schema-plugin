package me.learning.api_schema.dto.api.method

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.handler.throwOnMethodGetRequestBody
import me.learning.api_schema.dto.handler.throwOnMultipleProp
import kotlin.reflect.KClass

data class MethodBuilderT3<T1 : Any, T2 : Any, T3 : Any>(
    val route: Route,
    val method: MethodEnum,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropertyEnum>,
    val p2: Pair<KClass<T2>, RoutePropertyEnum>,
    val p3: Pair<KClass<T3>, RoutePropertyEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second, p3.second)

        method.throwOnMethodGetRequestBody(path, properties)
        method.throwOnMultipleProp(path, properties, RoutePropertyEnum.AUTH, "auth")
        method.throwOnMultipleProp(path, properties, RoutePropertyEnum.REQUEST_BODY, "request body")
        method.throwOnMultipleProp(path, properties, RoutePropertyEnum.REQUEST_FILE, "request file")
    }
}
