package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpHeaders
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.inline.RouteProp
import me.learning.api_schema.dto.route.inline.SchemaBuilderProp
import me.learning.api_schema.extension.cleanRoutePath
import me.learning.api_schema.extension.isRequestBody
import me.learning.api_schema.extension.prop
import java.io.File


inline fun Route.GETFILE(
    path: String = "",
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.() -> File
): Route {
    return this.get(path.cleanRoutePath(), schemaBuilder<Unit, Unit>()) {
        val file = call.request.block()
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}


inline fun <reified V : Any, reified T : RouteProp<V>> Route.GETFILE(
    path: String = "",
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T) -> File
): Route {
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, listOf(V::class to T::class))
    val builder = when (true) {
        isRequestBody<T>() -> schemaBuilder<Unit, V>()
        else -> schemaBuilder<Unit, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val p1 = call.prop<V, T>(path).first
        val file = call.request.block(p1)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}


inline fun <
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>
        > Route.GETFILE(
    path: String = "",
    removeFileAfterProcessing: Boolean = false,
    crossinline block: suspend RoutingRequest.(T1, T2) -> File
): Route {
    val collection = listOf(V1::class to T1::class, V2::class to T2::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<Unit, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<Unit, V2>(prop.pathVariable)
        else -> schemaBuilder<Unit, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val p2 = call.prop<V2, T2>(path, idx).first
        val file = call.request.block(p1, p2)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}


inline fun <
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>
        > Route.GETFILE(path: String = "", removeFileAfterProcessing: Boolean = false, crossinline block: suspend RoutingRequest.(T1, T2, T3) -> File
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<Unit, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<Unit, V2>(prop.pathVariable)
        isRequestBody<T3>() -> schemaBuilder<Unit, V3>(prop.pathVariable)
        else -> schemaBuilder<Unit, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val (p2, idx2) = call.prop<V2, T2>(path, idx)
        val p3 = call.prop<V3, T3>(path, idx2).first
        val file = call.request.block(p1, p2, p3)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}


inline fun <
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>
        > Route.GETFILE(path: String = "", removeFileAfterProcessing: Boolean = false, crossinline block: suspend RoutingRequest.(T1, T2, T3, T4) -> File
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
        V4::class to T4::class,
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<Unit, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<Unit, V2>(prop.pathVariable)
        isRequestBody<T3>() -> schemaBuilder<Unit, V3>(prop.pathVariable)
        isRequestBody<T4>() -> schemaBuilder<Unit, V4>(prop.pathVariable)
        else -> schemaBuilder<Unit, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val (p2, idx2) = call.prop<V2, T2>(path, idx)
        val (p3, idx3) = call.prop<V3, T3>(path, idx2)
        val p4 = call.prop<V4, T4>(path, idx3).first
        val file = call.request.block(p1, p2, p3, p4)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"${file.name}\""
        )
        call.respondFile(file)

        if (removeFileAfterProcessing) file.delete()
    }
}
