package com.skh.api_schema.extension

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.routing.*
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.common.Helper.badRequest
import com.skh.api_schema.common.Helper.clean
import com.skh.api_schema.common.Helper.extractAllPathParameters
import com.skh.api_schema.common.Helper.megaByteToByte
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.dto.handler.FileSizeExceededException
import com.skh.api_schema.dto.handler.MaxRequestFileItemException
import com.skh.api_schema.dto.request.FileInfoReq
import java.io.File
import kotlin.Long
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
    return try { block() }
    catch (e: Exception) {
        when (e) {
            is RequestValidationException -> badRequest(e.reasons.minOf { it })
            else -> {
                e.cause?.cause?.cause.throwable()
                e.cause?.cause.throwable()
                e.cause.throwable()
                e.throwable()

                print(">>> Invalid request body: $clazzName")
                badRequest("The request body is invalid")
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

fun Throwable?.throwable() = when (this) {

    is BadRequestException -> badRequest(message ?: localizedMessage)

    is IllegalArgumentException -> badRequest(message ?: localizedMessage)

    is InvalidFormatException -> {
        val msg = message ?: localizedMessage
        val fieldPattern = """\["([^"]+)"\]""".toRegex()
        val valuePattern = """from String "([^"]+)"""".toRegex()

        val field = fieldPattern.findAll(msg)
            .map { it.groupValues[1] }
            .joinToString(".")
        val value = valuePattern.find(msg)?.groupValues?.get(1) ?: "unknown"

        badRequest("Invalid request value: [$value] of field: $field")
    }

    is MismatchedInputException -> {
        val field = path.mapNotNull { it.fieldName }.joinToString(".")
        badRequest("The $field field must not be blank.")
    }

    else -> {}
}

suspend inline fun <reified T : Any> RoutingCall.requestBody(): T {
    return receiveRequestBody(T::class.simpleName) { receive<T>() }
}

suspend fun <T : Any> RoutingCall.requestBody(clazz: KClass<T>): T {
    return receiveRequestBody(clazz.simpleName) { receive(clazz) }
}

suspend inline fun <reified T> RoutingCall.getFileDataRequest(
    fileAsList: Boolean,
    extensions: List<String>,
    maxMB: Long,
    limit: Int
): Pair<List<FileInfoReq>, T?> {
    val files = mutableListOf<FileInfoReq>()
    var data: T? = null
    var fileCount = 0

    val requiresData = when (T::class) {
        Unit::class, Void::class, Nothing::class -> false
        else -> true
    }

    val hasFileLimit = limit > 0
    val multiPartSize = (maxMB * 2).megaByteToByte()
    var isCleanFile = true

    try {
        receiveMultipart(formFieldLimit = multiPartSize).forEachPart { part ->
            try {
                when (part) {
                    is PartData.FileItem -> {
                        val fileExisted = when (fileAsList) {
                            true -> false
                            else -> files.isNotEmpty()
                        }
                        part.getRequest(fileExisted, extensions, fileAsList, maxMB)?.let(files::add)
                        if (hasFileLimit && ++fileCount > limit) {
                            throw MaxRequestFileItemException("Files request must not exceed $limit items")
                        }
                    }

                    is PartData.FormItem -> { if (requiresData && data == null) { data = part.getRequest<T>(false) } }

                    else -> {}
                }
            } finally {
                part.dispose()
            }
        }

        val value = if (requiresData) { data ?: badRequest("Invalid request data cannot be empty") } else data

        return when (fileAsList) {
            true -> {
                isCleanFile = false
                Pair(files, value)
            }
            else -> {
                val file = files.firstOrNull() ?: badRequest("Invalid request file cannot be empty")
                isCleanFile = false
                Pair(listOf(file), value)
            }
        }
    } catch (e: MaxRequestFileItemException) {
        badRequest(e.message)
    } catch (e: FileSizeExceededException) {
        badRequest(e.message)
    } finally {
        files.clean(isCleanFile)
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
            method.throwOnFileReqBody<T>(path)
            requestBody(pair.first) to pathVarIndex
        }

        RoutePropEnum.PATH_VARIABLE -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            getPathVariable(pair.first, param) to pathVarIndex.plus(1)
        }
    }
}

suspend inline fun <reified T : Any> RoutingCall.prop(
    enum: RoutePropEnum,
    path: String = "",
    pathVarIndex: Int = 0,
    method: MethodEnum = MethodEnum.POST
): Pair<T, Int> {
    return when (enum) {
        RoutePropEnum.AUTH -> auth<T>() to pathVarIndex

        RoutePropEnum.REQUEST_BODY -> {
            method.throwOnFileReqBody<T>(path)
            requestBody<T>() to pathVarIndex
        }

        RoutePropEnum.PATH_VARIABLE -> {
            val allPathVar = extractAllPathParameters(path)
            val param = allPathVar.getOrNull(pathVarIndex) ?: ""
            getPathVariable<T>(param) to pathVarIndex.plus(1)
        }
    }
}

fun RoutingCall.setFileHeader(file: File) {
    response.header(HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name).toString())
    response.header(HttpHeaders.ContentType, file.determineContentType())
}
