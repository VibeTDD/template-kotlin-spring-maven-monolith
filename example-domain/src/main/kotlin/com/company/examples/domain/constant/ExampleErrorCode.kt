package com.company.examples.domain.constant

import com.company.api.commons.validation.ValidationCode

enum class ExampleErrorCode(val msg: String) : ValidationCode {
    EMAIL_ALREADY_EXISTS("The email already exists"),
    COUNTRY_NOT_ALLOWED("The country is not allowed"),
    SALARY_OUT_OF_RANGE("The salary must be between {min} and {max} values"),
    ;

    override fun getMessage(): String = msg
    override fun getCode(): String = name
}

object ExampleValidationField {
    const val EMAIL = "email"
    const val COUNTRY = "country"
    const val ALLOWED_COUNTRIES = "allowedCountries"
    const val SALARY = "salary"
    const val MIN = "min"
    const val MAX = "max"
}