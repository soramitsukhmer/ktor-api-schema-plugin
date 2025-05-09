package me.learning.api_schema.route.methods

import me.learning.api_schema.common.Helper.asKType
import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.extension.auth
import me.learning.api_schema.common.extension.getPathVariable
import me.learning.api_schema.common.extension.ok
import me.learning.api_schema.common.extension.requestBody
import me.learning.api_schema.route.configBuilder

/**
 * Registers a POST route and handles a request with a given path and request body type.
 *
 * @param T The type of the response body that will be sent back to the client.
 * @param I The type of the request body expected in the incoming request.
 * @param route The current routing context where this handler should be registered.
 * @param path The path or endpoint that this POST handler will handle.
 * @param block A suspend function that processes the incoming request and generates a response.
 * @return A `Route` object representing the registered route.
 */
inline fun <reified T, reified I : Any> post(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(requestBody: I) -> T
): Route {
    return route.post(path, configBuilder<T>(requestBody = I::class.asKType())) {
        val requestBody = call.requestBody<I>()
        call.ok(call.request.block(requestBody))
    }
}


/**
 * Handles a POST request with specific authentication and request body processing logic.
 *
 * @param T The type of the response body to be returned.
 * @param I The type of the authentication information required for the request.
 * @param J The type of the request body for the POST endpoint.
 * @param route The routing context to which the POST route is added.
 * @param path The path for the POST endpoint (default is an empty string).
 * @param block A suspendable function that processes the authenticated request and request body
 *              and produces a response of type [T].
 * @return The route with the specified POST handler.
 */
inline fun <reified T, reified I : Any, reified J : Any> post(
    route: Route,
    path: String = "",
    crossinline block: suspend  RoutingRequest.(auth: I, requestBody: J) -> T
): Route {
    return route.post(path, configBuilder<T>(requestBody = I::class.asKType())) {
        val auth = call.auth<I>()
        val requestBody = call.requestBody<J>()
        call.ok(call.request.block(auth, requestBody))
    }
}


/**
 * Registers a POST route with the given path and allows handling requests with the provided handler block.
 *
 * @param T The type of the response.
 * @param I The type of the authentication object.
 * @param J The type of the path variable extracted from the given path.
 * @param K The type of the request body.
 * @param route The route to which the POST endpoint will be added.
 * @param path The path for the POST endpoint. Default is an empty string.
 * @param block The handler block that processes the request. It receives the authentication object, the path variable, and the request body as parameters.
 * @return The updated route with the registered POST endpoint.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any> post(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varI: J, requestBody: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return route.post(path, configBuilder<T>(variable = pathVar, requestBody = K::class.asKType())) {
        val auth = call.auth<I>()
        val valueI = call.getPathVariable<J>(pathVarJ)
        val requestBody = call.requestBody<K>()
        call.ok(call.request.block(auth, valueI, requestBody))
    }
}


/**
 * Creates a POST route and handles requests with specified path parameters, authentication, and a request body.
 *
 * @param T The type of the response body returned by the handler.
 * @param I The type of the authentication object.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param L The type of the request body.
 * @param route The parent route to which the POST route will be added.
 * @param path The endpoint path defining the route, potentially with path variables in `{}` (e.g., `/example/{id}`).
 * @param block The handler function for the POST request, which receives the authentication object, path variables,
 * and request body, and produces a response of type `T`.
 * @return The created POST route.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any, reified L : Any> post(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varI: J, varJ: K, requestBody: L) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return route.post(path, configBuilder<T>(variable = pathVar, requestBody = L::class.asKType())) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val requestBody = call.requestBody<L>()
        call.ok(call.request.block(auth, valueJ, valueK, requestBody))
    }
}
