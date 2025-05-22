package me.learning.api_schema.dto.handler

import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.request.FileDataInfoReq
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.dto.request.FilesDataInfoReq
import java.io.File
import kotlin.reflect.KClass

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

fun <T : Any> KClass<T>.throwOnFileOrListTypeReqBody(method: MethodEnum, path: String) {
    this.throwOnFileReqBody(MethodEnum.GET, path)
    when (this) {
        List::class -> "list type"
        else -> return throwOnFileReqBody(MethodEnum.GET, path)
    }.let { throw IllegalArgumentException("Route path[$path], method[$method]: Unsupported request body $it") }
}

fun <T : Any> KClass<T>.throwOnFileReqBody(method: MethodEnum, path: String) {
    when (this) {
        FilesDataInfoReq::class -> "file type"
        FileDataInfoReq::class -> "file type"
        FileInfoReq::class -> "file type"
        File::class -> "file type"
        else -> return
    }.let { throw IllegalArgumentException("Route path[$path], method[$method]: Unsupported request body $it") }
}
