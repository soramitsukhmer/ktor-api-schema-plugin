package me.learning.api_schema.core

import me.learning.api_schema.common.Constant.SECURITY_BEARER_SCHEMA_NAME
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import me.learning.api_schema.common.RequestBodyFormDataEnum
import me.learning.api_schema.extension.asKType
import java.io.File
import kotlin.reflect.KClass

fun Route.hasAuth(): Boolean {
    val uri = this.parent?.toString() ?: return false
    return uri.startsWith("/(authenticate")
}

/**
 * Builds a configuration for a route based on provided parameters.
 *
 * @param T The response type for the route.
 * @param hasAuth Indicates if the route requires authentication. Default is true.
 * @param pathVariable A map of path variable names to their respective types. Default is null.
 * @param requestBody The class type of the request body object, if applicable. Default is null.
 * @param requestFormData Specifies if the route handles multipart form data and the form type. Default is null.
 * @param requestBodyFileAsList Information of a request file as a list
 * @return A lambda function to configure the route with the specified parameters.
 */

inline fun <reified T> Route.schemaBuilder(
    pathVariable: Map<String, KClass<*>>? = null,
    requestBody: KClass<*>? = null,
    requestFormData: RequestBodyFormDataEnum? = null,
    requestBodyFileAsList: Boolean = false,
): RouteConfig.() -> Unit = {

    if (hasAuth()) securitySchemeNames(SECURITY_BEARER_SCHEMA_NAME)

    request {
        if (requestFormData == null) {
            // raw
            pathVariable?.let { it.forEach { (key, value) -> pathParameter(key, value.asKType()) } }
            requestBody?.asKType()?.let(::body)
        } else {
            // form-data
            multipartBody {
                mediaTypes(ContentType.MultiPart.FormData)
                if (requestFormData == RequestBodyFormDataEnum.FILE_DATA) {
                    requestBody?.asKType()?.let { part("data", it) { required = !it.isMarkedNullable } }
                }

                if (requestBodyFileAsList) {
                    part<Array<File>>("files") { required = true }
                } else {
                    part<File>("file") { required = true }
                }
            }
        }
    }

    response {
        when (T::class) {
            Void::class -> {}
            else -> code(HttpStatusCode.OK) { body<T>() }
        }
    }
}
