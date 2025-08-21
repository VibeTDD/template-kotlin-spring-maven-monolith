package com.company.api.commons.validation.exception

import com.company.api.commons.validation.ValidationCode

class ValidationException(val errors: List<ValidationError>) : Exception()

data class ValidationError(
    val code: ValidationCode,
    val attributes: Map<String, Any?>
)