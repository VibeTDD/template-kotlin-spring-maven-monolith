package com.company.examples.domain.validator

import com.company.api.commons.validation.ValidationRule
import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.port.config.BusinessSpecificConfigPort
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.constant.ExampleErrorCodes

class CountryValidator(
    private val businessSpecificConfigPort: BusinessSpecificConfigPort,
) : ValidationRule<CreateExampleCommand> {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        if (businessSpecificConfigPort.getAllowedCountries().contains(command.country)) return emptyList()

        return listOf(
            ValidationError(
                code = ExampleErrorCodes.COUNTRY_NOT_ALLOWED,
                message = "The country is not allowed",
                attributes = mapOf(
                    "country" to command.country,
                    "allowedCountries" to businessSpecificConfigPort.getAllowedCountries()
                )
            )
        )
    }
}