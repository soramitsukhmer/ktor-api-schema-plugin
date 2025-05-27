package me.learning.api_schema.dto.route.post

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RouteFormDataPropEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.handler.throwOnNotFileInfoReqBody
import me.learning.api_schema.dto.route.file.PostFileDtoT1
import kotlin.reflect.KClass

data class PostDto(
    val route: Route,
    val path: String
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>) = PostDtoT1(
        route,
        path,
        pair
    )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> requestBody(): PostDtoT1<T> {
        MethodEnum.POST.throwOnFileOrListTypeReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }

    inline fun <reified T : Any> file(extension: List<String> = emptyList(), removeFileAfterProcessing: Boolean = false): PostFileDtoT1<T> {
        MethodEnum.POST.throwOnNotFileInfoReqBody<T>(path)
        val enum = when (T::class) {
            List::class -> RouteFormDataPropEnum.FILES
            else -> RouteFormDataPropEnum.FILE
        }
        return PostFileDtoT1(
            route,
            path,
            extension,
            removeFileAfterProcessing,
            Pair(T::class, enum)
        )
    }
}
