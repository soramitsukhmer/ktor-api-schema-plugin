package me.learning.api_schema.dto.handler

class AuthUnauthorizedException(override val message: String) : RuntimeException(message)
class InvalidAuthException(override val message: String) : RuntimeException(message)
