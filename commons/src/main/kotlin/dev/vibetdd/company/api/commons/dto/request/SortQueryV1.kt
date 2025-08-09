package dev.vibetdd.api.commons.dto.request

data class SortQueryV1(
    val field: String = "updatedAt",
    val order: String = "DESC",
)