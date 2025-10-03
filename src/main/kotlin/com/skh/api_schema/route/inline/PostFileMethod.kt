package com.skh.api_schema.route.inline

import com.skh.api_schema.common.Helper.clean
import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import com.skh.api_schema.common.MethodEnum
import com.skh.api_schema.config.ApiSchemaProperties.property
import com.skh.api_schema.extension.ok
import com.skh.api_schema.core.schemaBuilder
import com.skh.api_schema.dto.request.FileInfoReq
import com.skh.api_schema.dto.route.extension.tuple.Tuple3
import com.skh.api_schema.dto.route.extension.tuple.Tuple4
import com.skh.api_schema.dto.route.extension.tuple.Tuple5
import com.skh.api_schema.dto.route.inline.RouteProp
import com.skh.api_schema.dto.route.inline.SchemaBuilderProp
import com.skh.api_schema.dto.route.inline.impl.RequestBody
import com.skh.api_schema.extension.cleanRoute
import com.skh.api_schema.extension.getFileDataRequest
import com.skh.api_schema.extension.isRequestBody
import com.skh.api_schema.extension.prop

// ================================================================================================================ //
// ================================================================================================================ //
// =====================                                                              ============================= //
// =====================                    BLOCK POST WITH FILE                      ============================= //
// ===================== to check local storage: System.getProperty("java.io.tmpdir") ============================= //
// =====================                                                              ============================= //
// ================================================================================================================ //
// ================================================================================================================ //


