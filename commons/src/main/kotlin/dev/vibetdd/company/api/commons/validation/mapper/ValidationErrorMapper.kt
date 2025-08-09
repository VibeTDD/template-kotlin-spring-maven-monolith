package dev.vibetdd.api.commons.validation.mapper

import dev.vibetdd.api.commons.dto.response.ErrorV1
import dev.vibetdd.api.commons.validation.exception.ValidationError
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.validation.FieldError
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

fun FieldError.toV1() = createError(
    code = code ?: "Unknown",
    message = defaultMessage ?: "Unexpected error",
    field = field,
    value = rejectedValue,
)

fun MethodArgumentTypeMismatchException.toV1() = createError(
    code = "InvalidFormat",
    field = name,
    message = cause?.message ?: message!!,
    value = value,
)

fun MissingKotlinParameterException.toV1() = createError(
    code = "NotNull",
    field = parameter.name.orEmpty(),
    message = "Must not be null",
)

fun ValidationError.toV1() = ErrorV1(
    code = code,
    message = message,
    attributes = attributes,
)

private fun createError(
    code: String,
    message: String,
    field: String,
    value: Any? = null,
) = ErrorV1(
    code = code,
    message = message,
    attributes = mapOf(
        "field" to field,
        "value" to value,
    ),
)