package me.learning.api_schema.core

import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import me.learning.api_schema.extension.asKType
import java.io.File
import kotlin.reflect.KClass

fun Route.hasAuth(): Boolean {
    val uri = this.parent?.toString() ?: return false
    return uri.startsWith("/(authenticate")
}

/**
 * Configures a schema for the given route using the specified parameters.
 * The schema can include path variables, request bodies, and multipart form-data handling.
 *
 * @param T The response type for the defined schema.
 * @param pathVariable A map specifying the path variables and their respective classes. Default is null.
 * @param requestBody The class type of the request body if required. Default is null.
 * @param bodyAsFormData Whether the request body should be treated as multipart form-data. Default is false.
 * @param bodyFileAsList Determines if the multipart form-data should treat the file as a list of `File` objects. Default is false.
 * @return A lambda function to configure the route's schema.
 */

inline fun <reified T> Route.schemaBuilder(
    pathVariable: Map<String, KClass<*>>? = null,
    requestBody: KClass<*>? = null,
    bodyAsFormData: Boolean = false,
    bodyFileAsList: Boolean = false,
): RouteConfig.() -> Unit = {

    if (hasAuth()) securitySchemeNames(SECURITY_BEARER_SCHEMA_NAME)

    request {
        if (!bodyAsFormData) {
            // raw
            pathVariable?.let { it.forEach { (key, value) -> pathParameter(key, value.asKType()) } }
            requestBody?.asKType()?.let(::body)
        } else {
            // form-data
            multipartBody {
                mediaTypes(ContentType.MultiPart.FormData)
                requestBody?.let { req ->
                    if (req != Void::class) { part("data", req.asKType()) }
                }
                if (bodyFileAsList) {
                    part<Array<File>>("files") { required = true }
                } else {
                    part<File>("file") { required = true }
                }
            }
        }
    }

    response {
        when (T::class) {
            Unit::class -> {}
            Void::class -> {}
            else -> code(HttpStatusCode.OK) { body<T>() }
        }
    }
}
