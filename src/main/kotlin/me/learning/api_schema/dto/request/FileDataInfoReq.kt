package me.learning.api_schema.dto.request

data class FileDataInfoReq<T>(
    val file: FileInfoReq,
    val data: T
)

data class FilesDataInfoReq<T>(
    val files: List<FileInfoReq>,
    val data: T
)
