package me.learning.api_schema.dto.route.put

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnMethodGetRequestBody
import me.learning.api_schema.dto.handler.throwOnMultipleProp
import kotlin.reflect.KClass

data class PutBuilderT4<T1 : Any, T2 : Any, T3 : Any, T4 : Any>(
    val route: Route,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>,
    val p3: Pair<KClass<T3>, RoutePropEnum>,
    val p4: Pair<KClass<T4>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second, p3.second, p4.second)

        MethodEnum.GET.throwOnMethodGetRequestBody(path, properties)
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.REQUEST_BODY, "request body")
    }
}
