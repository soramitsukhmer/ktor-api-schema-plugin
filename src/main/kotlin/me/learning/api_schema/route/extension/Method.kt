package me.learning.api_schema.route.extension

import io.ktor.server.routing.Route
import me.learning.api_schema.dto.route.extension.get.GetDto
import me.learning.api_schema.dto.route.extension.post.PostDto
import me.learning.api_schema.dto.route.extension.put.PutDto

fun Route.get(path: String = "") = GetDto(this, path)

fun Route.post(path: String = "") = PostDto(this, path)

fun Route.put(path: String = "") = PutDto(this, path)
