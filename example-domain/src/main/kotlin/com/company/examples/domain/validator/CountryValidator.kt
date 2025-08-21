package com.company.examples.domain.validator

import com.company.api.commons.validation.ValidationRule
import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import com.company.examples.domain.port.config.ExampleConfigPort
import com.company.examples.domain.model.command.CreateExampleCommand

class CountryValidator(
    private val exampleConfigPort: ExampleConfigPort,
) : ValidationRule<CreateExampleCommand> {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        if (exampleConfigPort.getAllowedCountries().contains(command.country)) return emptyList()

        return listOf(
            ValidationError(
                code = ExampleErrorCode.COUNTRY_NOT_ALLOWED,
                attributes = mapOf(
                    ExampleValidationField.COUNTRY to command.country,
                    ExampleValidationField.ALLOWED_COUNTRIES to exampleConfigPort.getAllowedCountries()
                )
            )
        )
    }
}