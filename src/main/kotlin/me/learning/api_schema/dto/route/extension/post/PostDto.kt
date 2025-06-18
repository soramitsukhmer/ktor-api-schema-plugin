package me.learning.api_schema.dto.route.extension.post

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RouteFormDataPropEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.dto.route.extension.file.PostFileDtoT1
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

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

    fun file(extension: List<String> = emptyList(), removeFileAfterProcessing: Boolean = false) = PostFileDtoT1(
        route,
        path,
        extension,
        removeFileAfterProcessing,
        FileInfoReq::class to RouteFormDataPropEnum.FILE
    )

    @Suppress("UNCHECKED_CAST")
    fun files(extension: List<String> = emptyList(), removeFileAfterProcessing: Boolean = false) = PostFileDtoT1(
        route,
        path,
        extension,
        removeFileAfterProcessing,
        (typeOf<List<FileInfoReq>>().classifier as KClass<List<FileInfoReq>>) to RouteFormDataPropEnum.FILES
    )
}
