package me.learning.api_schema.dto.route.extension.put

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.extension.throwOnFileReqBody
import me.learning.api_schema.extension.throwOnInvalidTypeOfPathVariable
import kotlin.reflect.KClass

data class PutDtoT1<T1 : Any>(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val p1: Pair<KClass<T1>, RoutePropEnum>
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): PutDtoT2<T1, T> =
        PutDtoT2(
            route,
            path,
            responseWrapper,
            hidden,
            p1,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): PutDtoT2<T1, T> {
        MethodEnum.PUT.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }

    inline fun <reified T : Any> requestBody(): PutDtoT2<T1, T> {
        MethodEnum.PUT.throwOnFileReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }
}
