package com.company.examples.domain.handler

import com.company.api.commons.exception.ModelNotFoundException
import com.company.api.commons.model.Model
import com.company.examples.domain.model.Example
import com.company.examples.domain.port.storage.ExampleStoragePort
import org.springframework.stereotype.Component
import java.util.*

@Component
class GetExampleUseCase(
    private val storagePort: ExampleStoragePort
) {

    fun execute(id: UUID): Model<Example> = storagePort.getById(id)
        ?: throw ModelNotFoundException(
            model = Example::class,
            params = mapOf("id" to id)
        )
}