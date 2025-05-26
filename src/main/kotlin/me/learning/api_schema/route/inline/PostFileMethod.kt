package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.dto.handler.throwOnFileReqBody
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.ok
import me.learning.api_schema.core.schemaBuilder
import me.learning.api_schema.dto.request.FileDataReq
import me.learning.api_schema.dto.request.FilesDataReq
import me.learning.api_schema.extension.fileInfoRequest

// ================================================================================================================ //
// ================================================================================================================ //
// =====================                                                              ============================= //
// =====================                    BLOCK POST WITH FILE                      ============================= //
// ===================== to check local storage: System.getProperty("java.io.tmpdir") ============================= //
// =====================                                                              ============================= //
// ================================================================================================================ //
// ================================================================================================================ //


/**
 * Handles a POST request that involves uploading a file to the specified route. It processes the incoming file
 * and an optional data payload (if provided) and executes a custom processing block with the parsed request.
 *
 * @param path The URL path for the route. Defaults to an empty string, which matches the root.
 * @param extensions A list of valid file extensions for the uploaded file (e.g., ["jpg", "png"]). Defaults to an empty list, meaning no restrictions.
 * @param deleteAfterFinish Whether to delete the uploaded file from the server after processing is complete. Defaults to false.
 * @param block A suspending lambda that processes the `FileDataReq` object containing the uploaded file and optional data.
 *              The lambda returns an instance of the specified response type `T`.
 * @return The configured `Route` instance for this POST endpoint.
 */
inline fun <reified T, reified I : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(file: FileDataReq<I>) -> T
): Route {
    MethodEnum.POST.throwOnFileReqBody<I>(path)
    val builder = schemaBuilder<T>(requestBody = I::class, requestBodyAsFormData = true)

    return this.post(path, builder) {
        val request = call.fileInfoRequest<FileDataReq<I>, I>(path, extensions)
        call.ok(call.request.block(request))
        if (deleteAfterFinish) request.file.file.delete()
    }
}

/**
 * Defines a POST route to handle file upload with additional data. Supports multipart form-data with file uploads,
 * and optionally deletes the uploaded files after processing the request.
 *
 * @param path The URL path*/
inline fun <reified T, reified I : Any> Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(file: FilesDataReq<I>) -> T
): Route {
    MethodEnum.POST.throwOnFileReqBody<I>(path)
    val builder = schemaBuilder<T>(requestBody = I::class, requestBodyAsFormData = true, bodyFileAsList = true)

    return this.post(path, builder) {
        val request = call.fileInfoRequest<FilesDataReq<I>, I>(path, extensions)
        call.ok(call.request.block(request))
        if (deleteAfterFinish) request.files.forEach { it.file.delete() }
    }
}


/**
 * Defines a POST route that allows file uploads with optional additional data.
 *
 * @param path The endpoint path for the route. Default is an empty string.
 * @param extensions A list of allowed file extensions. Default is an empty list, indicating no restrictions.
 * @param deleteAfterFinish A flag indicating whether the uploaded file should be deleted after processing. Default is false.
 * @param block A suspendable lambda that processes the request. It receives authentication details of type `I`
 *              and a `FileDataReq` object containing the uploaded file and optional additional data of type `J`.
 * @return The configured route.
 */
inline fun <reified T, reified I : Any, reified J : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, file: FileDataReq<J>) -> T
): Route {
    MethodEnum.POST.throwOnFileReqBody<J>(path)
    val builder = schemaBuilder<T>(requestBody = J::class, requestBodyAsFormData = true)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        val request = call.fileInfoRequest<FileDataReq<J>, J>(path, extensions)
        call.ok(call.request.block(auth, request))
        if (deleteAfterFinish) request.file.file.delete()
    }
}


/**
 * Registers a POST route to handle file uploads with specific configurations.
 *
 * This method supports handling multipart form-data requests where multiple files
 * can be uploaded, along with optional additional data. Upon completion, files
 * can optionally be deleted from the server based on the input configuration.
 *
 * @param path The relative path for this route. Defaults to an empty string.
 * @param extensions A list of allowed file extensions. An empty list allows all extensions. Defaults to an empty list.
 * @param deleteAfterFinish If true, deletes uploaded files after processing is complete. Defaults to false.
 * @param block A suspending lambda that provides an authenticated context and the file upload data.
 *              The lambda receives two parameters:
 *              - `auth`: The authentication data parsed from the request.
 *              - `file`: The file data request containing the metadata and associated additional data.
 *              The lambda returns a response of type `T`.
 * @return The registered Route instance.
 */
inline fun <reified T, reified I : Any, reified J : Any> Route.POSTFILES(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, file: FilesDataReq<J>) -> T
): Route {
    MethodEnum.POST.throwOnFileReqBody<I>(path)
    val builder = schemaBuilder<T>(requestBody = J::class, requestBodyAsFormData = true, bodyFileAsList = true)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        val request = call.fileInfoRequest<FilesDataReq<J>, J>(path, extensions)
        call.ok(call.request.block(auth, request))
        if (deleteAfterFinish) request.files.forEach { it.file.delete() }
    }
}
