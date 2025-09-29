package com.skh.api_schema.plugin

import com.skh.api_schema.config.ApiSchemaProperties.validator
import com.skh.api_schema.extension.messages
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.requestValidatorConfigPlugin() {
    if (this.pluginOrNull(RequestValidation) == null) {

        install(RequestValidation) {
            validate<Any> {
                val errors = validator.validate(it)

                when (errors.isEmpty()) {
                    true -> ValidationResult.Valid
                    false -> ValidationResult.Invalid(errors.messages())
                }
            }
        }
    }
}
