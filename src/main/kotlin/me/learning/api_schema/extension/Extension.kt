package me.learning.api_schema.extension

import me.learning.api_schema.utils.JacksonObjMapper.objectMapper
import me.learning.api_schema.utils.fromMapToClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

inline fun <reified T> Map<*, *>.ct(): T = objectMapper.fromMapToClass<T>(this)

fun <T> Map<*, *>.ct(clazz: Class<T>): T = objectMapper.fromMapToClass(clazz, this)

fun <T : Any> KClass<T>.asKType(): KType {
    return this.java.kotlin.createType()
}

fun KClass<*>.getQueryParamInfo(): List<Pair<String, KType>> {
    return this.memberProperties.map { prop ->
        prop.name to prop.returnType
    }
}