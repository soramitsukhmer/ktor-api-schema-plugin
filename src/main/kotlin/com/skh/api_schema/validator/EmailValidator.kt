package com.skh.api_schema.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import com.skh.api_schema.extension.isEmail

class EmailValidator : ConstraintValidator<EmailCustom, String> {

    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        var valid = true
        if (!email.isNullOrEmpty()) valid = email.isEmail()
        return valid
    }
}
