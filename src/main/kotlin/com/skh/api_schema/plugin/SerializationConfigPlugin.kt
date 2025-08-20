package com.skh.api_schema.plugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install

fun Application.configureSerialization() {
    if (this.pluginOrNull(ContentNegotiation) == null) {
        install(ContentNegotiation) {
            jackson {
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                configure(SerializationFeature.INDENT_OUTPUT, true)
                dateFormat = StdDateFormat().withColonInTimeZone(true)
            }
        }
    }
}
