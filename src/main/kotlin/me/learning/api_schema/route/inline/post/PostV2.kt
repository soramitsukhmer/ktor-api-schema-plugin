package me.learning.api_schema.route.inline.post

import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.route.inline.RouteProp
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.prop

inline fun <reified T, reified D : Any, reified T1 : RouteProp<D>> Route.mPost(
    path: String = "",
    crossinline block: suspend RoutingRequest.(T1) -> T
): Route {
    return this.post(path, schemaBuilder<T>()) {
        val prop = call.prop<D, T1>(path).first
        call.ok(call.request.block(prop))
    }
}
