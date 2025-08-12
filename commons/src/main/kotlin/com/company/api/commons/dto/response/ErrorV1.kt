package com.company.api.commons.dto.response

data class ErrorResponseV1(
    val errors: List<ErrorV1>,
)

data class ErrorV1(
    val code: String,
    val message: String,
    val attributes: Map<String, Any?> = mapOf(),
)

