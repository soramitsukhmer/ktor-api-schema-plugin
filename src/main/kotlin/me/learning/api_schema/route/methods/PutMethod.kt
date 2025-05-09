package me.learning.api_schema.route.methods

import io.github.smiley4.ktoropenapi.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.asKType
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.extension.auth
import me.learning.api_schema.common.extension.getPathVariable
import me.learning.api_schema.common.extension.ok
import me.learning.api_schema.common.extension.requestBody
import me.learning.api_schema.route.configBuilder

/**
 * Configures a route to handle HTTP `PUT` requests with authentication and request body parsing.
 *
 * @param T The type of the response returned by the handler function.
 * @param I The type representing authentication information.
 * @param J The type representing the request body.
 * @param route The parent route to which this `PUT` request should be added.
 * @param path An optional subpath for the `PUT` route. Defaults to an empty string.
 * @param block A suspending lambda that handles the request. It takes two parameters:
 * - `auth`: The authentication information of type `I`.
 * - `requestBody`: The parsed request body of type `J`.
 * This lambda returns a response of type `T`.
 * @return The configured `Route` object representing this `PUT` route.
 */
inline fun <reified T, reified I : Any, reified J : Any> put(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, requestBody: J) -> T
): Route {
    return route.put(path, configBuilder<T>(requestBody = J::class.asKType())) {
        val auth = call.auth<I>()
        val request = call.requestBody<J>()
        call.ok(call.request.block(auth, request))
    }
}


/**
 * Configures an HTTP PUT route with the specified path and request processing block.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object, expected to be obtained from the request.
 * @param J The type of the path variable, extracted according to the path format.
 * @param K The type of the request body, parsed from the incoming request.
 * @param route The starting route definition to which this PUT handler will be added.
 * @param path The relative path for the PUT route. Can include path variables in the form `{variable}`. Defaults to an empty string.
 * @param block A suspending lambda function that receives the authentication object of type `I`,
 *              the path variable of type `J`, and the request body of type `K`. Returns a response of type `T`.
 * @return The configured `Route` object with the added PUT handler.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any> put(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, requestBody: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return route.put(path, configBuilder<T>(variable = pathVar, requestBody = K::class.asKType())) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val request = call.requestBody<K>()
        call.ok(call.request.block(auth, valueJ, request))
    }
}


/**
 * Registers a PUT HTTP method handler on the provided route. The handler includes support
 * for authentication, path variables, and processing the request body.
 *
 * @param T The type of the response body returned by the handler.
 * @param I The type of the authentication object required for this route.
 * @param J The type for the first path variable, if provided.
 * @param K The type for the second path variable, if provided.
 * @param L The type of the request body that the handler expects.
 * @param route The route object to register the PUT handler on.
 * @param path The relative path under the route for this handler. Defaults to an empty string.
 * @param block A suspending lambda that processes requests. It provides access to authentication,
 *              path variables, and the request body, and produces a response.
 * @return The updated route with the registered PUT handler.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any, reified L : Any> put(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, requestBody: L) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return route.put(path, configBuilder<T>(variable = pathVar, requestBody = L::class.asKType())) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val request = call.requestBody<L>()
        call.ok(call.request.block(auth, valueJ, valueK, request))
    }
}
