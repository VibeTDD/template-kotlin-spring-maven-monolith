package dev.vibetdd.api.commons.mapper

import dev.vibetdd.api.commons.dto.response.StatusV1
import dev.vibetdd.api.commons.model.Status

fun <T> Status<T>.toDto() = StatusV1(
    name = name.toString(),
    msg = msg,
)