package com.company.api.commons.validation.dto

data class ValidationErrorV1(
    val code: String,
    val field: String,
    val value: Any? = null,
    val message: String,
    val attributes: Map<String, Any> = mapOf(),
)
