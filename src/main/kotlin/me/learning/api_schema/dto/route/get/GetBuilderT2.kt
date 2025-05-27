package me.learning.api_schema.dto.route.get

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnInvalidTypeOfPathVariable
import me.learning.api_schema.dto.handler.throwOnMethodGetRequestBody
import me.learning.api_schema.dto.handler.throwOnMultipleProp
import kotlin.reflect.KClass

data class GetBuilderT2<T1 : Any, T2 : Any>(
    val route: Route,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropEnum>,
    val p2: Pair<KClass<T2>, RoutePropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second)

        MethodEnum.GET.throwOnMethodGetRequestBody(path, properties)
        MethodEnum.GET.throwOnMultipleProp(path, properties, RoutePropEnum.AUTH, "auth")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): GetBuilderT3<T1, T2, T> =
        GetBuilderT3(
            route,
            path,
            p1,
            p2,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> pathVariable(): GetBuilderT3<T1, T2, T> {
        MethodEnum.GET.throwOnInvalidTypeOfPathVariable(path, T::class)
        return addProp(Pair(T::class, RoutePropEnum.PATH_VARIABLE))
    }
}
