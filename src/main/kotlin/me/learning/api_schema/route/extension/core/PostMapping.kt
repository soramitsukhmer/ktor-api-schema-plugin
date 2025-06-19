package me.learning.api_schema.route.extension.core

import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.dto.route.extension.file.PostFileDtoT1
import me.learning.api_schema.dto.route.extension.file.PostFileDtoT2
import me.learning.api_schema.dto.route.extension.file.PostFileDtoT3
import me.learning.api_schema.dto.route.extension.post.PostDto
import me.learning.api_schema.dto.route.extension.post.PostDtoT1
import me.learning.api_schema.dto.route.extension.post.PostDtoT2
import me.learning.api_schema.dto.route.extension.tuple.Tuple2
import me.learning.api_schema.dto.route.extension.tuple.Tuple3
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.cleanRoutePath
import me.learning.api_schema.extension.getFileDataRequest
import me.learning.api_schema.extension.getFileRequest
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop


inline fun <reified T> PostDto.map(
    crossinline block: suspend RoutingRequest.() -> T
): Route {
    val builder = route.schemaBuilder<T>()
    return route.post(path.cleanRoutePath(), builder) {
        call.ok(call.request.block())
    }
}

inline fun <reified T, reified T1 : Any> PostDtoT1<T1>.map(
    crossinline block: suspend RoutingRequest.(v1: T1) -> T
): Route {
    val requestBody = p1.first.takeIf { p1.second.isRequestBody() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody)
    return route.post(path.cleanRoutePath(), builder) {
        val v1 = call.prop(p1, path).first
        call.ok(call.request.block(v1))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any> PostDtoT2<T1, T2>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> T
): Route {
    val requestBody = p2.first.takeIf { p2.second.isRequestBody() } ?: p1.first.takeIf { p1.second.isRequestBody() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody)
    return route.post(path.cleanRoutePath(), builder) {
        val v1 = call.prop(p1, path).first
        val v2 = call.prop(p2, path).first
        call.ok(call.request.block(Tuple2(v1, v2)))
    }
}

// ============== File Builder ==============

@JvmName("PostFileBuilderT1_FileInfoReq")
inline fun <reified T> PostFileDtoT1<FileInfoReq>.map(
    crossinline block: suspend RoutingRequest.(v1: FileInfoReq) -> T
): Route {
    val builder = route.schemaBuilder<T>(bodyAsFormData = true, bodyFileAsList = false)
    return route.post(path.cleanRoutePath(), builder) {
        val files = call.getFileRequest(true, extensions)

        call.ok(call.request.block(files.first()))
        if (removeFileAfterProcessing) files.forEach { it.file.delete() }
    }
}

@JvmName("PostFileBuilderT1_List_FileInfoReq")
inline fun <reified T> PostFileDtoT1<List<FileInfoReq>>.map(
    crossinline block: suspend RoutingRequest.(v1: List<FileInfoReq>) -> T
): Route {
    val builder = route.schemaBuilder<T>(bodyAsFormData = true, bodyFileAsList = true)
    return route.post(path.cleanRoutePath(), builder) {
        val files = call.getFileRequest(true, extensions)

        call.ok(call.request.block(files))
        if (removeFileAfterProcessing) files.forEach { it.file.delete() }
    }
}

@JvmName("PostFileBuilderT2_FileInfoReq")
inline fun <reified T, reified T2 : Any> PostFileDtoT2<FileInfoReq, T2>.map(
    crossinline block: RoutingRequest.(t2: Tuple2<FileInfoReq, T2>) -> T
): Route {
    val requestBody = p2.first.takeIf { p2.second.isText() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody, bodyAsFormData = true, bodyFileAsList = false)
    return route.post(path.cleanRoutePath(), builder) {
        val tuple2 = when (p2.second.isText()) {
            true -> call.getFileDataRequest<T2>(false, extensions).let { Tuple2(it.first.first(), it.second) }
            false -> call.getFileRequest(false, extensions).first().let { Tuple2(it, call.auth<T2>()) }
        }

        call.ok(call.request.block(tuple2))
        if (removeFileAfterProcessing) tuple2.t1.file.delete()
    }
}

@JvmName("PostFileBuilderT2_List_FileInfoReq")
inline fun <reified T, reified T2 : Any> PostFileDtoT2<List<FileInfoReq>, T2>.map(
    crossinline block: RoutingRequest.(t2: Tuple2<List<FileInfoReq>, T2>) -> T
): Route {
    val requestBody = p2.first.takeIf { p2.second.isText() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody, bodyAsFormData = true, bodyFileAsList = true)
    return route.post(path.cleanRoutePath(), builder) {
        val tuple2 = when (p2.second.isText()) {
            true -> call.getFileDataRequest<T2>(false, extensions).let { Tuple2(it.first, it.second) }
            false -> Tuple2(call.getFileRequest(false, extensions), call.auth<T2>())
        }

        call.ok(call.request.block(tuple2))
        if (removeFileAfterProcessing) tuple2.t1.forEach { it.file.delete() }
    }
}

@JvmName("PostFileBuilderT3_FileInfoReq")
inline fun <reified T, reified T2 : Any, reified T3 : Any> PostFileDtoT3<FileInfoReq, T2, T3>.map(
    crossinline block: RoutingRequest.(t2: Tuple3<FileInfoReq, T2, T3>) -> T
): Route {
    val requestBody = p2.first.takeIf { p2.second.isText() } ?: p3.first.takeIf { p3.second.isText() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody, bodyAsFormData = true, bodyFileAsList = false)
    return route.post(path.cleanRoutePath(), builder) {
        val tuple3 = when (p2.second.isText()) {
            true -> call.getFileDataRequest<T2>(false, extensions)
                .let { Tuple3(it.first.first(), it.second, call.auth<T3>()) }
            false -> call.getFileDataRequest<T3>(false, extensions)
                .let { Tuple3(it.first.first(), call.auth<T2>(), it.second) }
        }

        call.ok(call.request.block(tuple3))
        if (removeFileAfterProcessing) tuple3.t1.file.delete()
    }
}

@JvmName("PostFileBuilderT3_List_FileInfoReq")
inline fun <reified T, reified T2 : Any, reified T3 : Any> PostFileDtoT3<List<FileInfoReq>, T2, T3>.map(
    crossinline block: RoutingRequest.(t2: Tuple3<List<FileInfoReq>, T2, T3>) -> T
): Route {
    val requestBody = p2.first.takeIf { p2.second.isText() } ?: p3.first.takeIf { p3.second.isText() }
    val builder = route.schemaBuilder<T>(requestBody = requestBody, bodyAsFormData = true, bodyFileAsList = true)
    return route.post(path.cleanRoutePath(), builder) {
        val tuple3 = when (p2.second.isText()) {
            true -> call.getFileDataRequest<T2>(false, extensions)
                .let { Tuple3(it.first, it.second, call.auth<T3>()) }
            false -> call.getFileDataRequest<T3>(false, extensions)
                .let { Tuple3(it.first, call.auth<T2>(), it.second) }
        }

        call.ok(call.request.block(tuple3))
        if (removeFileAfterProcessing) tuple3.t1.forEach { it.file.delete() }
    }
}
