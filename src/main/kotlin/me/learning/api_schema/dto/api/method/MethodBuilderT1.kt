package me.learning.api_schema.dto.api.method

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.handler.throwOnMethodGetRequestBody
import kotlin.reflect.KClass

data class MethodBuilderT1<T1 : Any>(
    val route: Route,
    val method: MethodEnum,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropertyEnum>
) {
    init {
        method.throwOnMethodGetRequestBody(path, listOf(p1.second))
    }
}
