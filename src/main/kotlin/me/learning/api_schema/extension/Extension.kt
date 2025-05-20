package me.learning.api_schema.extension

import io.ktor.http.content.PartData
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import me.learning.api_schema.common.Helper.badRequest
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.utils.JacksonObjMapper.objectMapper
import me.learning.api_schema.utils.fromMapToClass
import java.io.File
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

inline fun <reified T> Map<*, *>.ct(): T = objectMapper.fromMapToClass<T>(this)

fun <T> Map<*, *>.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T> Any.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T : Any> KClass<T>.asKType(): KType {
    return this.java.kotlin.createType()
}

suspend fun PartData.FileItem.getRequest(existed: Boolean, extensions: List<String>): FileInfoReq? {
    if (this.name != "file") return null
    if (existed) badRequest("Invalid request duplicate file")

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
        size = file.length(),
    )
}

inline fun <reified T> PartData.FormItem.getRequest(existed: Boolean): T? {
    if (this.name != "data") return null
    if (existed) badRequest("Invalid request duplicate data")

    val data = this.name ?: badRequest("data must not be empty")

    return try {
        data.ct(T::class.java)
    } catch (e: Exception) {
        badRequest("Invalid data type, ${e.localizedMessage}")
    }
}
