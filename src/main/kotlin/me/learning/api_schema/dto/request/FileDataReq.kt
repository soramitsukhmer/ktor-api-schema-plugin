package me.learning.api_schema.dto.request

import java.util.Optional

data class FileDataReq<T>(
    val file: FileInfo,
    val data: Optional<T>
)
