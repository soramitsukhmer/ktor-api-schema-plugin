package me.learning.api_schema.common

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
    fun isPathVariable() = this == PATH_VARIABLE
    fun isAuth() = this == AUTH
}

enum class RouteFormDataPropEnum {
    AUTH,
    FILE,
    FILES,
    TEXT;

    fun isText() = this == TEXT
}
