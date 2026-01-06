package com.skh.api_schema.config

import com.skh.api_schema.common.Constant.DATETIME_FORMAT
import com.skh.api_schema.common.Constant.DATE_FORMAT
import com.skh.api_schema.common.Constant.DEFAULT_MAX_FILE_SIZE_100MB
import com.skh.api_schema.common.Constant.TIME_FORMAT
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.ktor.server.config.ApplicationConfig
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.lang.IllegalArgumentException

object ApiSchemaProperties {
    private fun getApplicationConfig(): ApplicationConfig {
        val profile = System.getenv("KTOR_PROFILE")?.let { "-$it" } ?: ""
        return ApplicationConfig("application$profile.yaml")
    }

    private var config = getApplicationConfig()

    private var format: OutputFormat = OutputFormat.JSON

    private inline fun <reified T> ApplicationConfig.getOptionalValue(root: String, key: String, elze: () -> T): T {
        return try {
            val path = root.trim().takeIf { it.isNotEmpty() }?.let { "$it.$key" } ?: key
            val value = property(path).getString()
            when (T::class) {
                Int::class -> value.toInt()
                Long::class -> value.toLong()
                Float::class -> value.toFloat()
                Double::class -> value.toDouble()
                Boolean::class -> value.toBoolean()
                String::class -> value
                else -> elze()
            } as T
        } catch (_: Exception) {
            elze()
        }
    }

    private inline fun <reified T> String.valueOf(root: String = "api-schema", noinline elze: (() -> T)? = null): T {
        val value = elze?.invoke() ?: when (T::class) {
            Int::class -> 0
            Long::class -> 0L
            Float::class -> 0f
            Double::class -> 0.0
            Boolean::class -> false
            String::class -> ""
            else -> throw IllegalArgumentException("Unsupported type ${T::class}")
        } as T

        return config.getOptionalValue<T>(root, this) { value }
    }

    init {
        val value = "format".valueOf { "json" }

        format = when (value) {
            "json" -> OutputFormat.JSON
            "yaml" -> OutputFormat.YAML
            else -> throw IllegalArgumentException("Unsupported api schema format: [$value]")
        }
    }

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    data class InfoProperty(
        val title: String,
        val version: String,
        val description: String?,
        val summary: String?
    )

    open class PathProperty(
        open val path: String,
        open val enabled: Boolean,
    )

    data class DownloadProperty(
        override val path: String,
        override val enabled: Boolean,
        val hidden: Boolean,
        val filename: String,
    ) : PathProperty(path, enabled)

    data class Property(
        val enabled: Boolean,
        val format: OutputFormat,
        val path: String,
        val baseUrls: List<String>,
        val maxFileSizeMB: Long,
        val datetimeFormat: String,
        val dateFormat: String,
        val timeFormat: String,
        val info: InfoProperty,
        val download: DownloadProperty,
        val redoc: PathProperty,
        val swagger: PathProperty
    )

    val property = Property(
        enabled = "enabled".valueOf { false },
        format = format,
        path = "path".valueOf { "schema" },
        baseUrls = "base-urls".valueOf<String>().split(",").filter { it.trim().isNotBlank() },
        maxFileSizeMB = "max-file-size-mb".valueOf { DEFAULT_MAX_FILE_SIZE_100MB },
        datetimeFormat = "datetime-format".valueOf { DATETIME_FORMAT },
        dateFormat = "date-format".valueOf { DATE_FORMAT },
        timeFormat = "time-format".valueOf { TIME_FORMAT },
        info = InfoProperty(
            title = "info.title".valueOf { "module".valueOf("application") { "Ktor - API Schema" } },
            version = "info.version".valueOf<String> { "latest" },
            description = "info.description".valueOf<String>().takeIf { it.isNotBlank() },
            summary = "info.summary".valueOf<String>().takeIf { it.isNotBlank() }
        ),
        download = DownloadProperty(
            path = "download.path".valueOf { "download" },
            enabled = "download.enabled".valueOf { false },
            hidden = "download.hidden-route".valueOf { true },
            filename = "download.filename".valueOf { "module".valueOf("application") { "ktor-openapi-schema" } },
        ),
        redoc = PathProperty(
            path = "redoc.path".valueOf { "redoc" },
            enabled = "redoc.enabled".valueOf { false },
        ),
        swagger = PathProperty(
            path = "swagger.path".valueOf { "swagger" },
            enabled = "swagger.enabled".valueOf { false },
        )
    )
}
