package me.learning.api_schema.common

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

object Helper {

    fun extractAllPathParameters(path: String): List<String> {
        return Regex("""\{([^}]+)}""")
            .findAll(path)
            .map { it.groupValues[1] }
            .toList()
    }

    fun <T : Any> KClass<T>.asKType(): KType {
        return this.java.kotlin.createType()
    }

    fun KClass<*>.getQueryParamInfo(): List<Pair<String, KType>> {
        return this.memberProperties.map { prop ->
            prop.name to prop.returnType
        }
    }
}
