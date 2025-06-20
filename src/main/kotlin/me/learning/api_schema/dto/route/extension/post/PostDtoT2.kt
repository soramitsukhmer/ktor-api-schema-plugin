package me.learning.api_schema.dto.route.extension.post

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnMultipleProp
import me.learning.api_schema.dto.handler.throwUnsupportedWhenPropExistedOnMethod
import kotlin.reflect.KClass

data class PostDtoT2<T1 : Any, T2 : Any>(
    val route: Route,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second)

        MethodEnum.POST.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
        MethodEnum.POST.throwOnMultipleProp(path, properties, RoutePropEnum.REQUEST_BODY, "request body")
        MethodEnum.POST.throwUnsupportedWhenPropExistedOnMethod(path, listOf(p1.second), RoutePropEnum.PATH_VARIABLE)
    }
}
