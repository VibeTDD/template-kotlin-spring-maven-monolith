package com.company.examples.adapter.repository.doc

import com.company.examples.domain.model.Example
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("examples")
// Always add indexes for fields that involved in queries
@CompoundIndexes(
    CompoundIndex(def = "{'data.email': 1}"),
)
data class ExampleDoc(
    val id: String,
    @field:Version
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val data: Example,
)
