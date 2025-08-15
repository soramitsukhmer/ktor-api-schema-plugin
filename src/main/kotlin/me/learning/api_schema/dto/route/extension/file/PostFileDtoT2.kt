package me.learning.api_schema.dto.route.extension.file

import io.ktor.server.routing.Route
import me.learning.api_schema.common.RouteFormDataPropEnum
import kotlin.reflect.KClass

data class PostFileDtoT2<T1 : Any, T2 : Any>(
    val route: Route,
    val path: String,
    val extensions: List<String>,
    val removeFileAfterProcessing: Boolean,
    val responseWrapper: Boolean,
    val hidden: Boolean,
    val p1: Pair<KClass<T1>, RouteFormDataPropEnum>,
    val p2: Pair<KClass<T2>, RouteFormDataPropEnum>
) {

    init {
        val properties = listOf(p1.second, p2.second)
        properties.groupBy { it }.filter { it.value.size > 1 }
            .takeIf { it.size > 1 }
            ?.let { throw IllegalArgumentException("Unsupported multiple properties found: ${it.keys}") }
        val hasFile = RouteFormDataPropEnum.FILE in properties
        val hasFiles = RouteFormDataPropEnum.FILES in properties
        if (hasFiles && hasFile) throw IllegalArgumentException("Unsupported multiple properties found: FILE")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RouteFormDataPropEnum>, extensions: List<String>): PostFileDtoT3<T1, T2, T> {
        val ext = when (true) {
            this.extensions.isNotEmpty() -> this.extensions
            else -> extensions
        }

        return PostFileDtoT3(
            route,
            path,
            ext,
            removeFileAfterProcessing,
            responseWrapper,
            hidden,
            p1,
            p2,
            pair
        )
    }

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RouteFormDataPropEnum.AUTH), extensions)

    inline fun <reified T : Any> data() = addProp(Pair(T::class, RouteFormDataPropEnum.TEXT), extensions)
}
