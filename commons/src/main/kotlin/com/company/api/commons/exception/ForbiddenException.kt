package com.company.api.commons.exception

class ForbiddenException(
    message: String,
    val attributes: Map<String, Any> = mapOf(),
) : Exception(message)
