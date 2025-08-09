package dev.vibetdd.api.commons.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

private val objectMapper = ObjectMapper()
    .findAndRegisterModules()
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

fun Any.toJson(): String = objectMapper.writeValueAsString(this)