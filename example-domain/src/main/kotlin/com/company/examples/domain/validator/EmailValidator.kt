package com.company.examples.domain.validator

import com.company.api.commons.validation.ValidationRule
import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.port.storage.ExampleStoragePort

class EmailValidator(
    private val exampleStorage: ExampleStoragePort,
) : ValidationRule<CreateExampleCommand> {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        // Note: this layer should not do input validation, like if a field is empty, has valid format etc
        if (exampleStorage.existsByEmail(command.email)) {
            errors.add(
                ValidationError(
                    code = ExampleErrorCode.EMAIL_ALREADY_EXISTS,
                    attributes = mapOf(ExampleValidationField.EMAIL to command.email)
                )
            )
        }
        
        return errors
    }
}