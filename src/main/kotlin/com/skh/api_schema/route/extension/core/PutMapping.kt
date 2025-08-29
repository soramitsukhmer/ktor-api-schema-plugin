package com.skh.api_schema.route.extension.core

import io.github.smiley4.ktoropenapi.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import com.skh.api_schema.core.schemaBuilder
import com.skh.api_schema.dto.route.extension.put.PutDto
import com.skh.api_schema.dto.route.extension.put.PutDtoT1
import com.skh.api_schema.dto.route.extension.put.PutDtoT2
import com.skh.api_schema.dto.route.extension.put.PutDtoT3
import com.skh.api_schema.dto.route.extension.put.PutDtoT4
import com.skh.api_schema.dto.route.extension.tuple.Tuple2
import com.skh.api_schema.dto.route.extension.tuple.Tuple3
import com.skh.api_schema.dto.route.extension.tuple.Tuple4
import com.skh.api_schema.extension.cleanRoutePath
import com.skh.api_schema.extension.getApiSchemaBuilderProp
import com.skh.api_schema.extension.isRequestBody
import com.skh.api_schema.extension.ok
import com.skh.api_schema.extension.prop

inline fun <reified T> PutDto.map(crossinline block: suspend RoutingRequest.() -> T): Route {
    val builder = route.schemaBuilder<T, Unit>(hidden, responseWrapper = responseWrapper, accessRights = accessRights)
    return route.put(path.cleanRoutePath(), builder) {
        call.ok(call.request.block(), responseWrapper)
    }
}

inline fun <reified T, reified T1 : Any> PutDtoT1<T1>.map(crossinline block: suspend RoutingRequest.(v1: T1) -> T): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
    }

    return route.put(path.cleanRoutePath(), builder) {
        val v1 = call.prop(p1, path).first
        call.ok(call.request.block(v1), responseWrapper)
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any> PutDtoT2<T1, T2>.map(crossinline block: suspend RoutingRequest.(t2: Tuple2<T1, T2>) -> T): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p2.isRequestBody() -> route.schemaBuilder<T, T2>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
    }

    return route.put(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val v2 = call.prop(p2, path, idx1).first
        call.ok(call.request.block(Tuple2(v1, v2)), responseWrapper)
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any> PutDtoT3<T1, T2, T3>.map(
    crossinline block: suspend RoutingRequest.(t3: Tuple3<T1, T2, T3>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p2.isRequestBody() -> route.schemaBuilder<T, T2>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p3.isRequestBody() -> route.schemaBuilder<T, T3>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
    }

    return route.put(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val v3 = call.prop(p3, path, idx2).first
        call.ok(call.request.block(Tuple3(v1, v2, v3)), responseWrapper)
    }
}

inline fun <reified T, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> PutDtoT4<T1, T2, T3, T4>.map(
    crossinline block: suspend RoutingRequest.(t4: Tuple4<T1, T2, T3, T4>) -> T
): Route {
    val variable = path.getApiSchemaBuilderProp(listOf(p1)).first
    val builder = when (true) {
        p1.isRequestBody() -> route.schemaBuilder<T, T1>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p2.isRequestBody() -> route.schemaBuilder<T, T2>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p3.isRequestBody() -> route.schemaBuilder<T, T3>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        p4.isRequestBody() -> route.schemaBuilder<T, T4>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
        else -> route.schemaBuilder<T, Unit>(hidden, variable, responseWrapper = responseWrapper, accessRights = accessRights)
    }

    return route.put(path.cleanRoutePath(), builder) {
        val (v1, idx1) = call.prop(p1, path)
        val (v2, idx2) = call.prop(p2, path, idx1)
        val (v3, idx3) = call.prop(p3, path, idx2)
        val v4 = call.prop(p4, path, idx3).first
        call.ok(call.request.block(Tuple4(v1, v2, v3, v4)), responseWrapper)
    }
}
