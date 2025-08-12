package com.company.examples.domain.validator

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.model.command.CreateExampleCommand

interface ExampleValidator {
    fun validate(command: CreateExampleCommand): List<ValidationError>
}