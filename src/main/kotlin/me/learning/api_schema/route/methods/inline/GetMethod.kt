package me.learning.api_schema.route.methods.inline

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.getPathVariable
import me.learning.api_schema.extension.ok
import me.learning.api_schema.route.configBuilder
import kotlin.reflect.KClass


/**
 * Defines a GET route that processes requests without authentication. The route handler processes
 * the incoming request and returns a response of type [T].
 *
 * @param T The type of the response body.
 * @param path The path pattern for the route. Defaults to an empty string.
 * @param block A suspendable lambda function that processes the request and produces a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.() -> T
): Route {
    return this.get(path = path, configBuilder<T>(hasAuth = false)) {
        call.ok(call.request.block())
    }
}


/**
 * Defines a GET route with a single path parameter handled dynamically.
 * This method allows for type-safe path extraction and returns a response of type [T].
 *
 * @param T The type of the response body.
 * @param I The type of the path parameter.
 * @param path The URL path for the route. Defaults to an empty string. The path can include placeholders
 *             for path variables in the format `{variable}`.
 * @param pathI The [KClass] representing the type of the path variable.
 * @param block A suspendable lambda function that processes the extracted path variable ([varI])
 *              and returns a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T : Any, reified I : Any> Route.get(
    path: String = "",
    pathI: KClass<I>,
    crossinline block: suspend RoutingRequest.(varI: I) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarI = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarI to I::class)

    return get(path, configBuilder<T>(hasAuth = false, pathVariable = pathVar)) {
        val valueI = call.getPathVariable(pathI, pathVarI)
        call.ok(call.request.block(valueI))
    }
}


/**
 * Defines a GET route with optional authentication and response handling.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param path The URL path for the GET route. Default is an empty string.
 * @param block A lambda function that takes the authentication object of type [I]
 * and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T : Any, reified I : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I) -> T
): Route {
    return get(path, configBuilder<T>()) {
        val auth = call.auth<I>()
        call.ok(call.request.block(auth))
    }
}


/**
 * Defines a GET route with authentication and a path parameter.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the path variable.
 * @param path The URL path for the GET route. Defaults to an empty string. The path can include
 * placeholders for path variables in the format `{variable}`.
 * @param block A suspendable lambda function that takes the authentication object of type [I]
 * and the path parameter of type [J], and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return get(path, configBuilder<T>(pathVariable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        call.ok(call.request.block(auth, valueJ))
    }
}


/**
 * Defines a GET route with customizable path parameters and authentication.
 * This route allows for type-safe handling of authentication and up to two path parameters.
 * The response is generated using the provided lambda function, which processes the authenticated user
 * and parsed path parameters to produce a result of type [T].
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the first path parameter.
 * @param K The type of the second path parameter.
 * @param path The endpoint path for the GET route. Defaults to an empty string.
 *             The path can contain placeholders for path variables in the format `{variable}`.
 * @param block A suspendable lambda function that takes the authenticated user
 *              ([I]) and parsed values of the path parameters ([J] and [K])
 *              as inputs and produces a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return get(path, configBuilder<T>(pathVariable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        call.ok(call.request.block(auth, valueJ, valueK))
    }
}


/**
 * Defines a GET route with customizable path parameters and authentication.
 * This method allows handling multiple path parameters and type-safe parsing of values.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param L The type of the third path variable.
 * @param path The path pattern for the route. Defaults to an empty string. The path can include
 * placeholders for path variables in the format `{variable}`.
 * @param block A suspendable lambda function defining the behavior of the route. It accepts the
 * authentication object ([I]), the parsed values of the first, second, and third path variables
 * ([J], [K], and [L]), and produces a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any, reified L : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, varL: L) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVarL = allPathVar.getOrNull(2) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
        pathVarL to L::class,
    )

    return get(path, configBuilder<T>(pathVariable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val valueL = call.getPathVariable<L>(pathVarL)
        call.ok(call.request.block(auth, valueJ, valueK, valueL))
    }
}
