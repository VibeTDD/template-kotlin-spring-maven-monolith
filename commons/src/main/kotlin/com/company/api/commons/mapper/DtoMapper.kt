package com.company.api.commons.mapper

import com.company.api.commons.dto.response.StatusV1
import com.company.api.commons.model.Status

fun <T> Status<T>.toDto() = StatusV1(
    name = name.toString(),
    msg = msg,
)