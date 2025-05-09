package me.learning.api_schema.dto.handler

class InvalidAuthException(override val message: String) : RuntimeException(message)
