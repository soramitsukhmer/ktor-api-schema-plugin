package com.skh.api_schema.validator

import com.google.i18n.phonenumbers.PhoneNumberUtil
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import com.skh.api_schema.common.Constant.DEFAULT_PHONE_REGION
import com.skh.api_schema.extension.isPhone

class PhoneNumberValidator : ConstraintValidator<PhoneNumber, String> {
    private val phoneUtil = PhoneNumberUtil.getInstance()

    override fun initialize(phoneNumber: PhoneNumber?) {}

    override fun isValid(phoneNumber: String?, context: ConstraintValidatorContext): Boolean {
        return try {
            var valid = true
            phoneNumber?.let {
                if(it != ""){
                    valid = it.isPhone()
                    phoneUtil.isValidNumber(phoneUtil.parse(phoneNumber, DEFAULT_PHONE_REGION))
                }
            }
            return valid
        } catch (e: Exception) { false }
    }
}
