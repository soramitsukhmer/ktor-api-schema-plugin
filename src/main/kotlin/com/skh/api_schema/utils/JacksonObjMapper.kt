package com.skh.api_schema.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.skh.api_schema.config.ApiSchemaProperties.property
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object JacksonObjMapper {

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

    val objectMapper = jacksonObjectMapper().apply {
        registerModule(javaTimeModule)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(SerializationFeature.INDENT_OUTPUT, true)
        dateFormat = SimpleDateFormat(property.datetimeFormat)
    }
}

inline fun <reified T> ObjectMapper.fromMapToClass(map: Map<*, *>): T {
    val str = writeValueAsString(map)
    return readValue(str, T::class.java)
}

fun <T> ObjectMapper.fromMapToClass(clazz: Class<T>, map: Map<*, *>): T {
    val str = writeValueAsString(map)
    return readValue(str, clazz)
}

fun <T> ObjectMapper.fromMapToClass(clazz: Class<T>, value: Any): T {
    val str = when (value) {
        is String -> value
        else -> writeValueAsString(value)
    }
    return readValue(str, clazz)
}