inline fun <reified T> Route.postFile(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(file: FileInfoReq) -> T
): Route {
    val builder = schemaBuilder<T, Unit>(hidden, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)

    return this.post(path.cleanRoute(), builder) {
        val request = call.getFileDataRequest<Unit>(false, extensions, maxMB, 0).first.first()
        call.ok(call.request.block(request), responseWrapper)
        request.file.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T> Route.postFiles(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    maxItem: Int = 0,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(files: List<FileInfoReq>) -> T
): Route {
    val builder = schemaBuilder<T, Unit>(hidden, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)

    return this.post(path.cleanRoute(), builder) {
        val files = call.getFileDataRequest<Unit>(true, extensions, maxMB, maxItem).first
        call.ok(call.request.block(files), responseWrapper)
        files.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T, reified V1 : Any, reified T1 : RouteProp<V1>> Route.postFile(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, file: FileInfoReq) -> T
): Route {
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, listOf(V1::class to T1::class))
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val (file, p1) = when (isRequestBody<T1>()) {
            true -> call.getFileDataRequest<V1>(false, extensions, maxMB, 0).let { it.first.first() to RequestBody(it.second) }
            false -> call.getFileDataRequest<Unit>(false, extensions, maxMB, 0).first.first() to call.prop<V1, T1>(path).first
        }

        call.ok(call.request.block(p1 as T1, file), responseWrapper)
        file.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T, reified V1 : Any, reified T1 : RouteProp<V1>> Route.postFiles(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    maxItem: Int = 0,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, files: List<FileInfoReq>) -> T
): Route {
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, listOf(V1::class to T1::class))
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val (files, p1) = when (isRequestBody<T1>()) {
            true -> call.getFileDataRequest<V1>(true, extensions, maxMB, maxItem).let { it.first to RequestBody(it.second) }
            false -> call.getFileDataRequest<Unit>(true, extensions, maxMB, maxItem).first to call.prop<V1, T1>(path).first
        }

        call.ok(call.request.block(p1 as T1, files), responseWrapper)
        files.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        > Route.postFile(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, file: FileInfoReq) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions, maxMB, 0)
                .let { Tuple3(RequestBody(it.second), call.prop<V2, T2>(path).first, it.first.first()) }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions, maxMB, 0)
                .let { Tuple3(call.prop<V1, T1>(path).first, RequestBody(it.second), it.first.first()) }
            else -> Tuple3(call.prop<V1, T1>(path).first, call.prop<V2, T2>(path).first, call.getFileDataRequest<Unit>(false, extensions, maxMB, 0).first.first())
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3), responseWrapper)
        t.t3.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        > Route.postFiles(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    maxItem: Int = 0,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, files: List<FileInfoReq>) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(true, extensions, maxMB, maxItem)
                .let { Tuple3(RequestBody(it.second), call.prop<V2, T2>(path).first, it.first) }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(true, extensions, maxMB, maxItem)
                .let { Tuple3(call.prop<V1, T1>(path).first, RequestBody(it.second), it.first) }
            else -> Tuple3(call.prop<V1, T1>(path).first, call.prop<V2, T2>(path).first, call.getFileDataRequest<Unit>(true, extensions, maxMB, maxItem).first)
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3), responseWrapper)
        t.t3.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        > Route.postFile(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, file: FileInfoReq) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions, maxMB, 0).let {
                val (p2, idx) = call.prop<V2, T2>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(RequestBody(it.second), p2, p3, it.first.first())
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(p1, RequestBody(it.second), p3, it.first.first())
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V3>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p2 = call.prop<V2, T2>(path, idx).first
                Tuple4(p1, p2, RequestBody(it.second), it.first.first())
            }
            else -> call.getFileDataRequest<Unit>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple4(p1, p2, p3, it.first.first())
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4), responseWrapper)
        t.t4.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        > Route.postFiles(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    maxItem: Int = 0,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, file: List<FileInfoReq>) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(true, extensions, maxMB, maxItem).let {
                val (p2, idx) = call.prop<V2, T2>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(RequestBody(it.second), p2, p3, it.first)
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p3 = call.prop<V3, T3>(path, idx).first
                Tuple4(p1, RequestBody(it.second), p3, it.first)
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V3>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val p2 = call.prop<V2, T2>(path, idx).first
                Tuple4(p1, p2, RequestBody(it.second), it.first)
            }
            else -> call.getFileDataRequest<Unit>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple4(p1, p2, p3, it.first)
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4), responseWrapper)
        t.t4.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>,
        > Route.postFile(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, T4, file: FileInfoReq) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
        V4::class to T4::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        isRequestBody<T4>() -> schemaBuilder<T, V4>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = false, accessRights = accessRights)
    }

    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(false, extensions, maxMB, 0).let {
                val (p2, idx2) = call.prop<V2, T2>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(RequestBody(it.second), p2, p3, p4, it.first.first())
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, RequestBody(it.second), p3, p4, it.first.first())
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V3>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx2).first
                Tuple5(p1, p2, RequestBody(it.second), p4, it.first.first())
            }
            isRequestBody<T4>() -> call.getFileDataRequest<V4>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple5(p1, p2, p3, RequestBody(it.second), it.first.first())
            }
            else -> call.getFileDataRequest<Unit>(false, extensions, maxMB, 0).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, p2, p3, p4, it.first.first())
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4 as T4, t.t5), responseWrapper)
        t.t5.clean(removeFileAfterProcessing)
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>,
        > Route.postFiles(
    path: String = "",
    accessRights: List<String> = emptyList(),
    extensions: List<String> = emptyList(),
    maxMB: Long = property.maxFileSizeMB,
    maxItem: Int = 0,
    responseWrapper: Boolean = true,
    removeFileAfterProcessing: Boolean = false,
    hidden: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2, T3, T4, files: List<FileInfoReq>) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
        V4::class to T4::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.POST, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        isRequestBody<T4>() -> schemaBuilder<T, V4>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
        else -> schemaBuilder<T, Unit>(hidden, prop.pathVariable, responseWrapper = responseWrapper, bodyFileAsList = true, accessRights = accessRights)
    }


    return this.post(path.cleanRoute(), builder) {
        val t = when (true) {
            isRequestBody<T1>() -> call.getFileDataRequest<V1>(true, extensions, maxMB, maxItem).let {
                val (p2, idx2) = call.prop<V2, T2>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(RequestBody(it.second), p2, p3, p4, it.first)
            }
            isRequestBody<T2>() -> call.getFileDataRequest<V2>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p3, idx3) = call.prop<V3, T3>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, RequestBody(it.second), p3, p4, it.first)
            }
            isRequestBody<T3>() -> call.getFileDataRequest<V3>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p4 = call.prop<V4, T4>(path, idx2).first
                Tuple5(p1, p2, RequestBody(it.second), p4, it.first)
            }
            isRequestBody<T4>() -> call.getFileDataRequest<V4>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val p3 = call.prop<V3, T3>(path, idx2).first
                Tuple5(p1, p2, p3, RequestBody(it.second), it.first)
            }
            else -> call.getFileDataRequest<Unit>(true, extensions, maxMB, maxItem).let {
                val (p1, idx) = call.prop<V1, T1>(path)
                val (p2, idx2) = call.prop<V2, T2>(path, idx)
                val (p3, idx3) = call.prop<V3, T3>(path, idx2)
                val p4 = call.prop<V4, T4>(path, idx3).first
                Tuple5(p1, p2, p3, p4, it.first)
            }
        }

        call.ok(call.request.block(t.t1 as T1, t.t2 as T2, t.t3 as T3, t.t4 as T4, t.t5), responseWrapper)
        t.t5.clean(removeFileAfterProcessing)
    }
}
