package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.extension.ok
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.dto.handler.throwOnMultipleDataRequestBody
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.dto.route.extension.tuple.Tuple3
import me.learning.api_schema.dto.route.extension.tuple.Tuple4
import me.learning.api_schema.dto.route.extension.tuple.Tuple5
import me.learning.api_schema.dto.route.inline.RouteProp
import me.learning.api_schema.dto.route.inline.SchemaBuilderProp
import me.learning.api_schema.dto.route.inline.impl.RequestBody
import me.learning.api_schema.extension.getFileRequest
import me.learning.api_schema.extension.getFileDataRequest
import me.learning.api_schema.extension.isRequestBody
import me.learning.api_schema.extension.prop

// ================================================================================================================ //
// ================================================================================================================ //
// =====================                                                              ============================= //
// =====================                    BLOCK POST WITH FILE                      ============================= //
// ===================== to check local storage: System.getProperty("java.io.tmpdir") ============================= //
// =====================                                                              ============================= //
// ================================================================================================================ //
// ================================================================================================================ //


inline fun <reified T> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(file: FileInfoReq) -> T
): Route {
    val builder = schemaBuilder<T>(bodyAsFormData = true)

    return this.post(path, builder) {
        val request = call.getFileRequest(false, extensions).first()
        call.ok(call.request.block(request))
        if (removeFileAfterProcessing) request.file.delete()
    }
}


inline fun <reified T> Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(files: List<FileInfoReq>) -> T
): Route {
    val builder = schemaBuilder<T>(bodyAsFormData = true, bodyFileAsList = true)

    return this.post(path, builder) {
        val files = call.getFileRequest(true, extensions)
        call.ok(call.request.block(files))
        if (removeFileAfterProcessing) files.forEach { it.file.delete() }
    }
}


inline fun <reified T, reified V1 : Any, reified T1 : RouteProp<V1>> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, file: FileInfoReq) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)

    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, listOf(V1::class to T1::class))
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = false
    )

    return this.post(path, builder) {
        val (file, p1) = when (isRequestBody<T1>()) {
            true -> call.getFileDataRequest<V1>(false, extensions).let { it.first.first() to RequestBody(it.second) }
            false -> call.getFileRequest(false, extensions).first() to call.prop<V1, T1>(path).first
        }

        call.ok(call.request.block(p1 as T1, file))
        if (removeFileAfterProcessing) file.file.delete()
    }
}


inline fun <reified T, reified V1 : Any, reified T1 : RouteProp<V1>> Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, files: List<FileInfoReq>) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)

    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, listOf(V1::class to T1::class))
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = true
    )

    return this.post(path, builder) {
        val (files, p1) = when (isRequestBody<T1>()) {
            true -> call.getFileDataRequest<V1>(false, extensions).let { it.first to RequestBody(it.second) }
            false -> call.getFileRequest(false, extensions) to call.prop<V1, T1>(path).first
        }

        call.ok(call.request.block(p1 as T1, files))
        if (removeFileAfterProcessing) files.forEach { it.file.delete() }
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        > Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, file: FileInfoReq) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = false
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions)
                .let { Tuple3(RequestBody(it.second), call.prop<V2, T2>(path).first, it.first.first()) }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions)
                .let { Tuple3(call.prop<V1, T1>(path).first, RequestBody(it.second), it.first.first()) }
            else -> Tuple3(call.prop<V1, T1>(path).first, call.prop<V2, T2>(path).first, call.getFileRequest(false, extensions).first())
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3))
        if (removeFileAfterProcessing) t.t3.file.delete()
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        > Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, files: List<FileInfoReq>) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = true
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions)
                .let { Tuple3(RequestBody(it.second), call.prop<V2, T2>(path).first, it.first) }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions)
                .let { Tuple3(call.prop<V1, T1>(path).first, RequestBody(it.second), it.first) }
            else -> Tuple3(call.prop<V1, T1>(path).first, call.prop<V2, T2>(path).first, call.getFileRequest(false, extensions))
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3))
        if (removeFileAfterProcessing) t.t3.forEach { it.file.delete() }
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        > Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, file: FileInfoReq) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V3>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class, T3::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class, V3::class to T3::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = false
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions).let {
                val (p2, idx) = call.prop<V2, T2>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(RequestBody(it.second), p2, p3, it.first.first())
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(p1, RequestBody(it.second), p3, it.first.first())
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p2 = call.prop<V2, T2>(path, idx).first
                Tuple4(p1, p2, RequestBody(it.second), it.first.first())
            }
            else -> call.getFileRequest(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple4(p1, p2, p3, it.first())
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4))
        if (removeFileAfterProcessing) t.t4.file.delete()
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        > Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, file: List<FileInfoReq>) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V3>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class, T3::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class, V3::class to T3::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = true
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions).let {
                val (p2, idx) = call.prop<V2, T2>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(RequestBody(it.second), p2, p3, it.first)
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(p1, RequestBody(it.second), p3, it.first)
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p2 = call.prop<V2, T2>(path, idx).first
                Tuple4(p1, p2, RequestBody(it.second), it.first)
            }
            else -> call.getFileRequest(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple4(p1, p2, p3, it)
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4))
        if (removeFileAfterProcessing) t.t4.forEach { it.file.delete() }
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>,
        > Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, T4, file: FileInfoReq) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V3>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V4>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class, T3::class, T4::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class, V3::class to T3::class, V4::class to T4::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = false
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions).let {
                val (p2, idx2) = call.prop<V2, T2>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(RequestBody(it.second), p2, p3, p4, it.first.first())
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, RequestBody(it.second), p3, p4, it.first.first())
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx2).first
                Tuple5(p1, p2, RequestBody(it.second), p4, it.first.first())
            }
            isRequestBody<T4>() -> call.getFileDataRequest<V4>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple5(p1, p2, p3, RequestBody(it.second), it.first.first())
            }
            else -> call.getFileRequest(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, p2, p3, p4, it.first())
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4 as T4, t.t5))
        if (removeFileAfterProcessing) t.t5.file.delete()
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>,
        > Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, T4, files: List<FileInfoReq>) -> T
): Route {
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V1>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V2>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V3>(path)
    MethodEnum.POST.throwOnFileOrListTypeReqBody<V4>(path)
    MethodEnum.POST.throwOnMultipleDataRequestBody(path, listOf(T1::class, T2::class, T3::class, T4::class))

    val collection = listOf(V1::class to T1::class, V2::class to T2::class, V3::class to T3::class, V4::class to T4::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(path, collection)
    val builder = schemaBuilder<T>(
        pathVariable = prop.pathVariable,
        requestBody = prop.requestBody,
        bodyAsFormData = true,
        bodyFileAsList = true
    )

    return this.post(path, builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions).let {
                val (p2, idx2) = call.prop<V2, T2>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(RequestBody(it.second), p2, p3, p4, it.first)
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, RequestBody(it.second), p3, p4, it.first)
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V2>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx2).first
                Tuple5(p1, p2, RequestBody(it.second), p4, it.first)
            }
            isRequestBody<T4>() -> call.getFileDataRequest<V4>(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple5(p1, p2, p3, RequestBody(it.second), it.first)
            }
            else -> call.getFileRequest(false, extensions).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, p2, p3, p4, it)
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4 as T4, t.t5))
        if (removeFileAfterProcessing) t.t5.forEach { it.file.delete() }
    }
}
