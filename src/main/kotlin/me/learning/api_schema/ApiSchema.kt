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
import me.learning.api_schema.config.ApiSchemaConfig
import me.learning.api_schema.plugin.configureSerialization
import me.learning.api_schema.plugin.requestValidatorConfigPlugin

/**
 * Plugin responsible for setting up API schema generation and routing configuration.
 *
 * The `ApiSchema` plugin integrates features for generating and exposing
 * API schemas including OpenAPI, Swagger UI, and Redoc for documentation purposes.
 * It ensures the application is equipped with request validation and JSON serialization
 * plugins to handle incoming and outgoing data formats.
 *
 * Key features of the `ApiSchema` plugin:
 * - Configures request validation using `RequestValidation`.
 * - Sets up JSON serialization and deserialization using Jackson.
 * - Installs the OpenAPI plugin for schema generation with customizable
 *   metadata such as title, version, and description.
 * - Defines security schemas like bearer authentication for API documentation.
 * - Exposes API documentation through configurable routes for OpenAPI, Swagger, and Redoc.
 * - Utilizes configuration blocks through `ApiSchemaConfig` for fine-grained customization.
 *
 * This plugin dynamically installs specific endpoints to serve:
 * - The raw OpenAPI schema via a JSON endpoint.
 * - Swagger UI for interactive API documentation.
 * - Redoc for simplified API documentation visualization.
 */

val ApiSchema = createApplicationPlugin("ApiSchema", ::ApiSchemaConfig) {
    application.requestValidatorConfigPlugin()
    application.configureSerialization()

    if (pluginConfig.enabled && (application.pluginOrNull(OpenApi) == null)) {
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

            if (pluginConfig.swagger.enabled) {
                val route = pluginConfig.swagger.path.mergeRoute(baseRoute, pluginConfig.defaultSwaggerPath)
                route(route) {
                    println(">>> expose endpoint swagger schema: $route")
                    swaggerUI(baseRoute)
                }
            }

            if (pluginConfig.redoc.enabled) {
                val route = pluginConfig.redoc.path.mergeRoute(baseRoute, pluginConfig.defaultRoute)
                route(route) {
                    println(">>> expose endpoint redoc schema: $route")
                    redoc(baseRoute)
                }
            }
        }
    }
}
