package me.learning.api_schema.dto.route.inline

import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.handler.throwOnFileReqBody
import me.learning.api_schema.dto.handler.throwOnMultipleDataRequestBody
import me.learning.api_schema.dto.route.inline.impl.PathVariable
import me.learning.api_schema.dto.route.inline.impl.RequestBody
import kotlin.reflect.KClass

data class SchemaBuilderProp(
    val pathVariable: Map<String, KClass<*>>?,
    val requestBody: KClass<*>?,
) {
    companion object {
        fun getSchemaBuilderProp(method: MethodEnum, path: String, collection: List<Pair<KClass<*>, KClass<*>>>): SchemaBuilderProp {
            method.throwOnMultipleDataRequestBody(path, collection.map { it.second })
            collection.forEach { pair -> method.throwOnFileReqBody(path, pair.first) }

            val paths = extractAllPathParameters(path)
            val p1 = mutableMapOf<String, KClass<*>>()
            var p2: KClass<*>? = null
            var idx = 0

            collection.forEach { pair ->
                when (pair.second) {
                    RequestBody::class -> p2 = pair.first
                    PathVariable::class -> {
                        val name = paths.getOrNull(idx) ?: ""
                        p1[name] = pair.first
                        idx++
                    }
                }
            }

            return SchemaBuilderProp(p1, p2)
        }
    }
}
