package com.skh.api_schema.dto.route.extension.get

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnMethodGetRequestBody
import com.skh.api_schema.extension.throwOnMultipleProp
import kotlin.reflect.KClass

data class GetDtoT3<T1 : Any, T2 : Any, T3 : Any>(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val accessRights: List<String>,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>,
    val p3: Pair<KClass<T3>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second, p3.second)

        MethodEnum.GET.throwOnMethodGetRequestBody(path, properties)
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
    }
}
