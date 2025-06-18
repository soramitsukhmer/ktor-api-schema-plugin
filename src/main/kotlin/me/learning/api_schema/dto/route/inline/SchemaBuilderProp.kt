package me.learning.api_schema.dto.route.inline

import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.dto.route.inline.impl.PathVariable
import me.learning.api_schema.dto.route.inline.impl.RequestBody
import kotlin.reflect.KClass

data class SchemaBuilderProp(
    val pathVariable: Map<String, KClass<*>>?,
    val requestBody: KClass<*>?,
    val bodyAsFormData: Boolean = false,
    val bodyFileAsList: Boolean = false,
) {
    companion object {
        fun getSchemaBuilderProp(path: String, collection: List<Pair<KClass<*>, KClass<*>>>): SchemaBuilderProp {
            val allPathVar = extractAllPathParameters(path)
            val p1 = mutableMapOf<String, KClass<*>>()
            var p2: KClass<*>? = null
            var idx = 0

            collection.forEach { pair ->
                when (pair.second) {
                    RequestBody::class -> p2 = pair.first
                    PathVariable::class -> {
                        val name = allPathVar.getOrNull(idx) ?: ""
                        p1[name] = pair.first
                        idx++
                    }
                }
            }

            return SchemaBuilderProp(p1, p2)
        }
    }
}
