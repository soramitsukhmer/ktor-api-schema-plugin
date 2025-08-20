package com.skh.api_schema.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import com.skh.api_schema.extension.isFax

class FaxValidator : ConstraintValidator<Fax, String> {

    override fun initialize(fax: Fax?){}

    override fun isValid(fax: String?, context: ConstraintValidatorContext?): Boolean {
        var valid = true
        if (!fax.isNullOrEmpty()) valid = fax.isFax()
        return valid
    }
}
