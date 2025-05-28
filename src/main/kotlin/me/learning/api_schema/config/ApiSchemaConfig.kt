package me.learning.api_schema.config

import me.learning.api_schema.config.exception.ExceptionConfig
import me.learning.api_schema.config.schema.DownloadSchemaConfig
import me.learning.api_schema.extension.cleanRoute
import me.learning.api_schema.config.schema.InfoSchemaConfig
import me.learning.api_schema.config.schema.RouteSchemaConfig
import me.learning.api_schema.config.schema.ServerSchemaConfig

class ApiSchemaConfig internal constructor() {
    val defaultRoute = "/api/v1/schema"
    val defaultSwaggerPath = "/swagger"
    val defaultRedocPath = "/redoc"

    var enabled = true
    var route = ""

    var info = InfoSchemaConfig()
    var server = ServerSchemaConfig()

    var download = DownloadSchemaConfig()
    var swagger = RouteSchemaConfig(defaultSwaggerPath)
    var redoc = RouteSchemaConfig(defaultRedocPath)

    var handler = ExceptionConfig()

    fun info(block: InfoSchemaConfig.() -> Unit) {
        info = InfoSchemaConfig().apply(block)
    }

    fun server(block: ServerSchemaConfig.() -> Unit) {
        server = ServerSchemaConfig().apply(block)
    }

    fun download(block: DownloadSchemaConfig.() -> Unit) {
        download = DownloadSchemaConfig().apply(block)
    }

    fun swagger(block: RouteSchemaConfig.() -> Unit) {
        swagger = RouteSchemaConfig(defaultSwaggerPath).apply(block)
    }

    fun redoc(block: RouteSchemaConfig.() -> Unit) {
        redoc = RouteSchemaConfig(defaultRedocPath).apply(block)
    }

    fun getRouteBuilder() = route.cleanRoute().takeIf { it.trim().isNotEmpty() } ?: defaultRoute

    fun handler(block: ExceptionConfig.() -> Unit) {
        handler = ExceptionConfig().apply(block)
    }
}
