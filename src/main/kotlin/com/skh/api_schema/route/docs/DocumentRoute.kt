package com.skh.api_schema.route.docs

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
import com.skh.api_schema.config.ApiSchemaProperties.property
import com.skh.api_schema.extension.cleanRoute
import com.skh.api_schema.extension.mergeRoute

fun Routing.documentRoute() {
    val baseRoute = "/".plus(property.path.cleanRoute())

    fun content(): String {
        val spec = OpenApiPlugin.getOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID)
        val content = spec.replace(Regex("\"(/\\w+)/\"\\s*:")) { matchResult ->
            val pathWithoutSlash = matchResult.groupValues[1]
            "\"$pathWithoutSlash\":"
        }

        return content
    }

    route(property.path) {
        // openApi()

        println(">>> expose endpoint json api schema: $baseRoute")
        route({ hidden = true }) {
            get { call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { content() } }
        }
    }

    if (property.download.enabled) {
        val route = baseRoute.mergeRoute(property.download.path)
        route(route) {
            println(">>> expose endpoint download json api schema: $route")
            get({ hidden = property.download.hidden }) {
                val filename = property.download.filename.ifEmpty { property.info.title }
                    .let { if (it.endsWith(".json").not()) it.plus(".json") else it }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"$filename\""
                )
                call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { content() }
            }
        }
    }

    if (property.swagger.enabled) {
        val route = baseRoute.mergeRoute(property.swagger.path)
        route(route) {
            println(">>> expose endpoint swagger schema: $route")
            swaggerUI(baseRoute)
        }
    }

    if (property.redoc.enabled) {
        val route = baseRoute.mergeRoute(property.swagger.path)
        route(route) {
            println(">>> expose endpoint redoc schema: $route")
            redoc(baseRoute)
        }
    }
}
