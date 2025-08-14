package com.company.examples.domain.validator

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.constant.ExampleErrorCodes
import com.company.examples.domain.port.storage.ExampleStoragePort

class EmailValidator(
    private val exampleStorage: ExampleStoragePort,
) : ExampleValidator {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        // Note: this layer should not do input validation, like if a field is empty, has valid format etc
        if (exampleStorage.existsByEmail(command.email)) {
            errors.add(
                ValidationError(
                    code = ExampleErrorCodes.EMAIL_ALREADY_EXISTS,
                    message = "Email already exists",
                    attributes = mapOf("email" to command.email)
                )
            )
        }
        
        return errors
    }
}