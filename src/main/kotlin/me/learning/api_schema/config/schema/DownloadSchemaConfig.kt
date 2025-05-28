package me.learning.api_schema.config.schema

import me.learning.api_schema.extension.mergeRoute

class DownloadSchemaConfig internal constructor(
    var path: String = "",
    var enabled: Boolean = false,
    var hidden: Boolean = true,
    var filename: String = "",
    val defaultPath: String = "/download",
    val defaultFilename: String = "Ktor Openapi Schema.json"
) {

    fun getFilename(projectTitle: String? = null): String {
        return filename
            .ifEmpty {
                when (projectTitle.isNullOrEmpty().not()) {
                    true -> projectTitle
                    else -> defaultFilename
                }
            }.let { title ->
                when (title.endsWith(".json").not()) {
                    true -> title.plus(".json")
                    else -> title
                }
            }
    }

    fun getFullPath(baseUrl: String): String = path.mergeRoute(baseUrl, defaultPath)
}
