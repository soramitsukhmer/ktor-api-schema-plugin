package me.learning.api_schema.config

import me.learning.api_schema.config.exception.ExceptionConfig
import me.learning.api_schema.extension.cleanRoute
import me.learning.api_schema.config.schema.InfoSchemaConfig
import me.learning.api_schema.config.schema.RouteSchemaConfig
import me.learning.api_schema.config.schema.ServerSchemaConfig

class ApiSchemaConfig internal constructor() {
    val defaultRoute = "/api/v1/schema"
    val defaultDownloadPath = "/download"
    val defaultSwaggerPath = "/swagger"
    val defaultRedocPath = "/redoc"

    var enabled: Boolean = true
    var route: String = ""

    var info: InfoSchemaConfig = InfoSchemaConfig()
    var server: ServerSchemaConfig = ServerSchemaConfig()

    var download: RouteSchemaConfig = RouteSchemaConfig(defaultDownloadPath)
    var swagger: RouteSchemaConfig = RouteSchemaConfig(defaultSwaggerPath)
    var redoc: RouteSchemaConfig = RouteSchemaConfig(defaultRedocPath)

    var handler: ExceptionConfig = ExceptionConfig()

    fun info(block: InfoSchemaConfig.() -> Unit) {
        info = InfoSchemaConfig().apply(block)
    }

    fun server(block: ServerSchemaConfig.() -> Unit) {
        server = ServerSchemaConfig().apply(block)
    }

    fun download(block: RouteSchemaConfig.() -> Unit) {
        download = RouteSchemaConfig(defaultDownloadPath).apply(block)
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
