package me.learning.api_schema.api.common

import io.ktor.server.routing.RoutingCall
import me.learning.api_schema.api.dto.method.MethodBuilder
import me.learning.api_schema.api.dto.method.MethodBuilderT1
import me.learning.api_schema.api.dto.method.MethodBuilderT2
import me.learning.api_schema.api.dto.method.MethodBuilderT3
import me.learning.api_schema.api.dto.method.MethodBuilderT4
import me.learning.api_schema.api.dto.method.MethodBuilderT5
import me.learning.api_schema.api.dto.method.MethodBuilderT6
import me.learning.api_schema.common.Helper.extractAllPathParameters
import kotlin.reflect.KClass

fun String.getApiSchemaBuilderProp(
    pairs: List<Pair<KClass<*>, RoutePropertyEnum>> = emptyList()
): Pair<Map<String, KClass<*>>, KClass<*>?> {
    val allPathVar = extractAllPathParameters(this)
    val variable = pairs.filter { it.second == RoutePropertyEnum.PATH_VARIABLE }
        .mapIndexed { index, pair -> (allPathVar.getOrNull(index) ?: "") to pair.first }
        .toMap()
    val requestBody = pairs.find { it.second == RoutePropertyEnum.REQUEST_BODY }?.first
    return Pair(variable, requestBody)
}

fun MethodEnum.throwOnMultipleProp(path: String, properties: List<RoutePropertyEnum>, onProp: RoutePropertyEnum, tag: String) {
    properties
        .filter { it == onProp }
        .takeIf { it.size > 1 }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported multiple $tag") }
}

fun MethodEnum.throwOnMethodGetRequestBody(path: String, properties: List<RoutePropertyEnum>) {
    if (this != MethodEnum.GET) return
    properties
        .find { it == RoutePropertyEnum.REQUEST_BODY }
        ?.let { throw IllegalArgumentException("Route path[$path], method[$this]: Unsupported request body") }
}


fun <T1 : Any> MethodBuilder.auth(kClass: KClass<T1>): MethodBuilderT1<T1> {
    return MethodBuilderT1(this.route, this.method, this.path, true, kClass to RoutePropertyEnum.AUTH)
}
fun <T1 : Any> MethodBuilder.requestBody(kClass: KClass<T1>): MethodBuilderT1<T1> {
    return MethodBuilderT1(this.route, this.method, this.path, this.hasAuth, kClass to RoutePropertyEnum.REQUEST_BODY)
}
fun <T1 : Any> MethodBuilder.pathVariable(kClass: KClass<T1>): MethodBuilderT1<T1> {
    return MethodBuilderT1(this.route, this.method, this.path, this.hasAuth, kClass to RoutePropertyEnum.PATH_VARIABLE)
}


fun <T1 : Any, T2 : Any> MethodBuilderT1<T1>.auth(kClass: KClass<T2>) = MethodBuilderT2(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    kClass to RoutePropertyEnum.AUTH
)
fun <T1 : Any, T2 : Any> MethodBuilderT1<T1>.requestBody(kClass: KClass<T2>) = MethodBuilderT2(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    kClass to RoutePropertyEnum.REQUEST_BODY
)
fun <T1 : Any, T2 : Any> MethodBuilderT1<T1>.pathVariable(kClass: KClass<T2>) = MethodBuilderT2(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    kClass to RoutePropertyEnum.PATH_VARIABLE
)


fun <T1 : Any, T2 : Any, T3: Any> MethodBuilderT2<T1, T2>.auth(kClass: KClass<T3>) = MethodBuilderT3(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    kClass to RoutePropertyEnum.AUTH
)
fun <T1 : Any, T2 : Any, T3: Any> MethodBuilderT2<T1, T2>.requestBody(kClass: KClass<T3>) = MethodBuilderT3(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    kClass to RoutePropertyEnum.REQUEST_BODY
)
fun <T1 : Any, T2 : Any, T3: Any> MethodBuilderT2<T1, T2>.pathVariable(kClass: KClass<T3>) = MethodBuilderT3(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    kClass to RoutePropertyEnum.PATH_VARIABLE
)


fun <T1 : Any, T2 : Any, T3: Any, T4: Any> MethodBuilderT3<T1, T2, T3>.auth(kClass: KClass<T4>) = MethodBuilderT4(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    this.p3,
    kClass to RoutePropertyEnum.AUTH
)
fun <T1 : Any, T2 : Any, T3: Any, T4: Any> MethodBuilderT3<T1, T2, T3>.requestBody(kClass: KClass<T4>) = MethodBuilderT4(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    this.p3,
    kClass to RoutePropertyEnum.REQUEST_BODY
)
fun <T1 : Any, T2 : Any, T3: Any, T4: Any> MethodBuilderT3<T1, T2, T3>.pathVariable(kClass: KClass<T4>) = MethodBuilderT4(
    this.route,
    this.method,
    this.path,
    this.hasAuth,
    this.p1,
    this.p2,
    this.p3,
    kClass to RoutePropertyEnum.PATH_VARIABLE
)


fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any> MethodBuilderT4<T1, T2, T3, T4>.auth(kClass: KClass<T5>) =
    MethodBuilderT5(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        kClass to RoutePropertyEnum.AUTH
    )
fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any> MethodBuilderT4<T1, T2, T3, T4>.requestBody(kClass: KClass<T5>) =
    MethodBuilderT5(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        kClass to RoutePropertyEnum.REQUEST_BODY
    )
fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any> MethodBuilderT4<T1, T2, T3, T4>.pathVariable(kClass: KClass<T5>) =
    MethodBuilderT5(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        kClass to RoutePropertyEnum.PATH_VARIABLE
    )


fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any, T6: Any> MethodBuilderT5<T1, T2, T3, T4, T5>.auth(kClass: KClass<T6>) =
    MethodBuilderT6(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        this.p5,
        kClass to RoutePropertyEnum.AUTH
    )
fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any, T6: Any> MethodBuilderT5<T1, T2, T3, T4, T5>.requestBody(kClass: KClass<T6>) =
    MethodBuilderT6(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        this.p5,
        kClass to RoutePropertyEnum.REQUEST_BODY
    )
fun <T1 : Any, T2 : Any, T3: Any, T4: Any, T5: Any, T6: Any> MethodBuilderT5<T1, T2, T3, T4, T5>.pathVariable(kClass: KClass<T6>) =
    MethodBuilderT6(
        this.route,
        this.method,
        this.path,
        this.hasAuth,
        this.p1,
        this.p2,
        this.p3,
        this.p4,
        this.p5,
        kClass to RoutePropertyEnum.PATH_VARIABLE
    )
