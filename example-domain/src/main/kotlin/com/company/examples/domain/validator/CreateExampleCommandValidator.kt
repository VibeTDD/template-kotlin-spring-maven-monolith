package com.company.examples.domain.validator

import com.company.api.commons.validation.CommandValidator
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.port.config.BusinessSpecificConfigPort
import com.company.examples.domain.port.storage.ExampleStoragePort
import org.springframework.stereotype.Component

@Component
class CreateExampleCommandValidator(
    businessSpecificConfigPort: BusinessSpecificConfigPort,
    exampleStoragePort: ExampleStoragePort,
) : CommandValidator<CreateExampleCommand>(
    listOf(
        EmailValidator(exampleStoragePort),
        CountryValidator(businessSpecificConfigPort)
    )
)