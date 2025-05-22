package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpHeaders
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.extension.auth
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.extension.getPathVariable
import java.io.File
import kotlin.reflect.KClass

inline fun Route.GETFILE(
    path: String = "",
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend Route.() -> File
): Route {
    return this.get(path, schemaBuilder<Void>()) {
        val file = block()
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (deleteAfterFinish) file.delete()
    }
}

inline fun <reified T : Any> Route.GETFILE(
    path: String = "",
    withAuth: Boolean = false,
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend Route.(param: T) -> File
): Route {
    var pathVar: Map<String, KClass<T>>? = null
    var pathVarT = ""

    if (!withAuth) {
        val allPathVar = extractAllPathParameters(path)
        pathVarT = allPathVar.firstOrNull() ?: ""
        pathVar = mapOf(pathVarT to T::class)
    }

    return this.get(path, schemaBuilder<Void>(pathVariable = pathVar)) {
        val param = when (withAuth) {
            true -> call.auth<T>()
            false -> call.getPathVariable<T>(pathVarT)
        }
        val file = block(param)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (deleteAfterFinish) file.delete()
    }
}

inline fun <reified T : Any, reified I : Any> Route.GETFILE(
    path: String = "",
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend Route.(auth: T, varI: I) -> File
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarI = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarI to I::class)

    return this.get(path, schemaBuilder<Void>(pathVariable = pathVar)) {
        val auth = call.auth<T>()
        val valueI = call.getPathVariable<I>(pathVarI)
        val file = block(auth, valueI)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (deleteAfterFinish) file.delete()
    }
}
