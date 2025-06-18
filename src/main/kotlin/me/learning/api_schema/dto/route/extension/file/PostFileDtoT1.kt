package me.learning.api_schema.dto.route.extension.file

import io.ktor.server.routing.Route
import me.learning.api_schema.common.RouteFormDataPropEnum
import kotlin.reflect.KClass

data class PostFileDtoT1<T1 : Any>(
    val route: Route,
    val path: String,
    val extensions: List<String>,
    val removeFileAfterProcessing: Boolean = false,
    val p1: Pair<KClass<T1>, RouteFormDataPropEnum>
) {

    init {
        val hasFile = RouteFormDataPropEnum.FILE == p1.second
        val hasFiles = RouteFormDataPropEnum.FILES == p1.second
        if (!hasFiles && !hasFile) throw IllegalArgumentException("Unsupported property type not file")
    }

    fun <T : Any> addProp(pair: Pair<KClass<T>, RouteFormDataPropEnum>, extensions: List<String>): PostFileDtoT2<T1, T> {
        val ext = when (true) {
            this.extensions.isNotEmpty() -> this.extensions
            else -> extensions
        }

        return PostFileDtoT2(
            route,
            path,
            ext,
            removeFileAfterProcessing,
            p1,
            pair
        )
    }

    inline fun <reified T : Any> auth() = addProp(Pair(T::class, RouteFormDataPropEnum.AUTH), extensions)

    inline fun <reified T : Any> data() = addProp(Pair(T::class, RouteFormDataPropEnum.TEXT), extensions)
}
