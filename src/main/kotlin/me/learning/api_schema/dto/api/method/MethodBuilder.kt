package me.learning.api_schema.dto.api.method

import io.ktor.server.routing.Route
import me.learning.api_schema.common.MethodEnum

data class MethodBuilder(
    val route: Route,
    val method: MethodEnum,
    val path: String
)
