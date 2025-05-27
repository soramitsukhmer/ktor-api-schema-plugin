package me.learning.api_schema.dto.route.get

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnInvalidTypeOfPathVariable
import kotlin.reflect.KClass

data class GetDto(
    val route: Route,
    val path: String
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>) = GetDtoT1(
        route,
        path,
        pair
    )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): GetDtoT1<T> {
        MethodEnum.GET.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }
}
