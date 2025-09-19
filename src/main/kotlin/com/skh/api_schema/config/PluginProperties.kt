package com.skh.api_schema.config

import com.skh.api_schema.common.Constant.DATETIME_FORMAT
import com.skh.api_schema.common.Constant.DATE_FORMAT
import io.ktor.server.config.ApplicationConfig

object PluginProperties {
    private fun getApplicationConfig(): ApplicationConfig {
        val profile = System.getenv("KTOR_PROFILE")?.let { "-$it" } ?: ""
        return ApplicationConfig("application$profile.yaml")
    }

    private var config = getApplicationConfig()

    private inline fun <reified T> ApplicationConfig.getOptionalValue(key: String): T? {
        return try {
            val value = property(key).getString()
            when (T::class) {
                Int::class -> value.toInt()
                Long::class -> value.toLong()
                Float::class -> value.toFloat()
                Double::class -> value.toDouble()
                Boolean::class -> value.toBoolean()
                String::class -> value
                else -> null
            } as T?
        } catch (_: Exception) {
            null
        }
    }

    private fun <T> T?.elze(default: T): T {
        return this ?: default
    }

    data class Property(
        val enabled: Boolean = true,
        val baseUrls: List<String> = listOf(),
        val datetimeFormat: String = "",
        val dateFormat: String = "",
        val timeFormat: String = ""
    )

    val property = Property(
        enabled = config.getOptionalValue<Boolean>("api-shema.enabled").elze(true),
        baseUrls = config.getOptionalValue<String>("api-shema.base-urls")?.split(",")?.map { it.trim() }.elze(emptyList()),
        datetimeFormat = config.getOptionalValue<String>("api-shema.datetime-format").elze(DATETIME_FORMAT),
        dateFormat = config.getOptionalValue<String>("api-shema.date-format").elze(DATE_FORMAT),
        timeFormat = config.getOptionalValue<String>("api-shema.time-format").elze(DATE_FORMAT),
    )
}
