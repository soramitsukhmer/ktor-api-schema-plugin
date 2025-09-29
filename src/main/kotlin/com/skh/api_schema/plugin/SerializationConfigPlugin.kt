package com.skh.api_schema.plugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import com.skh.api_schema.config.ApiSchemaProperties.property
import com.skh.api_schema.utils.JacksonObjMapper.javaTimeModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import java.text.SimpleDateFormat

fun Application.configureSerialization() {
    if (this.pluginOrNull(ContentNegotiation) == null) {

        log.info("Registered Jackson Format -- DateTime: ${property.datetimeFormat}")
        log.info("Registered Jackson Format -- Date: ${property.dateFormat}")
        log.info("Registered Jackson Format -- Time: ${property.timeFormat}")

        install(ContentNegotiation) {
            jackson {
                registerModule(javaTimeModule)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                configure(SerializationFeature.INDENT_OUTPUT, true)
                dateFormat = SimpleDateFormat(property.datetimeFormat)
            }
        }
    }
}
