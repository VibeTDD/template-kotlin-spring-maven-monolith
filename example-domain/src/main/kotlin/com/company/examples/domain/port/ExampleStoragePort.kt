package com.company.examples.domain.port

import com.company.api.commons.model.Model
import com.company.examples.domain.model.Example
import java.util.UUID

interface ExampleStoragePort {
    fun create(example: Model<Example>)
    fun existsByEmail(email: String): Boolean
    fun getById(id: UUID): Model<Example>?
}