package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpHeaders
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import me.learning.api_schema.extension.auth
import me.learning.api_schema.core.schemaBuilder
import java.io.File

inline fun Route.GETFILE(
    path: String = "",
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend Route.() -> File
): Route {
    return this.get(path, schemaBuilder<Any>()) {
        val file = block()
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (deleteAfterFinish) { file.delete() }
    }
}

inline fun <reified T : Any> Route.GETFILE(
    path: String = "",
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend Route.(auth: T) -> File
): Route {
    return this.get(path, schemaBuilder<Any>()) {
        val auth = call.auth<T>()
        val file = block(auth)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (deleteAfterFinish) { file.delete() }
    }
}
