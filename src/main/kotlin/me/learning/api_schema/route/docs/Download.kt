package me.learning.api_schema.route.docs

import io.github.smiley4.ktoropenapi.OpenApiPlugin
import io.github.smiley4.ktoropenapi.config.OpenApiPluginConfig
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.download(projectFile: String?) {
    get({ hidden = true }) {
        val content = OpenApiPlugin.getOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID)
        val title = projectFile?.let { "$it openapi-schema" } ?: "openapi-schema"
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"$title.json\""
        )
        call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { content }
    }
}
