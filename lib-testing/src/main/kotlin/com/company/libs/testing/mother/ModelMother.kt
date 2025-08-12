package com.company.libs.testing.mother

import com.company.api.commons.model.Model
import com.company.libs.testing.Rand
import java.time.Instant
import java.util.UUID

object ModelMother {

    fun <T> of(
        id: UUID = Rand.uuid(),
        version: Long = Rand.long(),
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = Instant.now(),
        data: T
    ) = Model(
        id = id,
        version = version,
        createdAt = createdAt,
        updatedAt = updatedAt,
        data = data
    )
}