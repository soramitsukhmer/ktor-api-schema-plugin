package me.learning.api_schema.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import jakarta.validation.Validation

fun Application.requestValidatorConfigPlugin() {
    val validator = Validation.buildDefaultValidatorFactory().validator

    install(RequestValidation) {
        validate<Any> {
            val error = validator.validate(it)

            when (error.isEmpty()) {
                true -> ValidationResult.Valid
                false -> {
                    val messages = error.map { e -> "${e.propertyPath} ${e.message}" }
                    ValidationResult.Invalid(messages)
                }
            }
        }
    }
}
