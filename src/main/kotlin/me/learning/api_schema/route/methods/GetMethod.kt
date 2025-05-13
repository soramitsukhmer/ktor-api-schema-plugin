package me.learning.api_schema.route.methods

import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import io.ktor.server.routing.route
import me.learning.api_schema.common.Helper.extractAllPathParameters
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.getPathVariable
import me.learning.api_schema.extension.ok
import me.learning.api_schema.extension.pageRequest
import me.learning.api_schema.extension.queryParameter
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
 * Configures a GET route with optional authentication and pagination.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication object.
 * @param path The URL path for the GET route. Defaults to an empty string.
 * @param block A lambda function that takes the authentication object of type [I] and a pagination
 * request object of type [PageRequest], and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
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
 * Defines a route that handles GET requests with support for authentication, pagination, and query parameter parsing.
 *
 * @param T The type of the response returned by the `block` function.
 * @param I The type of the authentication principal, required for authorization.
 * @param J The type representing the query parameter model.
 * @param path The endpoint path for the route. Defaults to an empty string.
 * @param queryParam The KClass of the query parameter model used for parsing query parameters.
 * @param block The suspendable function that processes the request. It provides the authentication object of type `I`,
 *              the parsed query parameters of type `J`, and the pagination details as a `PageRequest` object,
 *              returning a response of type `T`.
 * @return The configured `Route` object.
 */
inline fun <reified T, reified I : Any, J : Any> Route.get(
    path: String = "",
    queryParam: KClass<J>,
    crossinline block: suspend RoutingRequest.(auth: I, param: J, pageRequest: PageRequest) -> T
): Route {
    return this.get(path = path, configBuilder<T>(hasPageRequest = true, param = queryParam)) {
        val auth = call.auth<I>()
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, param, pageRequest))
    }
}


/**
 * Defines a GET route with authentication and a path variable, and returns a response of type [T].
 *
 * @param T The type of the response object.
 * @param I The type of the authentication object.
 * @param J The type of the path variable.
 * @param path The URL path for the GET route. Defaults to an empty string.
 *             The path can include placeholders for path variables in the format `{variable}`.
 * @param block A suspendable lambda function that takes the authentication object of type [I]
 *              and the path variable of type [J], and returns the response object of type [T].
 * @return The configured [Route] instance.
 */
@JvmName("getAuthWithPathVar")
inline fun <reified T, reified I : Any, reified J : Any> Route.get(
    path: String = "",
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.get(path, configBuilder<T>(variable = pathVar)) {
        val auth = call.auth<I>()
        val valueI = call.getPathVariable<J>(pathVarJ)
        call.ok(call.request.block(auth, valueI))
    }
}


/**
 * Defines a GET route with authentication, path variables, and pagination support.
 *
 * @param T The type of the response object returned by the route handler.
 * @param I The type of the authentication object injected into the route handler.
 * @param J The type of the path variable extracted from the route's path.
 * @param path The route path, which may include path variables (e.g., `{variableName}`).
 * @param block A suspendable lambda function that defines the route's behavior. The lambda receives the authentication object of type [I],
 * the path variable of type [J], and a [PageRequest] object representing pagination information. It should return an object of type [T] that represents the response.
 * @return The newly created [Route].
 */
@JvmName("getAuthWithPathVarAndPageRequest")
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
 * Registers a GET route with the specified path, query parameters, and handler logic.
 *
 * @param T The type of the response body.
 * @param I The type representing the authentication information.
 * @param J The type of the extracted path variable.
 * @param K The class representing query parameters.
 * @param path The route path for this GET request. Defaults to an empty string.
 * @param queryParam The class defining the query parameters expected for the route.
 * @param block A lambda function that processes the GET request. The lambda receives the authentication information,
 * the extracted path variable, the mapped query parameters, and a `PageRequest` instance for pagination. It returns
 * a response of type `T`.
 * @return The `Route` object representing the registered GET route.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, K : Any> Route.get(
    path: String = "",
    queryParam: KClass<K>,
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, param: K, pageRequest: PageRequest) -> T
): Route {
    val allPathVar = extractAllPathParameters(path)
    val pathVarJ = allPathVar.firstOrNull() ?: ""
    val pathVar = mapOf(pathVarJ to J::class)

    return this.get(path, configBuilder<T>(hasPageRequest = true, variable = pathVar, param = queryParam)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val pageRequest = call.pageRequest()
        val param = call.queryParameter(queryParam)
        call.ok(call.request.block(auth, valueJ, param, pageRequest))
    }
}


/**
 * Handles a GET HTTP request at the specified `path`, authenticating the request and extracting
 * path variables. Executes the provided `block` with the request context, authentication information,
 * and extracted path variables.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication information required for the request.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param path The path for the GET route. Default is an empty string. It may contain path
 * variables in the format `{varName}`.
 * @param block A suspending lambda function to handle the request, taking authentication data,
 * the first path variable, and the second path variable as parameters, and returning a response
 * of type `T`.
 * @return The configured `Route` after applying the GET handler.
 */
@JvmName("getAuthWithPathVarAndVarK")
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

    return this.get(path, configBuilder<T>(variable = pathVar)) {
        val auth = call.auth<I>()
        val valueJ = call.getPathVariable<J>(pathVarJ)
        val valueK = call.getPathVariable<K>(pathVarK)
        call.ok(call.request.block(auth, valueJ, valueK))
    }
}


/**
 * Configures a GET route with pagination, path parameters, and authentication.
 *
 * @param T The type of the response body.
 * @param I The type of the authentication data.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param path The route path, which can include placeholders for path variables. Defaults to an empty string.
 * @param block A suspending lambda that represents the business logic of the route.
 *              It receives the authentication data, the first and second path variables,
 *              and pagination details as parameters, and returns a response of type `T`.
 * @return The configured route.
 */
@JvmName("getAuthWithPathVarAndVarKAndPageRequest")
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
 * Creates a GET route that handles authentication, path variables, query parameters, and pagination,
 * and uses a provided block to process the request and produce a response.
 *
 * @param T The type of the response body.
 * @param I The type of the required authentication object.
 * @param J The type of the first path variable.
 * @param K The type of the second path variable.
 * @param L The type of the query parameter class.
 * @param path The route's path, optionally containing placeholders for path variables. Defaults to an empty string.
 * @param queryParam The class used to derive query parameters.
 * @param block A suspending lambda that processes the request and produces the response, using the following parameters:
 * - auth: The authenticated object of type `I`.
 * - varJ: The first path variable of type `J`.
 * - varK: The second path variable of type `K`.
 * - param: The query parameter object of type `L`.
 * - pageRequest: The pagination details as a `PageRequest` object.
 *
 * @return The configured route.
 */
inline fun <reified T : Any, reified I : Any, reified J : Any, reified K : Any, L : Any> Route.get(
    path: String = "",
    queryParam: KClass<L>,
    crossinline block: suspend RoutingRequest.(auth: I, varJ: J, varK: K, param: L, pageRequest: PageRequest) -> T
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
        call.ok(call.request.block(auth, valueJ, valueK, param, pageRequest))
    }
}
