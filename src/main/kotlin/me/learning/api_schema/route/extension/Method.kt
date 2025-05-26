package me.learning.api_schema.route.extension

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum
import me.learning.api_schema.dto.api.method.MethodBuilder
import me.learning.api_schema.dto.api.method.MethodBuilderT1
import me.learning.api_schema.dto.handler.throwOnNotFileReqBody

fun Route.GET(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.GET, path)
}

fun Route.POST(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.POST, path)
}

fun Route.PUT(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.PUT, path)
}

//inline fun <reified T : Any> Route.GETFILE(path: String = "") : MethodBuilderT1<T> {
//    T::class.throwOnNotFileReqBody(MethodEnum.GET, path)
//    return MethodBuilderT1(this, MethodEnum.GET, path, T::class to RoutePropertyEnum.REQUEST_FILE)
//}

inline fun <reified T : Any> Route.POSTFILE(path: String = "") : MethodBuilderT1<T> {
    MethodEnum.POST.throwOnNotFileReqBody<T>(path)
    return MethodBuilderT1(this, MethodEnum.POST, path, T::class to RoutePropertyEnum.REQUEST_FILE)
}
