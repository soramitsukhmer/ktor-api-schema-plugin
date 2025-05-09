package me.learning.api_schema.route.methods

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
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
inline fun <reified T> get(
    route: Route,
    path: String = "",
    response: T
): Route {
    return route.get(path = path, configBuilder<T>(hasAuth = false)) { call.ok(response) }
}


/**
 * Configures a route to handle HTTP `GET` requests with authentication.
 *
 * @param T The type of the response returned by the handler function.
 * @param I The type representing authentication information.
 * @param route The parent route to which this `GET` request should be added.
 * @param path An optional subpath for the `GET` route. Defaults to an empty string.
 * @param block A suspending lambda that handles the request. It takes one parameter:
 * - `auth`: The authentication information of type `I`.
 * This lambda returns a response of type `T`.
 * @return The configured `Route` object representing this `GET` route.
 */
inline fun <reified T, reified I : Any> get(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I) -> T
): Route {
    return route.get(path = path, configBuilder<T>()) {
        val auth = call.auth<I>()
        call.ok(call.request.block(auth))
    }
}


/**
 * Configures a route to handle HTTP `GET` requests with authentication, query parameters, and response generation.
 *
 * @param T The type of the response returned by the handler function.
 * @param I The type representing authentication information.
 * @param J The type representing the query parameters.
 * @param route The parent route to which this `GET` request should be added.
 * @param path An optional subpath for the `GET` route. Defaults to an empty string.
 * @param queryParam The `KClass` representing the structure and properties of query parameters.
 * @param block A suspending lambda that handles the request. It takes two parameters:
 * - `auth`: The authentication information of type `I`.
 * - `param`: The parsed query parameters of type `J`.
 * This lambda returns a response of type `T`.
 * @return The configured `Route` object representing this `GET` route.
 */
inline fun <reified T, reified I : Any, J : Any> get(
    route: Route,
    path: String = "",
    queryParam: KClass<J>,
    crossinline block: suspend RoutingRequest.(auth: I, param: J) -> T
): Route {
    return route.get(path = path, configBuilder<T>(param = queryParam)) {
        val auth = call.auth<I>()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, param))
    }
}


/**
 * Registers a GET route with support for authentication and pagination.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param route The parent route under which the GET route should be registered.
 * @param path The path of the GET route. Defaults to an empty string.
 * @param block A suspend lambda that handles the request, providing authenticated data and pagination information.
 * @return The modified route with the newly added GET handler.
 */
inline fun <reified T, reified I : Any> get(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, pageRequest: PageRequest) -> T
): Route {
    return route.get(path = path, configBuilder<T>(hasPageRequest = true)) {
        val auth = call.auth<I>()
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, pageRequest))
    }
}


/**
 * Configures a GET route with authentication, pagination, and query parameter handling.
 *
 * @param T The type of the response body.
 * @param I The type used for authentication.
 * @param J The type of query parameter model.
 * @param route The parent route to attach this GET route to.
 * @param path The path for the GET route, relative to the parent route. Default is an empty string.
 * @param queryParam The KClass representing the query parameter model.
 * @param block A suspendable lambda to execute when this route is called. It takes three parameters:
 *              an authentication object of type `I`, a `PageRequest` object for pagination,
 *              and a query parameter object of type `J`. It returns a response object of type `T`.
 * @return The configured route.
 */
inline fun <reified T, reified I : Any, J : Any> get(
    route: Route,
    path: String = "",
    queryParam: KClass<J>,
    crossinline block: suspend RoutingRequest.(auth: I, pageRequest: PageRequest, param: J) -> T
): Route {
    return route.get(path = path, configBuilder<T>(hasPageRequest = true, param = queryParam)) {
        val auth = call.auth<I>()
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, pageRequest, param))
    }
}


