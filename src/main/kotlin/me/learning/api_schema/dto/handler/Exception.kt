package me.learning.api_schema.dto.handler

import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.extension.ifTypeListOFFileInfoReq
import java.io.File
import kotlin.reflect.KClass

class UnauthorizedAuthException(override val message: String) : RuntimeException(message)

class InvalidAuthException(override val message: String) : RuntimeException(message)

fun MethodEnum.throwOnMultipleProp(path: String, properties: List<RoutePropEnum>, onProp: RoutePropEnum, tag: String) {
    properties
        .filter { it == onProp }
        .takeIf { it.size > 1 }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported multiple $tag") }
}

fun MethodEnum.throwOnInvalidTypeOfPathVariable(path: String, kClass: KClass<*>) {
    val clazz = listOf(Long::class, Int::class, String::class)
    if (kClass !in clazz) throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported path variable class type ${kClass.simpleName}")
}

fun MethodEnum.throwOnMethodGetRequestBody(path: String, properties: List<RoutePropEnum>) {
    if (this != MethodEnum.GET) return
    properties
        .find { it == RoutePropEnum.REQUEST_BODY }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body") }
}

fun MethodEnum.throwUnsupportedWhenPropExistedOnMethod(path: String, properties: List<RoutePropEnum>, onProp: RoutePropEnum) {
    properties
        .find { it == onProp }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported $onProp") }
}

inline fun <reified T> MethodEnum.throwOnFileOrListTypeReqBody(path: String) {
    throwOnFileReqBody<T>(path)
    when (T::class) {
        List::class -> "list type"
        else -> return MethodEnum.GET.throwOnFileReqBody<T>(path)
    }.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body $it") }
}

inline fun <reified T> MethodEnum.throwOnFileReqBody(path: String) {
    when (T::class) {
        FileInfoReq::class -> "file type"
        File::class -> "file type"
        else -> return
    }.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body $it") }
}

inline fun <reified T : Any> MethodEnum.throwOnNotFileInfoReqBody(path: String) {
    when (T::class) {
        FileInfoReq::class -> return
        else -> {
            if (ifTypeListOFFileInfoReq<T>()) return

            val clazz = listOf("FilesDataInfoReq", "FileDataInfoReq", "FileInfoReq", "List<FileInfoReq>")
            throw IllegalArgumentException("Route path[$path], method[$this]: Supported only class type $clazz")
        }
    }
}
