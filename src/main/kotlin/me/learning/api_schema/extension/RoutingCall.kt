package me.learning.api_schema.extension

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.*
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.common.Helper.badRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.request.FileDataReq
import me.learning.api_schema.dto.request.FileInfo
import me.learning.api_schema.dto.request.FilesDataReq
import java.util.Optional
import kotlin.collections.joinToString
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.getDefaultValue(value: String, param: String): T {
    return when (this) {
        Long::class -> value.toLongOrNull()
            ?: throw BadRequestException("Invalid Long value for parameter: $param")
        Int::class -> value.toIntOrNull()
            ?: throw BadRequestException("Invalid Int value for parameter: $param")
        String::class -> value
        else -> badRequest("Unsupported type $this for path parameters")
    } as T
}

suspend fun <T : Any> receiveRequestBody(clazzName: String?, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        when (e) {
            is RequestValidationException -> badRequest(e.reasons.minOf { it })
            else -> {
                e.cause?.cause.isMismatchException()
                e.cause.isMismatchException()

                print(">>> Invalid body request: : $clazzName")
                badRequest("The body request is invalid")
            }
        }
    }
}

inline fun <reified T : Any> RoutingCall.getPathVariable(param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return T::class.getDefaultValue(value, param)
}

fun <T : Any> RoutingCall.getPathVariable(clazz: KClass<T>, param: String): T {
    val value = this.parameters[param]
        ?: badRequest("Missing path variable: $param")

    return clazz.getDefaultValue(value, param)
}

fun Throwable?.isMismatchException() = when (this) {
    is MismatchedInputException -> {
        val field = path.map { it.fieldName }.let { fields ->
            if (fields.size > 1) fields.joinToString(", ", "[", "]") { it }
            else fields.firstOrNull() ?: ""
        }
        badRequest("$field is missing")
    }
    else -> {}
}

suspend inline fun <reified T : Any> RoutingCall.requestBody(): T {
    return receiveRequestBody(T::class.simpleName) { receive<T>() }
}

suspend fun <T : Any> RoutingCall.requestBody(clazz: KClass<T>): T {
    return receiveRequestBody(clazz.simpleName) { receive(clazz) }
}


suspend inline fun <reified T : Any, reified I : Any> RoutingCall.fileInfoRequest(path: String, extensions: List<String>): T {
    val files = mutableListOf<FileInfo>()
    val isVoidData = I::class == Void::class
    var data: I? = null
    MethodEnum.POST.throwOnFileOrListTypeReqBody<I>(path)
    val isValidClassType = T::class in listOf(FileDataReq::class, FilesDataReq::class)
    if (!isValidClassType) throw IllegalArgumentException("Route path[$path], method[${MethodEnum.POST}]: Unsupported class type ${T::class}")

    val fileAsList = T::class == FilesDataReq::class

    receiveMultipart().forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val existed = when (true) {
                    fileAsList -> false
                    else -> files.isNotEmpty()
                }
                part.getRequest(existed, extensions, fileAsList)?.let(files::add)
            }
            is PartData.FormItem -> if (!isVoidData) part.getRequest<I>(data != null)?.let { data = it }
            else -> {}
        }
        part.dispose()
    }

    val value = when (true) {
        (null is I) -> data?.let { Optional.of(it) } ?: Optional.empty()
        isVoidData -> data?.let { Optional.of(it) } ?: Optional.empty()
        (data != null) -> Optional.of(data)
        else -> badRequest("Invalid request file cannot be empty")
    }

    return when (isVoidData) {
        true -> FilesDataReq(files, value)
        false -> {
            val file = files.firstOrNull() ?: badRequest("Invalid request file cannot be empty")
            FileDataReq(file, value)
        }
    } as T
}


suspend inline fun <reified T : Any> RoutingCall.prop(
    pair: Pair<KClass<T>, RoutePropertyEnum>,
    path: String = "",
    pathVarIndex: Int = 0,
    method: MethodEnum = MethodEnum.POST
): Pair<T, Int> {
    return when (pair.second) {
        RoutePropertyEnum.AUTH -> auth(pair.first) to pathVarIndex

        RoutePropertyEnum.REQUEST_BODY -> {
            method.throwOnFileOrListTypeReqBody<T>(path)
            requestBody(pair.first) to pathVarIndex
        }

        RoutePropertyEnum.PATH_VARIABLE -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            getPathVariable(pair.first, param) to pathVarIndex.plus(1)
        }

        RoutePropertyEnum.REQUEST_FILE -> {
            throw IllegalArgumentException("To implement")
//            val result = when (pair.first) {
//                FileInfo::class -> fileInfoRequest<FileInfo, Void>(path, listOf())
//                FileDataInfoReq::class -> fileInfoRequest<FileDataInfoReq<Any>, Any>(path, listOf())
//                FilesDataInfoReq::class -> fileInfoRequest<FilesDataInfoReq<Any>, Any>(path, listOf())
//                else -> fileInfoRequest<List<FileInfo>, Void>(path, listOf())
//            } as T
//
//            result to pathVarIndex
        }
    }
}
