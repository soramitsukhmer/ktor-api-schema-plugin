package me.learning.api_schema.route.extension.core

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.extension.get.GetDto
import me.learning.api_schema.dto.route.extension.get.GetDtoT1
import me.learning.api_schema.dto.route.extension.get.GetDtoT2
import me.learning.api_schema.dto.route.extension.get.GetDtoT3
import me.learning.api_schema.dto.route.extension.tuple.Tuple2
import me.learning.api_schema.dto.route.extension.tuple.Tuple3
import me.learning.api_schema.extension.cleanRoutePath
import me.learning.api_schema.extension.getApiSchemaBuilderProp
import me.learning.api_schema.extension.isRequestBody
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop
import me.learning.api_schema.extension.setFileHeader
import java.io.File

@JvmName("get_dto")
inline fun <reified T> GetDto.map(crossinline block: suspend RoutingRequest.() -> T): Route {
    val builder = route.schemaBuilder<T, Unit>(hidden, responseWrapper = responseWrapper)
    return route.get(path.cleanRoutePath(), builder) {
        call.ok(call.request.block(), responseWrapper)
    }
}

@JvmName("get_dto_t1")
inline fun <reified T, reified T1 : Any> GetDtoT1<T1>.map(crossinline block: suspend RoutingRequest.(v1: T1) -> T): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (p1.isRequestBody()) {
        true -> route.schemaBuilder<T, T1>(hidden, responseWrapper = responseWrapper)
        false -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val v1 = call.prop(p1, path).first
        call.ok(call.request.block(v1), responseWrapper)
    }
}

@JvmName("get_dto_t1_t2")
inline fun <reified T, reified T1 : Any, reified T2 : Any> GetDtoT2<T1, T2>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper)
        p2.isRequestBody() -> route.schemaBuilder<T, T2>(hidden, variable, responseWrapper = responseWrapper)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        call.ok(call.request.block(Tuple2(v1, v2)), responseWrapper)
    }
}

@JvmName("get_dto_t1_t2_t3")
inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any> GetDtoT3<T1, T2, T3>.map(
    crossinline block: suspend RoutingRequest.(t3: Tuple3<T1, T2, T3>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2, p3)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper)
        p2.isRequestBody() -> route.schemaBuilder<T, T2>(hidden, variable, responseWrapper = responseWrapper)
        p3.isRequestBody() -> route.schemaBuilder<T, T3>(hidden, variable, responseWrapper = responseWrapper)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        call.ok(call.request.block(Tuple3(v1, v2, v3)), responseWrapper)
    }
}

// ============== File Builder ==============

@JvmName("get_dto_file")
inline fun GetDto.map(
    removeFileAfterProcessing: Boolean,
    crossinline block: suspend RoutingRequest.() -> File
): Route {
    val builder = route.schemaBuilder<Unit, Unit>(hidden)
    return route.get(path.cleanRoutePath(), builder) {
        val file = call.request.block()
        call.setFileHeader(file)
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

@JvmName("get_dto_file_t1")
inline fun <reified T1 : Any> GetDtoT1<T1>.map(
    removeFileAfterProcessing: Boolean,
    crossinline block: suspend RoutingRequest.(v1: T1) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<Unit, T1>(hidden, variable)
        else -> route.schemaBuilder<Unit, Unit>(hidden, variable)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val v1 = call.prop(p1, path).first
        val file = call.request.block(v1)
        call.setFileHeader(file)
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

@JvmName("get_dto_file_t1_t2")
inline fun <reified T1 : Any, reified T2 : Any> GetDtoT2<T1, T2>.map(
    removeFileAfterProcessing: Boolean,
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<Unit, T1>(hidden, variable)
        p2.isRequestBody() -> route.schemaBuilder<Unit, T2>(hidden, variable)
        else -> route.schemaBuilder<Unit, Unit>(hidden, variable)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        val file = call.request.block(Tuple2(v1, v2))
        call.setFileHeader(file)
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}

@JvmName("get_dto_file_t1_t2_t3")
inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> GetDtoT3<T1, T2, T3>.map(
    removeFileAfterProcessing: Boolean,
    crossinline block: suspend RoutingRequest.(t3: Tuple3<T1, T2, T3>) -> File
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1, p2, p3)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<Unit, T1>(hidden, variable)
        p2.isRequestBody() -> route.schemaBuilder<Unit, T2>(hidden, variable)
        p3.isRequestBody() -> route.schemaBuilder<Unit, T3>(hidden, variable)
        else -> route.schemaBuilder<Unit, Unit>(hidden, variable)
    }

    return route.get(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        val file = call.request.block(Tuple3(v1, v2, v3))
        call.setFileHeader(file)
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}
