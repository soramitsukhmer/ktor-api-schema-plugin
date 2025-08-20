package com.skh.api_schema.dto.route.extension.put

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnFileReqBody
import com.skh.api_schema.extension.throwOnInvalidTypeOfPathVariable
import com.skh.api_schema.extension.throwOnMethodGetRequestBody
import com.skh.api_schema.extension.throwOnMultipleProp
import kotlin.reflect.KClass

data class PutDtoT2<T1 : Any, T2 : Any>(
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
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.REQUEST_BODY, "request body")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): PutDtoT3<T1, T2, T> =
        PutDtoT3(
            route,
            path,
            responseWrapper,
            hidden,
            p1,
            p2,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): PutDtoT3<T1, T2, T> {
        MethodEnum.PUT.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }

    inline fun <reified T : Any> requestBody(): PutDtoT3<T1, T2, T> {
        MethodEnum.PUT.throwOnFileReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }
}
