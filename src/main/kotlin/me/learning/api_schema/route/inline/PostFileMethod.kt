package me.learning.api_schema.route.inline

import io.github.smiley4.ktoropenapi.post
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRequest
import me.learning.api_schema.common.Helper.badRequest
import me.learning.api_schema.common.MethodEnum
import me.learning.api_schema.common.RequestBodyFormDataEnum
import me.learning.api_schema.dto.handler.throwOnFileReqBody
import me.learning.api_schema.dto.request.FileDataInfoReq
import me.learning.api_schema.dto.request.FileInfoReq
import me.learning.api_schema.dto.request.FilesDataInfoReq
import me.learning.api_schema.extension.auth
import me.learning.api_schema.extension.getRequest
import me.learning.api_schema.extension.ok
import me.learning.api_schema.core.schemaBuilder

// ================================================================================================================ //
// ================================================================================================================ //
// =====================                                                              ============================= //
// =====================                    BLOCK POST WITH FILE                      ============================= //
// ===================== to check local storage: System.getProperty("java.io.tmpdir") ============================= //
// =====================                                                              ============================= //
// ================================================================================================================ //
// ================================================================================================================ //

/**
 * Handles a POST request for file upload. Configures the route to accept a single file
 * as multipart form-data, applies optional file extension validation, and processes the uploaded file.
 *
 * @param T The response type returned by the processing block.
 * @param path The path for the route. Default is an empty string, indicating the root path.
 * @param extensions A list of valid file extensions for the uploaded file. If empty, all extensions are allowed.
 * @param deleteAfterFinish Indicates whether the uploaded file should be deleted after processing. Default is false.
 * @param block A suspendable lambda function to process the uploaded file. Receives the file information as a `FileInfoReq` object and returns a response of type `T`.
 * @return The configured `Route` instance.
 */
inline fun <reified T> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(file: FileInfoReq) -> T
): Route {
    val builder = schemaBuilder<T>(requestFormData = RequestBodyFormDataEnum.FILE)

    return this.post(path, builder) {
        var request: FileInfoReq? = null

        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> { request = part.getRequest(request != null, extensions) }
                else -> {}
            }
            part.dispose()
        }

        request?.let { call.ok(call.request.block(it)) } ?: badRequest("Invalid request file cannot be empty")

        if (deleteAfterFinish) request?.file?.delete()
    }
}


/**
 * Handles a POST request for uploading a file with additional processing and validation.
 *
 * @param path The endpoint path for the route. Default is an empty string.
 * @param extensions A list of allowed file extensions. Default is an empty list, meaning no restrictions.
 * @param deleteAfterFinish A flag indicating whether to delete the uploaded file after processing. Default is false.
 * @param block A lambda function that processes the request. It receives the authenticated user of type `I`
 *              and the file information of type `FileInfoReq` as parameters, and returns a result of type `T`.
 * @return The configured route object.
 */
@JvmName("postFileInfoReq")
inline fun <reified T, reified I : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, file: FileInfoReq) -> T
): Route {
    val builder = schemaBuilder<T>(requestFormData = RequestBodyFormDataEnum.FILE)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        var request: FileInfoReq? = null

        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> { request = part.getRequest(request != null, extensions) }
                else -> {}
            }
            part.dispose()
        }

        request?.let { call.ok(call.request.block(auth, it)) } ?: badRequest("Invalid request file cannot be empty")

        if (deleteAfterFinish) request?.file?.delete()
    }
}


/**
 * Handles the creation of a POST route to upload a file along with additional data.
 *
 * @param T The response type for the route.
 * @param I The type of the authentication object required for the route.
 * @param J The type of the additional data sent with the file.
 * @param path The route path to register. Defaults to an empty string.
 * @param extensions A list of allowed file extensions. Defaults to an empty list.
 * @param deleteAfterFinish If true, deletes the uploaded file after processing. Defaults to false.
 * @param block The logic to handle the uploaded file and additional data, receiving the authentication object and the file data with additional information.
 * @return The created route.
 */
