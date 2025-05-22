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

        if (deleteAfterFinish) { request?.file?.delete() }
    }
}

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

        if (deleteAfterFinish) { request.file.file.delete() }
    }
}


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

        if (deleteAfterFinish) { collection.map { it.file.delete() } }
    }
}


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

        if (deleteAfterFinish) {  collection.map { it.file.delete() } }
    }
}
