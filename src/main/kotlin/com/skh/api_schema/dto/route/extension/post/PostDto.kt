package com.skh.api_schema.dto.route.extension.post

import io.ktor.server.routing.Route
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.common.RouteFormDataPropEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.extension.throwOnFileReqBody
import com.skh.api_schema.dto.request.FileInfoReq
import com.skh.api_schema.dto.route.extension.file.PostFileDtoT1
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

data class PostDto(
    val route: Route,
    val path: String,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val accessRights: List<String>
) {
    fun <T : Any> addProp(pair: Pair<KClass<T>, RoutePropEnum>) = PostDtoT1(
        route,
        path,
        responseWrapper,
        hidden,
        accessRights,
        pair
    )

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RoutePropEnum.AUTH))

    inline fun <reified T : Any> requestBody(): PostDtoT1<T> {
        MethodEnum.POST.throwOnFileReqBody<T>(path)
        return addProp(Pair(T::class, RoutePropEnum.REQUEST_BODY))
    }

    fun file(extension: List<String> = emptyList(), removeFileAfterProcessing: Boolean = false) = PostFileDtoT1(
        route,
        path,
        extension,
        removeFileAfterProcessing,
        responseWrapper,
        hidden,
        accessRights,
        FileInfoReq::class to RouteFormDataPropEnum.FILE
    )

    @Suppress("UNCHECKED_CAST")
    fun files(extension: List<String> = emptyList(), removeFileAfterProcessing: Boolean = false) = PostFileDtoT1(
        route,
        path,
        extension,
        removeFileAfterProcessing,
        responseWrapper,
        hidden,
        accessRights,
        (typeOf<List<FileInfoReq>>().classifier as KClass<List<FileInfoReq>>) to RouteFormDataPropEnum.FILES
    )
}
