package com.company.api.commons.mapper

import com.company.api.commons.dto.response.ModelV1
import com.company.api.commons.dto.response.StatusV1
import com.company.api.commons.model.Model
import com.company.api.commons.model.Status

fun <T> Status<T>.toV1() = StatusV1(
    name = name.toString(),
    msg = msg,
)

fun <T, R> Model<T>.toModelV1(itemMapper: (T) -> R) = ModelV1(
    id = id,
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt,
    data = itemMapper.invoke(data)
)