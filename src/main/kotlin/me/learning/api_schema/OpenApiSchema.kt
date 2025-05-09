package me.learning.api_schema

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import me.learning.api_schema.common.extension.mergeRoute
import me.learning.api_schema.config.OpenApiSchemaConfig

/**
 * A Ktor plugin for configuring and installing OpenAPI schema support into the application.
 *
 * This plugin integrates with the `OpenApi` plugin if it is not already installed in the application.
 * It allows for customization of the OpenAPI schema metadata through the `OpenApiSchemaConfig` configuration,
 * including settings such as API information, server details, and security configuration.
 *
 * The configuration supports specifying:
 * - General API information like title, version, description, and summary.
 * - Server details such as the URL and description.
 * - Security settings for the OpenAPI schema using bearer authentication with a JWT format.
 */

val OpenApiSchema = createApplicationPlugin("OpenApiSchema", ::OpenApiSchemaConfig) {
    if (pluginConfig.enable && (application.pluginOrNull(OpenApi) == null)) {
        application.install(OpenApi) {
            pluginConfig.info.let { config ->
                info {
                    config.title?.let { title = it }
                    config.version?.let { version = it }
                    config.description?.let { description = it }
                    config.summary?.let { summary = it }
                }
            }

            pluginConfig.server.let { config ->
                server {
                    config.url?.let { url = it }
                    config.description?.let { description = it }
                }
            }

            security {
                securityScheme(SECURITY_BEARER_SCHEMA_NAME) {
                    type = AuthType.HTTP
                    scheme = AuthScheme.BEARER
                    bearerFormat = "JWT"
                }
            }
        }

        val baseRoute = pluginConfig.getRouteBuilder()

        this.application.routing {
            route(baseRoute) {
                println(">>> expose endpoint json api schema: $baseRoute")
                openApi()
            }

            if (pluginConfig.swagger.enable) {
                val route = pluginConfig.swagger.path.mergeRoute(baseRoute, pluginConfig.defaultSwaggerPath)
                route(route) {
                    println(">>> expose endpoint swagger schema: $route")
                    swaggerUI(baseRoute)
                }
            }

            if (pluginConfig.redoc.enable) {
                val route = pluginConfig.redoc.path.mergeRoute(baseRoute, pluginConfig.defaultRoute)
                route(route) {
                    println(">>> expose endpoint redoc schema: $route")
                    redoc(baseRoute)
                }
            }
        }
    }
}
