package com.skh.api_schema.dto.handler

class UnauthorizedAuthException(override val message: String) : RuntimeException(message)

class InvalidAuthException(override val message: String) : RuntimeException(message)

class MaxRequestFileItem(override val message: String) : RuntimeException(message)
