package me.learning.api_schema.extension

import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.api.method.MethodBuilder
import me.learning.api_schema.dto.api.method.MethodBuilderT1
import me.learning.api_schema.dto.api.method.MethodBuilderT2
import me.learning.api_schema.dto.api.method.MethodBuilderT3
import me.learning.api_schema.dto.api.method.MethodBuilderT4
import me.learning.api_schema.dto.api.method.MethodBuilderT5
import me.learning.api_schema.dto.api.method.MethodBuilderT6
import kotlin.reflect.KClass


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
