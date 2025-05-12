package me.learning.api_schema.route.methods

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import io.ktor.server.routing.route
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.common.extension.auth
import me.learning.api_schema.common.extension.getPathVariable
import me.learning.api_schema.common.extension.ok
import me.learning.api_schema.common.extension.pageRequest
import me.learning.api_schema.common.extension.queryParameter
import me.learning.api_schema.dto.request.PageRequest
import me.learning.api_schema.route.configBuilder
import kotlin.reflect.KClass


/**
 * Configures a route to handle HTTP `GET` requests with optional authentication and a static response body.
 *
 * @param T The type of the response body to be returned.
 * @param route The parent route to which this `GET` request should be added.
 * @param path An optional subpath for the `GET` route. Defaults to an empty string.
 * @param response The static response body of type `T` that will be returned for this route.
 * @return The configured `Route` object representing this `GET` route.
 */
inline fun <reified T> Route.get(
    path: String = "",
    response: T
): Route {
    return this.get(path = path, configBuilder<T>(hasAuth = false)) { call.ok(response) }
}


/**
 * Defines a GET route that supports authentication and generates a response of type [T].
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param path The URL path for the GET route. Default is an empty string.
 * @param block A lambda function that takes the authentication object of type [I]
 * and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T, reified I : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I) -> T
): Route {
    return this.get(path = path, configBuilder<T>()) {
        val auth = call.auth<I>()
        call.ok(call.request.block(auth))
    }
}


/**
 * Defines a GET route with optional query parameter and authentication handling.
 * The route handler processes the incoming request and returns a response of type [T].
 *
 * @param T The type of the response body.
 * @param I The type of the authenticated user object.
 * @param J The type of the query parameter.
 * @param path The URL path for the GET route. Default is an empty string.
 * @param queryParam The class to derive query parameters from its properties.
 * @param block A suspendable lambda function that takes the authenticated user ([I]),
 * the parsed query parameter ([J]), and produces a response of type [T].
 * @return The configured [Route] instance.
 */
inline fun <reified T, reified I : Any, J : Any> Route.get(
    path: String = "",
    queryParam: KClass<J>,
    crossinline block: suspend RoutingRequest.(auth: I, param: J) -> T
): Route {
    return this.get(path = path, configBuilder<T>(param = queryParam)) {
        val auth = call.auth<I>()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, param))
    }
}


/**
 * Defines a GET route with optional authentication and pagination handling.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param path The URL*/
inline fun <reified T, reified I : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, pageRequest: PageRequest) -> T
): Route {
    return this.get(path = path, configBuilder<T>(hasPageRequest = true)) {
        val auth = call.auth<I>()
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, pageRequest))
    }
}


/**
 * Registers a `GET` route with the specified path and query parameters, along with authentication, pagination, and processing logic.
 *
 * @param T The type of the response body.
 * @param I The type representing the authenticated user.
 * @param J The type of query parameter to be parsed from the request.
 * @param path The specific path of the route. Defaults to an empty string.
 * @param queryParam The class of the query parameter type to be extracted from the request.
 * @param block A lambda function containing the processing logic for the request. Accepts the authenticated user (`auth`),
 * the pagination information (`pageRequest`), and the query parameters (`param`) as parameters, and returns the response of type `T`.
 * @return The configured `Route` object.
 */
inline fun <reified T, reified I : Any, J : Any> Route.get(
    path: String = "",
    queryParam: KClass<J>,
    crossinline block: suspend RoutingRequest.(auth: I, pageRequest: PageRequest, param: J) -> T
): Route {
    return this.get(path = path, configBuilder<T>(hasPageRequest = true, param = queryParam)) {
        val auth = call.auth<I>()
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, pageRequest, param))
    }
}


/**
 * Registers an HTTP GET request handler for the specified route that supports
 * path variables, authentication, and pagination.
 *
 * @param path The path pattern for the route. Defaults to an empty string.
 * @param block A suspend function that is executed when the route is accessed. It provides:
 * - `auth`: An authentication object of type `I`.
 * - `varJ`: A path variable of type `J` extracted based on the route definition.
 * - `pageRequest`: A [PageRequest] object containing pagination details.
 * The function returns a result of type `T` that will be sent as the response body.
 *
 * @return The configured [Route].
 */
inline fun <reified T : Any, reified I : Any, reified J : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, pageRequest: PageRequest) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar)) {
        val auth = call.auth<I>()
        val valueI = call.getPathVariable<J>(pathVarJ)
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, valueI, pageRequest))
    }
}


/**
 * Registers a GET route with support for authentication, query parameters, path variables,
 * pagination, and request processing for the specified route path.
 *
 * @param path The URI path for the route. Default is an empty string.
 * @param queryParam The class type that defines the query parameters for the request.
 * @param block A suspend function that processes the request, providing the following parameters:
 *  - [auth]: The authentication information of type [I].
 *  - [varJ]: A variable derived from the first path parameter in the route, of type [J].
 *  - [pageRequest]: A [PageRequest] object that represents pagination details.
 *  - [param]: An instance of the query parameters of type [K].
 * The suspend function returns a response of type [T].
 *
 * @return The configured [Route] instance for the GET route.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, K : Any> Route.get(
    path: String = "",
    queryParam: KClass<K>,
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, pageRequest: PageRequest, param: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar, param = queryParam)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, valueJ, pageRequest, param))
    }
}


/**
 * Defines a GET endpoint within the routing scope that processes authentication,
 * path variables, pagination, and executes the provided block.
 *
 * @param path The endpoint path, supporting optional path variables in the form {variableName}. Default is an empty string.
 * @param block A suspend lambda function that processes the request. It receives:
 * - `auth`: The authenticated user or context of type `I`.
 * - `varJ`: A path*/
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, pageRequest: PageRequest) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return this.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, valueJ, valueK, pageRequest))
    }
}


/**
 * Defines a GET route with support for path variables, query parameters, authentication, and pagination.
 *
 * @param T The type of the response body.
 * @param I The authentication principal type required for the route.
 * @param J The type of the first path variable in the route (if any).
 * @param K The type of the second path variable in the route (if any).
 * @param L The type of the query parameter class used to extract parameters.
 * @param path The route path. Supports path variables in the form of `{variableName}`. Defaults to an empty string.
 * @param queryParam The class representing the query parameters for the route. All properties of the class are treated as query parameters.
 * @param block A suspending lambda that processes the incoming request. The lambda receives:
 *  - `auth`: The authenticated principal of type [I].
 *  - `varJ`: The first path variable of type [J].
 *  - `varK`: The second path variable of type [K].
 *  - `pageRequest`: A [PageRequest] containing pagination information.
 *  - `param`: An instance of type [L] containing the extracted query parameter values.
 *  Returns a response of type [T].
 * @return The defined route.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any, L : Any> Route.get(
    path: String = "",
    queryParam: KClass<L>,
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, pageRequest: PageRequest, param: L) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVarK = allPathVar.getOrNull(1) ?: ""
    val pathVar = mapOf(
        pathVarJ to J::class,
        pathVarK to K::class,
    )

    return this.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar, param = queryParam)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, valueJ, valueK, pageRequest, param))
    }
}
