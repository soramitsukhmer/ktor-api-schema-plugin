package me.learning.api_schema.api.dto.method

import io.ktor.server.routing.Route
import me.learning.api_schema.api.common.MethodEnum
import me.learning.api_schema.api.common.RoutePropertyEnum
import me.learning.api_schema.api.common.throwOnMethodGetRequestBody
import kotlin.reflect.KClass

data class MethodBuilderT1<T1 : Any>(
    val route: Route,
    val method: MethodEnum,
    val path: String,
    var hasAuth: Boolean,
    val p1: Pair<KClass<T1>, RoutePropertyEnum>
) {
    init {
        method.throwOnMethodGetRequestBody(path, listOf(p1.second))
        if (!hasAuth) {
            hasAuth = p1.second == RoutePropertyEnum.AUTH
        }
    }
}
