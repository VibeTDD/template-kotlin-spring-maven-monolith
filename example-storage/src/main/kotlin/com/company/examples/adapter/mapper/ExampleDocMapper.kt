package com.company.examples.adapter.mapper

import com.company.api.commons.model.Model
import com.company.api.commons.utils.toUUID
import com.company.examples.adapter.repository.doc.ExampleDoc
import com.company.examples.domain.model.Example

fun Model<Example>.toDoc() = ExampleDoc(
    id = id.toString(),
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt,
    data = data,
)

fun ExampleDoc.toModel() = Model(
    id = id.toUUID(),
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt,
    data = data,
)