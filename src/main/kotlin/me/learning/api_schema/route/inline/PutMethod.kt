package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.handler.throwOnFileOrListTypeReqBody
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.getPathVariable
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.requestBody
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.handler.throwOnFileReqBody

/**
 * Defines a PUT route with optional authentication and request body handling.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the request body.
 * @param path The URL path for the PUT route. Default is an empty string.
 * @param block A lambda function that takes the authentication object of type [I]
 * and request body of type [J], and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T, reified I : Any, reified J : Any> Route.PUT(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, requestBody: J) -> T
): Route {
    MethodEnum.PUT.throwOnFileOrListTypeReqBody<J>(path)
    return this.put(path, schemaBuilder<T>(requestBody = J::class)) {
        val auth = call.auth<I>()
        val request = call.requestBody<J>()
        call.ok(call.request.block(auth, request))
    }
}


/**
 * Configures a HTTP PUT route that processes requests with authentication, a path variable, and a request body.
 * The route handler processes the incoming request and returns a response of type T.
 *
 * @param T The return type of the response body.
 * @param I The type of the authentication information.
 * @param J The type of the path variable.
 * @param K The type of the request body.
 * @param path The path pattern for the route. Defaults to an empty string.
 * @param block A suspendable lambda function that takes authentication information, the path variable,
 *              and the request body as inputs, and produces a response of type T.
 * @return The configured Route instance.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any> Route.PUT(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, requestBody: K) -> T
): Route {
    MethodEnum.PUT.throwOnFileOrListTypeReqBody<K>(path)
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.put(path, schemaBuilder<T>(pathVariable = pathVar, requestBody = K::class)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val request = call.requestBody<K>()
        call.ok(call.request.block(auth, valueJ, request))
    }
}


/**
 * Defines a PUT route with customizable path parameters, an authenticated user, and a request body.
 * This method allows for type-safe handling of authentication, path parameters, and request body parsing.
 *
 * @param T The type of the response body.
 * @param I The type of the authenticated user object.
 * @param J The type of the first path parameter.
 * @param K The type of the second path parameter.
 * @param L The type of the request body.
 * @param path The endpoint path for the route. Defaults to an empty string. The path can contain
 * placeholders for path variables in the format `{variable}`.
 * @param block A suspendable lambda function that defines the behavior of the route. It receives the
 * authenticated user ([I]), parsed values of the first and second path parameters ([J] and [K]),
 * the request body ([L]), and returns a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any, reified L : Any> Route.PUT(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, requestBody: L) -> T
): Route {
    MethodEnum.PUT.throwOnFileOrListTypeReqBody<L>(path)
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return this.put(path, schemaBuilder<T>(pathVariable = pathVar, requestBody = L::class)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val request = call.requestBody<L>()
        call.ok(call.request.block(auth, valueJ, valueK, request))
    }
}
