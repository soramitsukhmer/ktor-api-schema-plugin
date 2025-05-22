package me.learning.api_schema.route.methods.extension

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.api.method.MethodBuilder

fun Route.GET(path: String = "", hasAuth: Boolean = false) : MethodBuilder {
    return MethodBuilder(this, MethodEnum.GET, path, hasAuth)
}

fun Route.POST(path: String = "", hasAuth: Boolean = false) : MethodBuilder {
    return MethodBuilder(this, MethodEnum.POST, path, hasAuth)
}

fun Route.PUT(path: String = "", hasAuth: Boolean = false) : MethodBuilder {
    return MethodBuilder(this, MethodEnum.PUT, path, hasAuth)
}
