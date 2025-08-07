package com.company.api.commons.validation.mapper

import com.company.api.commons.validation.dto.ValidationErrorV1
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.validation.FieldError
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

fun FieldError.toError() = ValidationErrorV1(
    code = code ?: "Unknown",
    field = field,
    value = rejectedValue,
    message = defaultMessage ?: "Unexpected error",
    // TODO add attributes
)

fun MethodArgumentTypeMismatchException.toError() = ValidationErrorV1(
    code = "InvalidFormat",
    field = name,
    message = cause?.message ?: message!!,
    attributes = mapOf(Pair("value", value ?: "")),
    value = value,
)

fun MissingKotlinParameterException.toError() = ValidationErrorV1(
    code = "NotNull",
    field = parameter.name.orEmpty(),
    message = "Must not be null",
)
