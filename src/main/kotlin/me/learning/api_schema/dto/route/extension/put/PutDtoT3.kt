package me.learning.api_schema.dto.route.extension.put

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.extension.throwOnFileReqBody
import me.learning.api_schema.extension.throwOnInvalidTypeOfPathVariable
import me.learning.api_schema.extension.throwOnMethodGetRequestBody
import me.learning.api_schema.extension.throwOnMultipleProp
import kotlin.reflect.KClass

data class PutDtoT3<T1 : Any, T2 : Any, T3 : Any>(
    val route: Route,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>,
    val p3: Pair<KClass<T3>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second, p3.second)

        MethodEnum.GET.throwOnMethodGetRequestBody(path, properties)
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.REQUEST_BODY, "request body")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): PutDtoT4<T1, T2, T3, T> =
        PutDtoT4(
            route,
            path,
            p1,
            p2,
            p3,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): PutDtoT4<T1, T2, T3, T> {
        MethodEnum.PUT.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }

    inline fun <reified T : Any> requestBody(): PutDtoT4<T1, T2, T3, T> {
        MethodEnum.PUT.throwOnFileReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }
}
