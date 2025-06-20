package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.extension.ok
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.inline.RouteProp
import me.learning.api_schema.dto.route.inline.SchemaBuilderProp
import me.learning.api_schema.extension.cleanRoutePath
import me.learning.api_schema.extension.isRequestBody
import me.learning.api_schema.extension.prop


inline fun <reified T> Route.GET(
    path: String = "",
    crossinline block: suspend RoutingRequest.() -> T
): Route {
    return this.get(path.cleanRoutePath(), schemaBuilder<T, Unit>()) {
        call.ok(call.request.block())
    }
}


inline fun <reified T, reified V1 : Any, reified T1 : RouteProp<V1>> Route.GET(
    path: String = "",
    crossinline block: suspend RoutingRequest.(T1) -> T
): Route {
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, listOf(V1::class to T1::class))
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>()
        else -> schemaBuilder<T, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val p1 = call.prop<V1, T1>(path).first
        call.ok(call.request.block(p1))
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>
        > Route.GET(path: String = "", crossinline block: suspend RoutingRequest.(T1, T2) -> T
): Route {
    val collection = listOf(V1::class to T1::class, V2::class to T2::class)
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(prop.pathVariable)
        else -> schemaBuilder<T, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val p2 = call.prop<V2, T2>(path, idx).first
        call.ok(call.request.block(p1, p2))
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>
        > Route.GET(path: String = "", crossinline block: suspend RoutingRequest.(T1, T2, T3) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(prop.pathVariable)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(prop.pathVariable)
        else -> schemaBuilder<T, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val (p2, idx2) = call.prop<V2, T2>(path, idx)
        val p3 = call.prop<V3, T3>(path, idx2).first
        call.ok(call.request.block(p1, p2, p3))
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>
        > Route.GET(path: String = "", crossinline block: suspend RoutingRequest.(T1, T2, T3, T4) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
        V4::class to T4::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(prop.pathVariable)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(prop.pathVariable)
        isRequestBody<T4>() -> schemaBuilder<T, V4>(prop.pathVariable)
        else -> schemaBuilder<T, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val (p2, idx2) = call.prop<V2, T2>(path, idx)
        val (p3, idx3) = call.prop<V3, T3>(path, idx2)
        val p4 = call.prop<V4, T4>(path, idx3).first
        call.ok(call.request.block(p1, p2, p3, p4))
    }
}


inline fun <reified T,
        reified V1 : Any, reified T1 : RouteProp<V1>,
        reified V2 : Any, reified T2 : RouteProp<V2>,
        reified V3 : Any, reified T3 : RouteProp<V3>,
        reified V4 : Any, reified T4 : RouteProp<V4>,
        reified V5 : Any, reified T5 : RouteProp<V5>
        > Route.GET(path: String = "", crossinline block: suspend RoutingRequest.(T1, T2, T3, T4, T5) -> T
): Route {
    val collection = listOf(
        V1::class to T1::class,
        V2::class to T2::class,
        V3::class to T3::class,
        V4::class to T4::class,
        V5::class to T5::class
    )
    val prop = SchemaBuilderProp.getSchemaBuilderProp(MethodEnum.GET, path, collection)
    val builder = when (true) {
        isRequestBody<T1>() -> schemaBuilder<T, V1>(prop.pathVariable)
        isRequestBody<T2>() -> schemaBuilder<T, V2>(prop.pathVariable)
        isRequestBody<T3>() -> schemaBuilder<T, V3>(prop.pathVariable)
        isRequestBody<T4>() -> schemaBuilder<T, V4>(prop.pathVariable)
        isRequestBody<T5>() -> schemaBuilder<T, V5>(prop.pathVariable)
        else -> schemaBuilder<T, Unit>(prop.pathVariable)
    }

    return this.get(path.cleanRoutePath(), builder) {
        val (p1, idx) = call.prop<V1, T1>(path)
        val (p2, idx2) = call.prop<V2, T2>(path, idx)
        val (p3, idx3) = call.prop<V3, T3>(path, idx2)
        val (p4, idx4) = call.prop<V4, T4>(path, idx3)
        val p5 = call.prop<V5, T5>(path, idx4).first
        call.ok(call.request.block(p1, p2, p3, p4, p5))
    }
}
