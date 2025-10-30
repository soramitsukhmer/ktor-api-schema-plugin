package com.skh.api_schema.common

enum class MethodEnum {
    GET,
    POST,
    PUT
}

enum class RoutePropEnum {
    AUTH,
    REQUEST_BODY,
    PATH_VARIABLE;

    fun isRequestBody() = this == REQUEST_BODY
}

enum class RouteFormDataPropEnum {
    AUTH,
    FILE,
    FILES,
    TEXT;

    fun isText() = this == TEXT
}
