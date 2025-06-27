package me.learning.api_schema.core

import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import me.learning.api_schema.dto.response.ResponseWrapper
import me.learning.api_schema.extension.asKType
import java.io.File
import kotlin.reflect.KClass

/**
 * Configures a schema for the given route using the specified parameters.
 * The schema can include path variables, request bodies, and multipart form-data handling.
 *
 * @param T The response type for the defined schema ResponseWrapper<T>.
 * @param I The class type of the request body if required.
 * @param pathVariable A map specifying the path variables and their respective classes. Default is null.
 * @param bodyFileAsList Determines if the multipart form-data should treat the file as a list of `File` objects. Default is null.
 * @return A lambda function to configure the route's schema.
 */


inline fun <reified T, reified I> Route.schemaBuilder(
    pathVariable: Map<String, KClass<*>>? = null,
    responseWrapper: Boolean = true,
    bodyFileAsList: Boolean? = null,
): RouteConfig.() -> Unit = {

    securitySchemeNames(SECURITY_BEARER_SCHEMA_NAME)

    request {
        bodyFileAsList?.let { fileAsList ->
            // form-data request body
            multipartBody {
                mediaTypes(ContentType.MultiPart.FormData)
                when (I::class) {
                    Nothing::class -> {}
                    Unit::class -> {}
                    Void::class -> {}
                    else -> part<I>("data")
                }
                when (fileAsList) {
                    true -> part<List<File>>("files") { required = true }
                    false -> part<File>("file") { required = true }
                }
            }
        } ?: kotlin.run {
            // raw request body
            pathVariable?.let { it.forEach { (key, value) -> pathParameter(key, value.asKType()) } }
            when (I::class) {
                Nothing::class -> {}
                Unit::class -> {}
                Void::class -> {}
                else -> body<I>()
            }
        }
    }

    response {
        when (T::class) {
            Nothing::class -> {}
            Unit::class -> {}
            Void::class -> {}
            else -> code(HttpStatusCode.OK) {
                if (responseWrapper) body<ResponseWrapper<T>>()
                else body<T>()
            }
        }
    }
}