/**
 * Registers a GET route in the provided route configuration with support for authentication,
 * path variables, pagination, and custom request handling.
 *
 * @param T The type of the response body returned by the route handler.
 * @param I The type of the authentication object.
 * @param J The type of the path variable extracted from the route.
 * @param route The parent route to which this GET route is added.
 * @param path The relative path that defines the specific route. Defaults to an empty string.
 * @param block A lambda executed for each request to the route. It takes the authentication object
 *              (`auth` parameter), the extracted path variable (`varJ` parameter), and the
 *              pagination request (`pageRequest` parameter) to produce the response of type `T`.
 *
 * @return The configured route with the additional GET route appended.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any> get(
    route: Route,
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, pageRequest: PageRequest) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return route.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar)) {
        val auth = call.auth<I>()
        val valueI = call.getPathVariable<J>(pathVarJ)
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, valueI, pageRequest))
    }
}


/**
 * Registers a GET route with the provided path and handles requests with pagination, path variables,
 * query parameters, and authentication. The handler is defined as a suspendable block to process requests.
 *
 * @param T The type of the response body to be returned.
 * @param I The type used for authentication.
 * @param J The type of the primary path variable extracted from the route.
 * @param K The type representing the query parameters.
 * @param route The routing context to which the GET route will be added.
 * @param path The path of the route, which can include path variables in the format `{variableName}`. Default is an empty string.
 * @param queryParam The `KClass` of type `K` representing the query parameters derived from its properties.
 * @param block The suspendable block of code that processes the request. It takes the following parameters:
 * - `auth`: The authenticated object of type `I`.
 * - `varJ`: The path variable of type `J` extracted from the route.
 * - `pageRequest`: The pagination properties wrapped in a `PageRequest` object.
 * - `param`: The query parameters of type `K`.
 *
 * @return The updated route with the GET handler configured.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, K : Any> get(
    route: Route,
    path: String = "",
    queryParam: KClass<K>,
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, pageRequest: PageRequest, param: K) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return route.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar, param = queryParam)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, valueJ, pageRequest, param))
    }
}


/**
 * Registers a GET request handler with authentication, path variable extraction, and pagination.
 *
 * @param T The type of the response that the handler will return.
 * @param I The type of the authentication object extracted for the request.
 * @param J The type of the first path variable extracted from the request.
 * @param K The type of the second path variable extracted from the request.
 * @param route The route to which this GET handler will be added.
 * @param path The path pattern defining the route, which can include path variables (e.g., `{id}`).
 * @param block The logic to be executed when the route is accessed.
 *        The function is provided the authentication object, the extracted path variables,
 *        and a `PageRequest` object for pagination, and it returns a response of type `T`.
 * @return The route with the registered GET handler.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any> get(
    route: Route,
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

    return route.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val pageRequest = call.pageRequest()
        call.ok(call.request.block(auth, valueJ, valueK, pageRequest))
    }
}


/**
 * Registers a GET route with support for path variables, query parameters,
 * pagination, and authentication. This route handles requests and processes
 * a response based on the provided logic in the `block` parameter.
 *
 * @param T The type of response returned by the route.
 * @param I The type representing the authentication data.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param L The type of the query parameter.
 * @param route The base route to which this GET handler will be attached.
 * @param path The endpoint path, optionally containing path variables denoted by `{}`. Default is an empty string.
 * @param queryParam The class of the query parameter; its properties are parsed from the request query string.
 * @param block A suspending lambda function that executes the logic for the route. It receives:
 * - `auth` of type `I`, representing authentication details.
 * - `varJ` of type `J`, representing the value of the first path variable.
 * - `varK` of type `K`, representing the value of the second path variable.
 * - `pageRequest` of type `PageRequest`, representing pagination details.
 * - `param` of type `L`, representing the parsed query parameter object.
 * The lambda returns a result of type `T` which will be sent as the response.
 * @return The configured `Route` with the GET handler.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any, L : Any> get(
    route: Route,
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

    return route.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar, param = queryParam)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, valueJ, valueK, pageRequest, param))
    }
}
