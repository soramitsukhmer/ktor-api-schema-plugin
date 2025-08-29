package com.skh.api_schema.route.extension

import io.ktor.server.routing.Route
import com.skh.api_schema.dto.route.extension.get.GetDto
import com.skh.api_schema.dto.route.extension.post.PostDto
import com.skh.api_schema.dto.route.extension.put.PutDto

fun Route.get(
    path: String = "",
    responseWrapper: Boolean = true,
    hidden: Boolean = false,
    accessRights: List<String> = emptyList()
) = GetDto(this, path, responseWrapper, hidden, accessRights)

fun Route.post(
    path: String = "",
    responseWrapper: Boolean = true,
    hidden: Boolean = false,
    accessRights: List<String> = emptyList()
) = PostDto(this, path, responseWrapper, hidden, accessRights)

fun Route.put(
    path: String = "",
    responseWrapper: Boolean = true,
    hidden: Boolean = false,
    accessRights: List<String> = emptyList()
) = PutDto(this, path, responseWrapper, hidden, accessRights)
