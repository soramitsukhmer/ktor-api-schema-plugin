package me.learning.api_schema.dto.request

import java.io.File

data class FileInfoReq(
    val file: File,
    val contentType: String,
    val originalName: String,
    val extension: String,
    val size: Long,
)
