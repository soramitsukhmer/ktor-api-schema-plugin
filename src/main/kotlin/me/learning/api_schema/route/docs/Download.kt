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
import me.learning.api_schema.config.schema.DownloadSchemaConfig

fun Route.download(property: DownloadSchemaConfig, projectTitle: String? = null) {
    get({ hidden = property.hidden }) {
        val content = OpenApiPlugin.getOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID)
        val filename = property.getFilename(projectTitle)
        call.response.header(
            HttpHeaders.ContentDisposition,
            "attachment; filename=\"$filename\""
        )
        call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { content }
    }
}
