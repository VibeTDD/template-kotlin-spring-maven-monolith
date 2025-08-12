package com.company.api.commons.model

import java.time.Instant
import java.util.UUID

data class Model<T>(
    val id: UUID,
    val version: Long = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
    val data: T,
)
