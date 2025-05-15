package me.learning.api_schema.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import me.learning.api_schema.extension.isContainSpace

class NoSpaceValidator : ConstraintValidator<NoSpace, String> {
    override fun isValid(string: String, context: ConstraintValidatorContext?): Boolean {
        return string.isContainSpace()
    }
}
