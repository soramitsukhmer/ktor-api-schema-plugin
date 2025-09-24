package com.skh.api_schema.common

import io.ktor.server.plugins.BadRequestException
import com.skh.api_schema.dto.handler.InvalidAuthException
import com.skh.api_schema.dto.request.FileInfoReq
import java.io.File
import java.nio.file.Files.delete

object Helper {

    fun extractAllPathParameters(path: String): List<String> {
        return Regex("""\{([^}]+)}""")
            .findAll(path)
            .map { it.groupValues[1] }
            .toList()
    }

    fun badRequest(message: String): Nothing = throw BadRequestException(message)

    fun invalidAuthentication(): Nothing = throw InvalidAuthException("invalid authentication")

    fun Long.megaByteToByte() = this * 1024 * 1024

    @JvmName("javaFileClean")
    fun File.clean(whenTrue: Boolean) {
        if (!whenTrue) return
        if (exists()) { delete() }
    }

    @JvmName("javaFilesClean")
    fun List<File>.clean(whenTrue: Boolean) {
        if (!whenTrue) return
        forEach { if (it.exists()) { it.delete() } }
    }

    @JvmName("fileInfoClean")
    fun FileInfoReq.clean(whenTrue: Boolean) {
        file.clean(whenTrue)
    }

    @JvmName("listFileInfoClean")
    fun List<FileInfoReq>.clean(whenTrue: Boolean) {
        map { it.file }.clean(whenTrue)
    }
}
