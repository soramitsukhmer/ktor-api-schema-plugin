package me.learning.api_schema.dto.route.extension.file

import io.ktor.server.routing.Route
import me.learning.api_schema.common.RouteFormDataPropEnum
import kotlin.reflect.KClass

data class PostFileDtoT3<T1 : Any, T2 : Any, T3 : Any>(
    val route: Route,
    val path: String,
    val extensions: List<String>,
    val removeFileAfterProcessing: Boolean,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val p1: Pair<KClass<T1>, RouteFormDataPropEnum>,
    val p2: Pair<KClass<T2>, RouteFormDataPropEnum>,
    val p3: Pair<KClass<T3>, RouteFormDataPropEnum>
) {
    init {
        val properties = listOf(p1.second, p2.second, p3.second)
        properties.groupBy { it }.filter { it.value.size > 1 }
            .takeIf { it.size > 1 }
            ?.let { throw IllegalArgumentException("Unsupported multiple properties found: ${it.keys}") }
        val hasFile = RouteFormDataPropEnum.FILE in properties
        val hasFiles = RouteFormDataPropEnum.FILES in properties
        if (hasFiles && hasFile) throw IllegalArgumentException("Unsupported multiple properties found: FILE")
    }
}
