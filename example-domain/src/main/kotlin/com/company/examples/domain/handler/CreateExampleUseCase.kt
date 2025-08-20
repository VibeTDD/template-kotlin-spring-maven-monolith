package com.company.examples.domain.handler

import com.company.api.commons.model.Model
import com.company.api.commons.utils.IdProvider
import com.company.api.commons.utils.SystemTimeProvider
import com.company.api.commons.utils.TimeProvider
import com.company.api.commons.utils.UUIDProvider
import com.company.examples.domain.model.Example
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.examples.domain.validator.CreateExampleCommandValidator
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CreateExampleUseCase(
    private val timeProvider: TimeProvider = SystemTimeProvider(),
    private val idProvider: IdProvider<UUID> = UUIDProvider(),
    private val exampleValidator: CreateExampleCommandValidator,
    private val storagePort: ExampleStoragePort,
) {

    fun execute(command: CreateExampleCommand): Model<Example> {
        exampleValidator.validate(command)

        val example = Model(
            id = idProvider.generate(),
            version = 0,
            createdAt = timeProvider.now(),
            updatedAt = timeProvider.now(),
            data = Example(
                email = command.email,
                country = command.country,
            )
        )

        storagePort.create(example)

        return example
    }
}