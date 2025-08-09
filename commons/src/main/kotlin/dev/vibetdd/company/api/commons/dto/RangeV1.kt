package dev.vibetdd.api.commons.dto

data class RangeV1<T>(
    val from: T? = null,
    val to: T? = null,
)
