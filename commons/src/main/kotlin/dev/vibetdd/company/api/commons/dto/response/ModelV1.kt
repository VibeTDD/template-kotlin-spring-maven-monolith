package dev.vibetdd.api.commons.dto.response

import java.time.Instant
import java.util.*

data class ModelV1<T>(
    val id: UUID,
    val version: Long = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
    val data: T,
)
