package me.learning.api_schema.extension

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.*
import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.common.Helper.badRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.request.FileInfoReq
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

suspend fun <T> receiveRequestBody(clazzName: String?, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        when (e) {
            is RequestValidationException -> badRequest(e.reasons.minOf { it })
            else -> {
                e.cause?.cause.isMismatchException()
                e.cause.isMismatchException()
                e.isMismatchException()

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

suspend fun RoutingCall.getFileRequest(fileAsList: Boolean, extensions: List<String>): List<FileInfoReq> {
    val files = mutableListOf<FileInfoReq>()

    receiveMultipart().forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val fileExisted = when (fileAsList) {
                    true -> false
                    else -> files.isNotEmpty()
                }
                part.getRequest(fileExisted, extensions, fileAsList)?.let(files::add)
            }
            else -> {}
        }
        part.dispose()
    }

    return when (fileAsList) {
        true -> files
        else -> {
            val file = files.firstOrNull() ?: badRequest("Invalid request file cannot be empty")
            listOf(file)
        }
    }
}

suspend inline fun <reified T> RoutingCall.getFileDataRequest(fileAsList: Boolean, extensions: List<String>): Pair<List<FileInfoReq>, T> {
    val files = mutableListOf<FileInfoReq>()
    var data: T? = null

    receiveMultipart().forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val fileExisted = when (fileAsList) {
                    true -> false
                    else -> files.isNotEmpty()
                }
                part.getRequest(fileExisted, extensions, fileAsList)?.let(files::add)
            }
            is PartData.FormItem -> part.getRequest<T>(data != null)?.let { data = it }
            else -> {}
        }
        part.dispose()
    }

    val value = data ?: badRequest("Invalid request data cannot be empty")

    return when (fileAsList) {
        true -> Pair(files, value)
        else -> {
            val file = files.firstOrNull() ?: badRequest("Invalid request file cannot be empty")
            Pair(listOf(file), value)
        }
    }
}

suspend inline fun <reified T : Any> RoutingCall.prop(
    pair: Pair<KClass<T>, RoutePropEnum>,
    path: String = "",
    pathVarIndex: Int = 0,
    method: MethodEnum = MethodEnum.POST
): Pair<T, Int> {
    return when (pair.second) {
        RoutePropEnum.AUTH -> auth(pair.first) to pathVarIndex

        RoutePropEnum.REQUEST_BODY -> {
            method.throwOnFileOrListTypeReqBody<T>(path)
            requestBody(pair.first) to pathVarIndex
        }

        RoutePropEnum.PATH_VARIABLE -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            getPathVariable(pair.first, param) to pathVarIndex.plus(1)
        }
    }
}
