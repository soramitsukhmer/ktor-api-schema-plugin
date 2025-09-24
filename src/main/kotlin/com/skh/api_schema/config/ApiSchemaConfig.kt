package com.skh.api_schema.config

import com.skh.api_schema.config.ApiSchemaProperties.property
import com.skh.api_schema.config.exception.ExceptionConfig
import com.skh.api_schema.config.schema.DownloadSchemaConfig
import com.skh.api_schema.extension.cleanRoute
import com.skh.api_schema.config.schema.InfoSchemaConfig
import com.skh.api_schema.config.schema.RouteSchemaConfig
import com.skh.api_schema.config.schema.ServerSchemaConfig

class ApiSchemaConfig internal constructor() {
    val defaultRoute = "/schema"
    val defaultSwaggerPath = "/swagger"
    val defaultRedocPath = "/redoc"

    var enabled = property.enabled
    var route = ""

    var info = InfoSchemaConfig()
    val servers = mutableListOf<ServerSchemaConfig>()

    var download = DownloadSchemaConfig()
    var swagger = RouteSchemaConfig(defaultSwaggerPath)
    var redoc = RouteSchemaConfig(defaultRedocPath)

    var handler = ExceptionConfig()

    fun info(block: InfoSchemaConfig.() -> Unit) {
        info = InfoSchemaConfig().apply(block)
    }

    fun server(block: ServerSchemaConfig.() -> Unit) {
        servers.add(ServerSchemaConfig().apply(block))
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
