package me.learning.api_schema.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JacksonObjMapper {
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        dateFormat = StdDateFormat().withColonInTimeZone(true)
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