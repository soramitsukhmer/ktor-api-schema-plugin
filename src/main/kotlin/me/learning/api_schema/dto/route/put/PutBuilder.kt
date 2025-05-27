package me.learning.api_schema.dto.route.put

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.handler.throwOnInvalidTypeOfPathVariable
import kotlin.reflect.KClass

data class PutBuilder(
    val route: Route,
    val path: String
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>) = PutBuilderT1(
        route,
        path,
        pair
    )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): PutBuilderT1<T> {
        MethodEnum.PUT.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }

    inline fun <reified T : Any> requestBody(): PutBuilderT1<T> {
        MethodEnum.PUT.throwOnFileOrListTypeReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }
}
