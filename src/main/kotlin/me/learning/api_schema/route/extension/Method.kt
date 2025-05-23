package me.learning.api_schema.route.extension

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.api.method.MethodBuilder

fun Route.GET(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.GET, path)
}

fun Route.POST(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.POST, path)
}

fun Route.PUT(path: String = "") : MethodBuilder {
    return MethodBuilder(this, MethodEnum.PUT, path)
}
