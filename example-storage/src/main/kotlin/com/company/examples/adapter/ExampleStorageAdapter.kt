package com.company.examples.adapter

import com.company.api.commons.exception.ModelDuplicatedException
import com.company.api.commons.model.Model
import com.company.examples.adapter.repository.ExampleRepository
import com.company.examples.domain.model.Example
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.examples.adapter.mapper.toDoc
import com.company.examples.adapter.mapper.toModel
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ExampleStorageAdapter(
    private val repository: ExampleRepository,
): ExampleStoragePort {

    override fun create(example: Model<Example>) {
        try {
            repository.save(example.toDoc())
        } catch (_: DuplicateKeyException) {
            throw ModelDuplicatedException(
                model = Example::class,
                params = mapOf("id" to example.id)
            )
        }
    }

    override fun existsByEmail(email: String): Boolean = repository.existsByDataEmail(email)

    override fun getById(id: UUID): Model<Example>? = repository.findByIdOrNull(id.toString())?.toModel()
}