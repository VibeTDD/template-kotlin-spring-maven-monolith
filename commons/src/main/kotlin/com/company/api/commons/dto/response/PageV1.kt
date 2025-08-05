package com.company.api.commons.dto.response

data class PageV1<T>(
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val items: List<T> = listOf(),
)
