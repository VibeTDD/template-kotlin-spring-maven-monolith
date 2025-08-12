package com.company.examples.domain.validator

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.config.business.BusinessSpecificConfig
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.constant.ExampleErrorCodes
import com.company.examples.domain.port.ExampleStoragePort
import org.springframework.stereotype.Component

@Component
class CountryValidator(
    private val businessSpecificConfig: BusinessSpecificConfig,
) : ExampleValidator {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        if (businessSpecificConfig.getAllowedCountries().contains(command.country)) return emptyList()

        return listOf(
            ValidationError(
                code = ExampleErrorCodes.COUNTRY_NOT_ALLOWED,
                message = "The country is not allowed",
                attributes = mapOf(
                    "country" to command.country,
                    "allowedCountries" to businessSpecificConfig.getAllowedCountries()
                )
            )
        )
    }
}