package me.learning.api_schema.dto.handler

import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.request.FileDataReq
import me.learning.api_schema.dto.request.FileInfo
import me.learning.api_schema.dto.request.FilesDataReq
import me.learning.api_schema.extension.ifTypeListOFFileInfoReq
import java.io.File

class UnauthorizedAuthException(override val message: String) : RuntimeException(message)

class InvalidAuthException(override val message: String) : RuntimeException(message)

fun MethodEnum.throwOnMultipleProp(path: String, properties: List<RoutePropertyEnum>, onProp: RoutePropertyEnum, tag: String) {
    properties
        .filter { it == onProp }
        .takeIf { it.size > 1 }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported multiple $tag") }
}

fun MethodEnum.throwOnMethodGetRequestBody(path: String, properties: List<RoutePropertyEnum>) {
    if (this != MethodEnum.GET) return
    properties
        .find { it == RoutePropertyEnum.REQUEST_BODY }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body") }
}

inline fun <reified T : Any> MethodEnum.throwOnFileOrListTypeReqBody(path: String) {
    throwOnFileReqBody<T>(path)
    when (T::class) {
        List::class -> "list type"
        else -> return MethodEnum.GET.throwOnFileReqBody<T>(path)
    }.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body $it") }
}

inline fun <reified T : Any> MethodEnum.throwOnFileReqBody(path: String) {
    when (T::class) {
        FilesDataReq::class -> "file type"
        FileDataReq::class -> "file type"
        FileInfo::class -> "file type"
        File::class -> "file type"
        else -> return
    }.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body $it") }
}

inline fun <reified T : Any> MethodEnum.throwOnNotFileReqBody(path: String) {
    when (T::class) {
        FileDataReq::class -> return
        FilesDataReq::class -> return
        FileInfo::class -> return
        else -> {
            if (ifTypeListOFFileInfoReq<T>()) return

            val clazz = listOf("FilesDataInfoReq", "FileDataInfoReq", "FileInfoReq", "List<FileInfoReq>")
            throw IllegalArgumentException("Route path[$path], method[$this]: Supported only class type $clazz")
        }
    }
}
