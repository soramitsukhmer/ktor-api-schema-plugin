package com.skh.api_schema

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.routing.routing
import com.skh.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import com.skh.api_schema.config.ApiSchemaConfig
import com.skh.api_schema.plugin.configureSerialization
import com.skh.api_schema.plugin.exceptionConfigPlugin
import com.skh.api_schema.plugin.requestValidatorConfigPlugin
import com.skh.api_schema.route.docs.documentRoute

/**
 * Configures an API schema plugin for a Ktor application. This plugin integrates a variety of features
 * including request validation, exception handling, content serialization, and OpenAPI documentation generation.
 *
 * When enabled, the plugin sets up APIs for serving OpenAPI specifications, Swagger UI, and ReDoc
 * documentation based on the configuration provided via `ApiSchemaConfig`.
 *
 * The plugin automatically:
 * - Validates incoming requests using a RequestValidation plugin.
 * - Configures JSON serialization with specific handling for Java and Kotlin data types.
 * - Sets up exception handling using the `StatusPages` plugin to manage custom or common HTTP status responses.
 * - Installs and configures the OpenAPI plugin if it is not already installed, allowing the application
 *   to expose API schema information in a standard OpenAPI format.
 * - Adds endpoints for serving OpenAPI schema (`json`), Swagger UI, and ReDoc documentation
 *   based on the specified or default routing paths.
 *
 * Security is configured using a bearer token scheme with JWT support. The OpenAPI schema generation is
 * enhanced with additional type overwrites for common Kotlin/Java types.
 *
 * This plugin is controlled by the `enabled` flag and will only take effect if explicitly enabled in
 * the `pluginConfig`. Additionally, users can configure route paths, information metadata, server details,
 * and exception handling strategies through the `ApiSchemaConfig`.
 */

val ApiSchema = createApplicationPlugin("ApiSchema", ::ApiSchemaConfig) {
    application.requestValidatorConfigPlugin()
    application.configureSerialization()
    application.exceptionConfigPlugin(pluginConfig.handler)

    if (pluginConfig.enabled && (application.pluginOrNull(OpenApi) == null)) {
        application.install(OpenApi) {
            pluginConfig.info.let { config ->
                info {
                    config.title.let { title = it }
                    config.version?.let { version = it }
                    config.description?.let { description = it }
                    config.summary?.let { summary = it }
                }
            }

            pluginConfig.servers.forEach { config ->
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

            schemas {
                generator = SchemaGenerator.reflection {
                    overwrite(SchemaGenerator.TypeOverwrites.LocalDateTime())
                    overwrite(SchemaGenerator.TypeOverwrites.LocalDate())
                    overwrite(SchemaGenerator.TypeOverwrites.JavaUuid())
                    overwrite(SchemaGenerator.TypeOverwrites.KotlinUuid())
                    overwrite(SchemaGenerator.TypeOverwrites.File())
                    overwrite(SchemaGenerator.TypeOverwrites.Instant())
                }
            }
        }

        application.routing { documentRoute(pluginConfig) }
    }
}
