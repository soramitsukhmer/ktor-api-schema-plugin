package me.learning.api_schema.dto.route.post

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.handler.throwUnsupportedWhenPropExistedOnMethod
import kotlin.reflect.KClass

data class PostDtoT1<T1 : Any>(
    val route: Route,
    val path: String,
    val p1: Pair<KClass<T1>, RoutePropEnum>
) {

    init {
        MethodEnum.POST.throwUnsupportedWhenPropExistedOnMethod(path, listOf(p1.second), RoutePropEnum.PATH_VARIABLE)
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>): PostDtoT2<T1, T> =
        PostDtoT2(
            route,
            path,
            p1,
            pair
        )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> requestBody(): PostDtoT2<T1, T> {
        MethodEnum.POST.throwOnFileOrListTypeReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }
}
