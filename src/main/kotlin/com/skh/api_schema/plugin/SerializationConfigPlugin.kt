package com.skh.api_schema.plugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.skh.api_schema.config.ApiSchemaProperties.property
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Application.configureSerialization() {
    if (this.pluginOrNull(ContentNegotiation) == null) {

        log.info("Registered Jackson Format -- DateTime: ${property.datetimeFormat}")
        log.info("Registered Jackson Format -- Date: ${property.dateFormat}")
        log.info("Registered Jackson Format -- Time: ${property.timeFormat}")

        val javaTimeModule = JavaTimeModule()
            .addDeserializer(
                LocalDateTime::class.java,
                LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(property.datetimeFormat))
            )
            .addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern(property.datetimeFormat))
            )
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ofPattern(property.dateFormat)))
            .addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ofPattern(property.dateFormat)))
            .addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeFormatter.ofPattern(property.timeFormat)))
            .addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeFormatter.ofPattern(property.timeFormat)))

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
