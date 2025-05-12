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
import me.learning.api_schema.extension.mergeRoute
import me.learning.api_schema.config.ApiSchemaConfig
import me.learning.api_schema.plugin.configureSerialization
import me.learning.api_schema.plugin.exceptionConfigPlugin
import me.learning.api_schema.plugin.requestValidatorConfigPlugin

/**
 * A server-side application plugin named `ApiSchema`, used to enhance the application with API schema-related configuration and support.
 *
 * This plugin performs the following:
 * - Sets up request validation using a `RequestValidation` plugin if not already installed.
 * - Configures serialization with `ContentNegotiation` and utilizes a Jackson serializer.
 * - Configures global exception handling via status pages based on provided configuration.
 *
 * If enabled in the configuration (`ApiSchemaConfig.enabled`), and if the `OpenApi` plugin is not already installed,
 * it will install and configure `OpenApi` with the following set of options:
 *
 * - OpenAPI metadata such as title, version, description, and summary, as defined in `ApiSchemaConfig.info`.
 * - Server information like URL and description sourced from `ApiSchemaConfig.server`.
 * - Security scheme configuration for bearer tokens (JWT).
 *
 * Furthermore, the plugin sets up routing for exposing API schema routes as follows:
 * - A base route for the OpenAPI schema is derived from the configuration.
 * - If Swagger support is enabled (`ApiSchemaConfig.swagger.enabled`), a route for Swagger UI is configured.
 * - If ReDoc support is enabled (`ApiSchemaConfig.redoc.enabled`), a route for ReDoc UI is configured.
 *
 * The plugin's behavior can be customized via `ApiSchemaConfig`.
 */

val ApiSchema = createApplicationPlugin("ApiSchema", ::ApiSchemaConfig) {
    application.requestValidatorConfigPlugin()
    application.configureSerialization()
    application.exceptionConfigPlugin(pluginConfig.handler)

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
