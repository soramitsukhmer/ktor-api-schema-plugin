package com.skh.api_schema.dto.route.extension.get

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnInvalidTypeOfPathVariable
import com.skh.api_schema.extension.throwOnMethodGetRequestBody
import com.skh.api_schema.extension.throwOnMultipleProp
import kotlin.reflect.KClass

data class GetDtoT2<T1 : Any, T2 : Any>(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second)

        MethodEnum.GET.throwOnMethodGetRequestBody(path, properties)
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): GetDtoT3<T1, T2, T> =
        GetDtoT3(
            route,
            path,
            responseWrapper,
            hidden,
            p1,
            p2,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): GetDtoT3<T1, T2, T> {
        MethodEnum.GET.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }
}
