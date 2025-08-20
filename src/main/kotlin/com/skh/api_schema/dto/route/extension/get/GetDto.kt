package com.skh.api_schema.dto.route.extension.get

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnInvalidTypeOfPathVariable
import kotlin.reflect.KClass

data class GetDto(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>) = GetDtoT1(
        route,
        path,
        responseWrapper,
        hidden,
        pair
    )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): GetDtoT1<T> {
        MethodEnum.GET.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }
}
