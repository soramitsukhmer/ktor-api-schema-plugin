package me.learning.api_schema.route.extension.core

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpHeaders
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.get.GetDto
import me.learning.api_schema.dto.route.get.GetDtoT1
import me.learning.api_schema.dto.route.get.GetDtoT2
import me.learning.api_schema.dto.route.get.GetDtoT3
import me.learning.api_schema.dto.route.tuple.Tuple2
import me.learning.api_schema.dto.route.tuple.Tuple3
import me.learning.api_schema.extension.getApiSchemaBuilderProp
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop
import java.io.File

inline fun <reified T> GetDto.map(
    crossinline block: suspend RoutingRequest.() -> T
): Route {
    val builder = route.schemaBuilder<T>()
    return route.get(path, builder) {
        call.ok(call.request.block())
    }
}

inline fun <reified T, reified T1 : Any> GetDtoT1<T1>.map(
    crossinline block: suspend RoutingRequest.(v1: T1) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = route.schemaBuilder<T>(pathVariable = variable)
    return route.get(path, builder) {
        val v1 = call.prop(p1, path).first
        call.ok(call.request.block(v1))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any> GetDtoT2<T1, T2>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2)).first
    val builder = route.schemaBuilder<T>(pathVariable = variable)
    return route.get(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        call.ok(call.request.block(Tuple2(v1, v2)))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any> GetDtoT3<T1, T2, T3>.map(
    crossinline block: suspend RoutingRequest.(t3: Tuple3<T1, T2, T3>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2, p3)).first
    val builder = route.schemaBuilder<T>(pathVariable = variable)
    return route.get(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        call.ok(call.request.block(Tuple3(v1, v2, v3)))
    }
}

// ============== File Builder ==============

inline fun GetDto.map(
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.() -> File
): Route {
    val builder = route.schemaBuilder<Unit>()
    return route.get(path, builder) {
        val file = call.request.block()
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)
        call.ok(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

inline fun <reified T1 : Any> GetDtoT1<T1>.map(
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(v1: T1) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = route.schemaBuilder<Unit>(pathVariable = variable)
    return route.get(path, builder) {
        val v1 = call.prop(p1, path).first
        val file = call.request.block(v1)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)
        call.ok(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

inline fun <reified T1 : Any, reified T2 : Any> GetDtoT2<T1, T2>.map(
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2)).first
    val builder = route.schemaBuilder<Unit>(pathVariable = variable)
    return route.get(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        val file = call.request.block(Tuple2(v1, v2))
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)
        call.ok(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> GetDtoT3<T1, T2, T3>.map(
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(t3: Tuple3<T1, T2, T3>) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2, p3)).first
    val builder = route.schemaBuilder<Unit>(pathVariable = variable)
    return route.get(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        val file = call.request.block(Tuple3(v1, v2, v3))
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)
        call.ok(file)

        if (removeFileAfterProcessing) file.delete()
    }
}
