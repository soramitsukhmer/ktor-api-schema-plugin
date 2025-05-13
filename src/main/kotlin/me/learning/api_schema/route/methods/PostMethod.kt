package me.learning.api_schema.route.methods

import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.extension.asKType
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.getPathVariable
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.requestBody
import me.learning.api_schema.route.configBuilder

/**
 * Defines a POST route with a specified path and a request body handling block.
 * This method configures the route with a request body of type `I` and handles the response of type `T`.
 *
 * @param T The type of the response body.
 * @param I The type of the request body.
 * @param path The endpoint path for the POST route. Defaults to an empty string.
 * @param block A suspend lambda that processes the request body of type `I`
 * and returns a response of type `T`.
 * @return The configured `Route` instance.
 */
inline fun <reified T, reified I : Any> Route.post(
    path: String = "",
    crossinline block: suspend RoutingRequest.(requestBody: I) -> T
): Route {
    return this.post(path, configBuilder<T>(hasAuth = false, requestBody = I::class.asKType())) {
        val requestBody = call.requestBody<I>()
        call.ok(call.request.block(requestBody))
    }
}


/**
 * Defines a POST route that processes a request with authentication and a request body,
 * and responds with a specified type.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the request body.
 * @param path The path of the route. Optional, defaults to an empty string.
 * @param block A suspend function that handles the request. It receives the authentication
 * object of type [I] and the parsed request body of type [J], and returns a response of type [T].
 * @return An instance of [Route] configured with the POST route.
 */
inline fun <reified T, reified I : Any, reified J : Any> Route.post(
    path: String = "",
    crossinline block: suspend  RoutingRequest.(auth: I, requestBody: J) -> T
): Route {
    return this.post(path, configBuilder<T>(requestBody = I::class.asKType())) {
        val auth = call.auth<I>()
        val requestBody = call.requestBody<J>()
        call.ok(call.request.block(auth, requestBody))
    }
}


/**
 * Registers a POST route with the specified endpoint, handling authentication,
 * path variables, and a request body. The response is processed via the provided
 * block, which gets invoked for handling the request.
 *
 * @param T The type of the response body returned by the route.
 * @param I The type of the authentication object used by the route.
 * @param J The type of the path variable extracted from the route.
 * @param K The type of the request body parsed by the route.
 * @param path The endpoint of the route. It can include path variables in the format `{variableName}`.
 * @param block A suspendable block that processes the request. Takes `auth` of type `I`,
 * `varI` of type `J` (path variable), and `requestBody` of type `K` as parameters.
 * Returns the response of type `T`.
 * @return The registered route.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any> Route.post(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varI: J, requestBody: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.post(path, configBuilder<T>(variable = pathVar, requestBody = K::class.asKType())) {
        val auth = call.auth<I>()
        val valueI = call.getPathVariable<J>(pathVarJ)
        val requestBody = call.requestBody<K>()
        call.ok(call.request.block(auth, valueI, requestBody))
    }
}


/**
 * Defines a POST route with optional path variables and a request body, and handles
 * authentication and response generation dynamically based on the given parameters.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param L The type of the request body.
 * @param path The path defining the route, optionally including placeholders for path variables.
 * @param block A suspend lambda that processes the route's logic. It receives the authentication
 * object, the values of the path variables, and the parsed request body, and returns a response of type T.
 * @return The configured POST route.
 */
inline fun <reified T, reified I : Any, reified J : Any, reified K : Any, reified L : Any> Route.post(
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

    return this.post(path, configBuilder<T>(variable = pathVar, requestBody = L::class.asKType())) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val requestBody = call.requestBody<L>()
        call.ok(call.request.block(auth, valueJ, valueK, requestBody))
    }
}
