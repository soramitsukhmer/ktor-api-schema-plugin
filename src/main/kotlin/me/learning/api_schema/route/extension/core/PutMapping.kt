package me.learning.api_schema.route.extension.core

import io.github.smiley4.ktoropenapi.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.put.PutBuilder
import me.learning.api_schema.dto.route.put.PutBuilderT1
import me.learning.api_schema.dto.route.put.PutBuilderT2
import me.learning.api_schema.dto.route.put.PutBuilderT3
import me.learning.api_schema.dto.route.put.PutBuilderT4
import me.learning.api_schema.dto.route.tuple.Tuple2
import me.learning.api_schema.dto.route.tuple.Tuple3
import me.learning.api_schema.dto.route.tuple.Tuple4
import me.learning.api_schema.extension.getApiSchemaBuilderProp
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop

inline fun <reified T> PutBuilder.map(
    crossinline block: suspend RoutingRequest.() -> T
): Route {
    val builder = route.schemaBuilder<T>()
    return route.put(path, builder) {
        call.ok(call.request.block())
    }
}

inline fun <reified T, reified T1 : Any> PutBuilderT1<T1>.map(
    crossinline block: suspend RoutingRequest.(v1: T1) -> T
): Route {
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(p1))
    val builder = route.schemaBuilder<T>(pathVariable = variable, requestBody = requestBody)
    return route.put(path, builder) {
        val v1 = call.prop(p1, path).first
        call.ok(call.request.block(v1))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any> PutBuilderT2<T1, T2>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> T
): Route {
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(p1, p2))
    val builder = route.schemaBuilder<T>(pathVariable = variable, requestBody = requestBody)

    return route.put(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        call.ok(call.request.block(Tuple2(v1, v2)))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any> PutBuilderT3<T1, T2, T3>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple3<T1, T2, T3>) -> T
): Route {
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(p1, p2, p3))
    val builder = route.schemaBuilder<T>(pathVariable = variable, requestBody = requestBody)

    return route.put(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        call.ok(call.request.block(Tuple3(v1, v2, v3)))
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> PutBuilderT4<T1, T2, T3, T4>.map(
    crossinline block: suspend RoutingRequest.(t2: Tuple4<T1, T2, T3, T4>) -> T
): Route {
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(p1, p2, p3, p4))
    val builder = route.schemaBuilder<T>(pathVariable = variable, requestBody = requestBody)

    return route.put(path, builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val (v3, idx3) = call.prop(p3, path, idx2)
        val v4 = call.prop(p4, path, idx3).first
        call.ok(call.request.block(Tuple4(v1, v2, v3, v4)))
    }
}
