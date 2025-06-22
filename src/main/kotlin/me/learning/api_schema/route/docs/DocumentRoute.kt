package me.learning.api_schema.route.docs

import io.github.smiley4.ktoropenapi.OpenApiPlugin
import io.github.smiley4.ktoropenapi.config.OpenApiPluginConfig
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import me.learning.api_schema.config.ApiSchemaConfig
import me.learning.api_schema.extension.mergeRoute

fun Routing.documentRoute(config: ApiSchemaConfig) {
    val baseRoute = config.getRouteBuilder()

    fun getContent(): String {
        val spec = OpenApiPlugin.getOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID)
        val content = spec.replace(Regex("\"(/\\w+)/\"\\s*:")) { matchResult ->
            val pathWithoutSlash = matchResult.groupValues[1]
            "\"$pathWithoutSlash\":"
        }

        return content
    }

    if (!config.enabled) return

    route(baseRoute) {
//        openApi()
        println(">>> expose endpoint json api schema: $baseRoute")
        route({ hidden = true }) {
            get { call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { getContent() } }
        }
    }

    if (config.download.enabled) {
        val route = config.download.getFullPath(baseRoute)
        route(route) {
            println(">>> expose endpoint download json api schema: $route")
            get({ hidden = config.download.hidden }) {
                val filename = config.download.getFilename(config.info.title)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"$filename\""
                )
                call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { getContent() }
            }
        }
    }

    if (config.swagger.enabled) {
        val route = config.swagger.path.mergeRoute(baseRoute, config.defaultSwaggerPath)
        route(route) {
            println(">>> expose endpoint swagger schema: $route")
            swaggerUI(baseRoute)
        }
    }

    if (config.redoc.enabled) {
        val route = config.redoc.path.mergeRoute(baseRoute, config.defaultRoute)
        route(route) {
            println(">>> expose endpoint redoc schema: $route")
            redoc(baseRoute)
        }
    }
}
