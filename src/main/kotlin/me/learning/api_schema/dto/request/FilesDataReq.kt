package me.learning.api_schema.dto.request

import java.util.Optional

data class FilesDataReq<T>(
    val files: List<FileInfo>,
    val data: Optional<T>
)
