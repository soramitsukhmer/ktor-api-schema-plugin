package com.skh.api_schema.dto.route.extension.get

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnInvalidTypeOfPathVariable
import com.skh.api_schema.extension.throwOnMethodGetRequestBody
import kotlin.reflect.KClass

data class GetDtoT1<T1 : Any>(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val p1: Pair<KClass<T1>, RoutePropEnum>
) {
    init {
        MethodEnum.GET.throwOnMethodGetRequestBody(path, listOf(p1.second))
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): GetDtoT2<T1, T> =
        GetDtoT2(
            route,
            path,
            responseWrapper,
            hidden,
            p1,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): GetDtoT2<T1, T> {
        MethodEnum.GET.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }
}
