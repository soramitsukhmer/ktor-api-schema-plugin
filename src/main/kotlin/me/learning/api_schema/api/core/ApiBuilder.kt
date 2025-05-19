package me.learning.api_schema.api.core

import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.api.common.MethodEnum
import me.learning.api_schema.api.common.getApiSchemaBuilderProp
import me.learning.api_schema.api.dto.method.MethodBuilder
import me.learning.api_schema.api.dto.method.MethodBuilderT1
import me.learning.api_schema.api.dto.method.MethodBuilderT2
import me.learning.api_schema.api.dto.method.MethodBuilderT3
import me.learning.api_schema.api.dto.method.MethodBuilderT4
import me.learning.api_schema.api.dto.method.MethodBuilderT5
import me.learning.api_schema.api.dto.method.MethodBuilderT6
import me.learning.api_schema.api.dto.tuple.Tuple2
import me.learning.api_schema.api.dto.tuple.Tuple3
import me.learning.api_schema.api.dto.tuple.Tuple4
import me.learning.api_schema.api.dto.tuple.Tuple5
import me.learning.api_schema.api.dto.tuple.Tuple6
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop
import me.learning.api_schema.route.configBuilder

inline fun <reified T1 : Any> MethodBuilder.map(crossinline block: suspend RoutingRequest.() -> T1): Route {
    val (variable, requestBody) = path.getApiSchemaBuilderProp()
    val builder = configBuilder<T1>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) { call.ok(call.request.block()) }
        MethodEnum.POST -> route.post(path = path, builder) { call.ok(call.request.block()) }
        MethodEnum.PUT -> route.put(path = path, builder) { call.ok(call.request.block()) }
    }
}

inline fun <reified T1 : Any, reified T2> MethodBuilderT1<T1>.map(
    crossinline block: suspend RoutingRequest.(T1) -> T2
): Route {
    val pair = this.p1
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair))
    val builder = configBuilder<T2>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val prop = call.prop(pair, path).first
            call.ok(call.request.block(prop))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val prop = call.prop(pair, path).first
            call.ok(call.request.block(prop))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val prop = call.prop(pair, path).first
            call.ok(call.request.block(prop))
        }
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3> MethodBuilderT2<T1, T2>.map(
    crossinline block: suspend RoutingRequest.(tuple2: Tuple2<T1, T2>) -> T3
): Route {
    val pair1 = this.p1
    val pair2 = this.p2
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair1, pair2))
    val builder = configBuilder<T3>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val prop2 = call.prop(pair2, path, pvIndex1).first
            call.ok(call.request.block(Tuple2(prop1, prop2)))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val prop2 = call.prop(pair2, path, pvIndex1).first
            call.ok(call.request.block(Tuple2(prop1, prop2)))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val prop2 = call.prop(pair2, path, pvIndex1).first
            call.ok(call.request.block(Tuple2(prop1, prop2)))
        }
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4> MethodBuilderT3<T1, T2, T3>.map(
    crossinline block: suspend RoutingRequest.(tuple2: Tuple3<T1, T2, T3>) -> T4
): Route {
    val pair1 = this.p1
    val pair2 = this.p2
    val pair3 = this.p3
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair1, pair2, pair3))
    val builder = configBuilder<T4>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val prop3 = call.prop(pair3, path, pvIndex2).first
            call.ok(call.request.block(Tuple3(prop1, prop2, prop3)))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val prop3 = call.prop(pair3, path, pvIndex2).first
            call.ok(call.request.block(Tuple3(prop1, prop2, prop3)))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val prop3 = call.prop(pair3, path, pvIndex2).first
            call.ok(call.request.block(Tuple3(prop1, prop2, prop3)))
        }
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5> MethodBuilderT4<T1, T2, T3, T4>.map(
    crossinline block: suspend RoutingRequest.(tuple2: Tuple4<T1, T2, T3, T4>) -> T5
): Route {
    val pair1 = this.p1
    val pair2 = this.p2
    val pair3 = this.p3
    val pair4 = this.p4
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair1, pair2, pair3, pair4))
    val builder = configBuilder<T5>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val prop4 = call.prop(pair4, path, pvIndex3).first
            call.ok(call.request.block(Tuple4(prop1, prop2, prop3, prop4)))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val prop4 = call.prop(pair4, path, pvIndex3).first
            call.ok(call.request.block(Tuple4(prop1, prop2, prop3, prop4)))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val prop4 = call.prop(pair4, path, pvIndex3).first
            call.ok(call.request.block(Tuple4(prop1, prop2, prop3, prop4)))
        }
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6> MethodBuilderT5<T1, T2, T3, T4, T5>.map(
    crossinline block: suspend RoutingRequest.(tuple2: Tuple5<T1, T2, T3, T4, T5>) -> T6
): Route {
    val pair1 = this.p1
    val pair2 = this.p2
    val pair3 = this.p3
    val pair4 = this.p4
    val pair5 = this.p5
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair1, pair2, pair3, pair4, pair5))
    val builder = configBuilder<T6>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val prop5 = call.prop(pair5, path, pvIndex4).first
            call.ok(call.request.block(Tuple5(prop1, prop2, prop3, prop4, prop5)))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val prop5 = call.prop(pair5, path, pvIndex4).first
            call.ok(call.request.block(Tuple5(prop1, prop2, prop3, prop4, prop5)))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val prop5 = call.prop(pair5, path, pvIndex4).first
            call.ok(call.request.block(Tuple5(prop1, prop2, prop3, prop4, prop5)))
        }
    }
}

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7> MethodBuilderT6<T1, T2, T3, T4, T5, T6>.map(
    crossinline block: suspend RoutingRequest.(tuple2: Tuple6<T1, T2, T3, T4, T5, T6>) -> T7
): Route {
    val pair1 = this.p1
    val pair2 = this.p2
    val pair3 = this.p3
    val pair4 = this.p4
    val pair5 = this.p5
    val pair6 = this.p6
    val (variable, requestBody) = path.getApiSchemaBuilderProp(listOf(pair1, pair2, pair3, pair4, pair5, pair6))
    val builder = configBuilder<T1>(hasAuth = hasAuth, pathVariable = variable, requestBody = requestBody)

    return when (method) {
        MethodEnum.GET -> route.get(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val (prop5, pvIndex5) = call.prop(pair5, path, pvIndex4)
            val prop6 = call.prop(pair6, path, pvIndex5).first
            call.ok(call.request.block(Tuple6(prop1, prop2, prop3, prop4, prop5, prop6)))
        }

        MethodEnum.POST -> route.post(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val (prop5, pvIndex5) = call.prop(pair5, path, pvIndex4)
            val prop6 = call.prop(pair6, path, pvIndex5).first
            call.ok(call.request.block(Tuple6(prop1, prop2, prop3, prop4, prop5, prop6)))
        }

        MethodEnum.PUT -> route.put(path = path, builder) {
            val (prop1, pvIndex1) = call.prop(pair1, path)
            val (prop2, pvIndex2) = call.prop(pair2, path, pvIndex1)
            val (prop3, pvIndex3) = call.prop(pair3, path, pvIndex2)
            val (prop4, pvIndex4) = call.prop(pair4, path, pvIndex3)
            val (prop5, pvIndex5) = call.prop(pair5, path, pvIndex4)
            val prop6 = call.prop(pair6, path, pvIndex5).first
            call.ok(call.request.block(Tuple6(prop1, prop2, prop3, prop4, prop5, prop6)))
        }
    }
}
