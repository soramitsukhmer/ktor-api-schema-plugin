package com.skh.api_schema.dto.handler

class UnauthorizedAuthException(override val message: String) : RuntimeException(message)

class InvalidAuthException(override val message: String) : RuntimeException(message)

class MaxRequestFileItemException(override val message: String) : RuntimeException(message)

class FileSizeExceededException(override val message: String) : RuntimeException(message)
