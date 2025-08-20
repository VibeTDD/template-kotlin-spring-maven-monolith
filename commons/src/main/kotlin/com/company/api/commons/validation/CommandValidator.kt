package com.company.api.commons.validation

import com.company.api.commons.validation.exception.ValidationError
import com.company.api.commons.validation.exception.ValidationException

open class CommandValidator<T>(
    private val validators: List<ValidationRule<T>>
) {

    fun validate(command: T) {
        val errors: List<ValidationError> = validators
            .filter { it.isApplicable(command) }
            .sortedBy { it.getOrder() }
            .flatMap { it.validate(command) }

        if (errors.isNotEmpty()) throw ValidationException(errors)
    }
}

interface ValidationRule<T> {
    fun isApplicable(command: T): Boolean = true
    fun validate(command: T): List<ValidationError>
    fun getOrder(): Int = 0
}