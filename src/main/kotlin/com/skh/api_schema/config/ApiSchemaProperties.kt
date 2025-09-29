package com.skh.api_schema.config

import com.skh.api_schema.common.Constant.DATETIME_FORMAT
import com.skh.api_schema.common.Constant.DATE_FORMAT
import com.skh.api_schema.common.Constant.DEFAULT_MAX_FILE_SIZE_100MB
import com.skh.api_schema.common.Constant.TIME_FORMAT
import io.ktor.server.config.ApplicationConfig
import jakarta.validation.Validation
import jakarta.validation.Validator

object ApiSchemaProperties {
    private fun getApplicationConfig(): ApplicationConfig {
        val profile = System.getenv("KTOR_PROFILE")?.let { "-$it" } ?: ""
        return ApplicationConfig("application$profile.yaml")
    }

    private var config = getApplicationConfig()

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    private inline fun <reified T> ApplicationConfig.getOptionalValue(key: String, elze: () -> T): T {
        return try {
            val value = property(key).getString()
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

    data class Property(
        val enabled: Boolean = true,
        val baseUrls: List<String> = listOf(),
        val maxFileSizeMB: Long = 0,
        val datetimeFormat: String = "",
        val dateFormat: String = "",
        val timeFormat: String = ""
    )

    val property = Property(
        enabled = config.getOptionalValue<Boolean>("api-schema.enabled") { true },
        baseUrls = config.getOptionalValue<String>("api-schema.base-urls") { "" }.split(",").map { it.trim() }.filter { it.isNotBlank() },
        maxFileSizeMB = config.getOptionalValue<Long>("api-schema.max-file-size-mb") { DEFAULT_MAX_FILE_SIZE_100MB },
        datetimeFormat = config.getOptionalValue<String>("api-schema.datetime-format") { DATETIME_FORMAT },
        dateFormat = config.getOptionalValue<String>("api-schema.date-format") { DATE_FORMAT },
        timeFormat = config.getOptionalValue<String>("api-schema.time-format") { TIME_FORMAT },
    )
}
