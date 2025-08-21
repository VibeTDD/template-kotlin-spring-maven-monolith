package com.company.examples.domain.constant

import com.company.api.commons.validation.ValidationCode

enum class ExampleErrorCode(val msg: String) : ValidationCode {
    EMAIL_ALREADY_EXISTS("The email already exists"),
    COUNTRY_NOT_ALLOWED("The country is not allowed");

    override fun getMessage(): String = msg
    override fun getCode(): String = name
}

object ExampleValidationField {
    const val EMAIL = "email"
    const val COUNTRY = "country"
    const val ALLOWED_COUNTRIES = "allowedCountries"
}