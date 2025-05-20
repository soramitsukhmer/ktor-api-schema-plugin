package me.learning.api_schema.dto.request

data class FileDataInfoReq<T>(
    val file: FileInfoReq,
    val data: T
)
