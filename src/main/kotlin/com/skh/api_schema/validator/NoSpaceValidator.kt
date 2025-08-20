package com.skh.api_schema.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import com.skh.api_schema.extension.isContainSpace

class NoSpaceValidator : ConstraintValidator<NoSpace, String> {
    override fun isValid(string: String, context: ConstraintValidatorContext?): Boolean {
        return string.isContainSpace()
    }
}
