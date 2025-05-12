package me.learning.api_schema.config

import me.learning.api_schema.common.extension.cleanRoute
import me.learning.api_schema.config.schema.InfoSchemaConfig
import me.learning.api_schema.config.schema.RouteSchemaConfig
import me.learning.api_schema.config.schema.ServerSchemaConfig

class ApiSchemaConfig internal constructor() {
    val defaultRoute = "/api/v1/schema"
    val defaultSwaggerPath = "/swagger"
    val defaultRedocPath = "/redoc"

    var enabled: Boolean = true
    var route: String = ""

    var info: InfoSchemaConfig = InfoSchemaConfig()
    var server: ServerSchemaConfig = ServerSchemaConfig()

    var swagger: RouteSchemaConfig = RouteSchemaConfig(defaultSwaggerPath)
    var redoc: RouteSchemaConfig = RouteSchemaConfig(defaultRedocPath)

    fun info(block: InfoSchemaConfig.() -> Unit) {
        info = InfoSchemaConfig().apply(block)
    }

    fun server(block: ServerSchemaConfig.() -> Unit) {
        server = ServerSchemaConfig().apply(block)
    }

    fun swagger(block: RouteSchemaConfig.() -> Unit) {
        swagger = RouteSchemaConfig(defaultSwaggerPath).apply(block)
    }

    fun redoc(block: RouteSchemaConfig.() -> Unit) {
        redoc = RouteSchemaConfig(defaultRedocPath).apply(block)
    }

    fun getRouteBuilder() = route.cleanRoute().takeIf { it.trim().isNotEmpty() } ?: defaultRoute
}
