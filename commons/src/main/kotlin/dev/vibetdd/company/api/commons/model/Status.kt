package dev.vibetdd.api.commons.model

data class Status<T>(
    val name: T,
    val msg: String? = null,
)