@JvmName("postFileDataInfoReq")
inline fun <reified T, reified I : Any, reified J : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, file: FileDataInfoReq<J>) -> T
): Route {
    J::class.throwOnFileReqBody(MethodEnum.POST, path)

    val builder = schemaBuilder<T>(requestBody = J::class, requestFormData = RequestBodyFormDataEnum.FILE_DATA)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        var fileReq: FileInfoReq? = null
        var dataReq: J? = null

        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> { fileReq = part.getRequest(fileReq != null, extensions) }
                is PartData.FormItem -> { dataReq = part.getRequest(dataReq != null) }
                else -> {}
            }
            part.dispose()
        }

        val request = FileDataInfoReq(
            file = fileReq ?: badRequest("Invalid request file cannot be empty"),
            data = dataReq ?: badRequest("Invalid request data cannot be empty")
        )

        call.ok(call.request.block(auth, request))

        if (deleteAfterFinish) request.file.file.delete()
    }
}


/**
 * Defines a POST route that handles file uploads and processes multiple files as part of
 * the request. This route expects multipart form-data containing a list of files, with
 * configurable options and behavior for file validation and post-processing.
 *
 * @param T The type of the response returned by the route.
 * @param I The type of the authentication object expected for this route.
 * @param path The relative path of the route. Defaults to an empty string, representing the root of the current route context.
 * @param extensions A list of valid file extensions. Files with extensions not in the list will be rejected. Defaults to an empty list, allowing all extensions.
 * @param deleteAfterFinish If true, any received files will be deleted from the storage after processing is complete.*/
@JvmName("postFileInfoAsListReq")
inline fun <reified T, reified I : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, file: List<FileInfoReq>) -> T
): Route {
    val builder = schemaBuilder<I>(requestFormData = RequestBodyFormDataEnum.FILE_DATA, requestBodyFileAsList = true)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        val collection = mutableListOf<FileInfoReq>()

        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> part.getRequest(false, extensions)?.let(collection::add)
                else -> {}
            }
            part.dispose()
        }

        call.ok(call.request.block(auth, collection))

        if (deleteAfterFinish) collection.map { it.file.delete() }
    }
}


/**
 * Defines a POST route to handle file uploads along with associated data object.
 *
 * This method allows the upload of multiple files along with a JSON-formatted data object. The uploaded files
 * and the associated data are passed to the provided `block` lambda for processing. The uploaded files can optionally
 * be deleted after processing is completed.
 *
 * @param T The type of the response returned by the `block` lambda.
 * @param I The authentication type required for the route.
 * @param J The type of the data object sent along with the file upload.
 * @param path The path of the route. Default is an empty string.
 * @param extensions A list of supported file extensions. If empty, all extensions are accepted. Default is an empty list.
 * @param deleteAfterFinish If true, the uploaded files will be deleted after processing is completed. Default is false.
 * @param block A suspending lambda function for processing the request. It receives an authentication object of type `I`
 *              and a `FilesDataInfoReq` object containing the uploaded files and the associated data of type `J`.
 *              The lambda must return an object of type `T`.
 * @return The configured `Route` object.
 */
@JvmName("postFileDataInfoAsListReq")
inline fun <reified T, reified I : Any, reified J : Any> Route.POSTFILE(
    path: String = "",
    extensions: List<String> = emptyList(),
    deleteAfterFinish: Boolean = false,
    crossinline block: suspend RoutingRequest.(auth: I, request: FilesDataInfoReq<J>) -> T
): Route {
    J::class.throwOnFileReqBody(MethodEnum.POST, path)

    val builder = schemaBuilder<T>(requestBody = J::class, requestFormData = RequestBodyFormDataEnum.FILE_DATA, requestBodyFileAsList = true)

    return this.post(path, builder) {
        val auth = call.auth<I>()
        val collection = mutableListOf<FileInfoReq>()
        var dataReq: J? = null

        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> part.getRequest(false, extensions)?.let(collection::add)
                is PartData.FormItem -> { dataReq = part.getRequest(dataReq != null) }
                else -> {}
            }
            part.dispose()
        }
        val request = FilesDataInfoReq(
            data = dataReq ?: badRequest("Invalid request data cannot be empty"),
            files = collection
        )

        call.ok(call.request.block(auth, request))

        if (deleteAfterFinish) collection.map { it.file.delete() }
    }
}
