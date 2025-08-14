package com.company.libs.testing.mother

import com.company.api.commons.model.Model
import com.company.libs.testing.Rand
import java.time.Instant
import java.util.UUID

object ModelMother {

    fun <T> of(
        id: UUID = Rand.uuid(),
        version: Long = Rand.long(),
        createdAt: Instant = Rand.instant(),
        updatedAt: Instant = Rand.instant(),
        data: T
    ) = Model(
        id = id,
        version = version,
        createdAt = createdAt,
        updatedAt = updatedAt,
        data = data
    )

    fun <T> ofV0(
        id: UUID = Rand.uuid(),
        data: T
    ) = of(
        id = id,
        version = 0,
        data = data
    )
}