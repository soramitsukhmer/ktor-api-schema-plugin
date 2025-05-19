package me.learning.api_schema.api.dto.method

import io.ktor.server.routing.Route
import me.learning.api_schema.api.common.MethodEnum

data class MethodBuilder(
    val route: Route,
    val method: MethodEnum,
    val path: String,
    val hasAuth: Boolean = false,
)
