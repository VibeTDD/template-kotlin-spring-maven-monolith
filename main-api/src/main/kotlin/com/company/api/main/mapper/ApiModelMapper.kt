package com.company.api.main.mapper

import com.company.api.commons.dto.response.ModelV1
import com.company.api.commons.dto.response.PageV1

fun <T, R> ModelV1<T>.toApiV1(transform: (T) -> R) = ModelV1(
    id = id,
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt,
    data = transform.invoke(data)
)

fun <T, R> PageV1<T>.toApiV1(transform: (T) -> R): PageV1<R> = PageV1(
    totalElements,
    totalPages,
    items.map { transform.invoke(it) }
)