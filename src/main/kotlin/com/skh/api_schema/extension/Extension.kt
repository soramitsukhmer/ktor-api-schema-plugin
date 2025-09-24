package com.skh.api_schema.extension

import io.ktor.http.content.PartData
import com.skh.api_schema.common.Helper.badRequest
import com.skh.api_schema.common.RouteFormDataPropEnum
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.dto.request.FileInfoReq
import com.skh.api_schema.dto.route.inline.RouteProp
import com.skh.api_schema.dto.route.inline.impl.RequestBody
import com.skh.api_schema.utils.JacksonObjMapper.objectMapper
import com.skh.api_schema.utils.fromMapToClass
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

fun <T> Map<*, *>.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T> Any.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T : Any> KClass<T>.asKType(): KType {
    return this.java.kotlin.createType()
}

suspend fun PartData.FileItem.getRequest(
    existed: Boolean,
    extensions: List<String>,
    asList: Boolean
): FileInfoReq? {
    if (asList && this.name != "files") return null
    if (!asList && this.name != "file") return null
    if (existed) badRequest("Invalid request duplicate file")
    if (this.contentType == null) badRequest("file must not empty")

    val extension = this.contentType?.contentSubtype ?: badRequest("Invalid file extension")

    if (extensions.isNotEmpty()) {
        extension.lowercase() in extensions.map { it.lowercase() } || badRequest("Invalid file extension $extensions")
    }

    val filename = this.originalFileName ?: badRequest("Invalid file name")
    val file = File.createTempFile("tfs-tmp--", "--${UUID.randomUUID()}.$extension")
    this.provider().copyAndClose(file.writeChannel())
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

inline fun <reified T> isListType() = T::class == List::class

fun Long.toStringMB() = "${((this * 1024L) * 1024L)} MB"