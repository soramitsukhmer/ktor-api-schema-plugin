package me.learning.api_schema.extension

import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RoutePropertyEnum


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
