package com.skh.api_schema.extension

import io.ktor.http.content.PartData
import com.skh.api_schema.common.Helper.badRequest
import com.skh.api_schema.common.Helper.megaByteToByte
import com.skh.api_schema.common.RouteFormDataPropEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.dto.handler.FileSizeExceededException
import com.skh.api_schema.dto.request.FileInfoReq
import com.skh.api_schema.dto.route.inline.RouteProp
import com.skh.api_schema.dto.route.inline.impl.RequestBody
import com.skh.api_schema.utils.JacksonObjMapper.objectMapper
import com.skh.api_schema.utils.fromMapToClass
import io.ktor.utils.io.jvm.javaio.toInputStream
import jakarta.validation.ConstraintViolation
import java.io.File
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.text.replace

fun <T> Map<*, *>.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T> Any.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T : Any> KClass<T>.asKType(): KType {
    return this.java.kotlin.createType()
}

fun PartData.FileItem.getRequest(
    existed: Boolean,
    extensions: List<String>,
    asList: Boolean,
    maxMB: Long
): FileInfoReq? {
    if (asList && this.name != "files") return null
    if (!asList && this.name != "file") return null
    if (existed) badRequest("Invalid request duplicate file")
    if (this.contentType == null) badRequest("file must not empty")

    var totalBytesRead = 0L

    val fileName = this.originalFileName
    when {
        fileName.isNullOrBlank() -> badRequest("Filename is required")
        !fileName.contains(".") -> badRequest("File must have an extension")
        fileName.startsWith(".") -> badRequest("Invalid filename")
        fileName.endsWith(".") -> badRequest("Invalid file extension")
    }

    val extension = this.originalFileName?.substringAfterLast(".", "")?.lowercase() ?: badRequest("Invalid file extension")

    if (extensions.isNotEmpty()) {
        when (extension.lowercase()) {
            "svg+xml" -> extensions.any { s -> s.equals("svg", ignoreCase = true) || s.equals(extension, ignoreCase = true) }
            else -> extensions.any { s -> s.equals(extension, ignoreCase = true) }
        } || badRequest("Invalid file extension $extensions")
    }

    val filename = this.originalFileName ?: badRequest("Invalid file name")
    val file = File.createTempFile("tfs-tmp--", "--${UUID.randomUUID()}.$extension")

    this.provider().toInputStream().use { input ->
        file.outputStream().use { output ->
            val buffer = ByteArray(8192)
            var bytesRead = input.read(buffer)

            while (bytesRead != -1) {
                totalBytesRead += bytesRead

                // Check size limits during streaming
                if (totalBytesRead > maxMB.megaByteToByte()) {
                    throw FileSizeExceededException("File size exceeds the maximum limit of $maxMB MB")
                }

                output.write(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }
    }

    return FileInfoReq(
        file = file,
        contentType = this.contentType?.toString() ?: "application/octet-stream",
        originalName = filename,
        extension = extension,
        size = file.length()
    )
}

suspend inline fun <reified T> PartData.FormItem.getRequest(existed: Boolean): T? {
    if (this.name != "data") return null
    if (existed) badRequest("Invalid request duplicate data")
    if ((null is T) && value.isEmpty()) return null
    if (value.isEmpty()) badRequest("Invalid data must not empty")
    return receiveRequestBody(T::class.simpleName) { value.ct(T::class.java) }
}


inline fun <reified T : RouteProp<*>> isRequestBody() = T::class == RequestBody::class

fun isRequestBody(routeProp: KClass<*>) = routeProp::class == RequestBody::class

fun Pair<KClass<*>, RoutePropEnum>.isRequestBody() = second.isRequestBody()

fun Pair<KClass<*>, RouteFormDataPropEnum>.isText() = second.isText()

fun <T> Set<ConstraintViolation<T>>.messages() = map { e -> e.message.replace("{field}", e.propertyPath.toString()) }
