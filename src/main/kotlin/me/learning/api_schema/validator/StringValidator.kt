package me.learning.api_schema.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StringValidator : ConstraintValidator<StringValid, String> {
    private val log: Logger = LoggerFactory.getLogger(StringValid::class.java)
    private var min: Int = Int.MIN_VALUE
    private var max: Int = Int.MAX_VALUE
    private var isOptional: Boolean = true

    override fun isValid(strValue: String?, context: ConstraintValidatorContext?): Boolean {
        // null values are valid
        val length = strValue?.length ?: 0
        val valid = if (isOptional) { length == 0 || length in min..max }
        else { length in min..max }

        if (!valid) {
            val message = "invalid format length[$length], min[$min] and max[$max]"
            log.error(message)
            return false
        }
        return true
    }

    override fun initialize(annotation: StringValid?) {
        super.initialize(annotation)
        annotation?.isOptional?.let { isOptional = it }
        min = annotation?.min!!
        max = annotation.max
    }
}
