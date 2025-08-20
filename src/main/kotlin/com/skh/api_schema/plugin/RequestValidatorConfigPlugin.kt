package com.skh.api_schema.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import jakarta.validation.Validation

fun Application.requestValidatorConfigPlugin() {
    if (this.pluginOrNull(RequestValidation) == null) {
        val validator = Validation.buildDefaultValidatorFactory().validator

        install(RequestValidation) {
            validate<Any> {
                val error = validator.validate(it)

                when (error.isEmpty()) {
                    true -> ValidationResult.Valid
                    false -> {
                        val messages = error.map { e -> e.message.replace("{field}", e.propertyPath.toString()) }
                        ValidationResult.Invalid(messages)
                    }
                }
            }
        }
    }
}
