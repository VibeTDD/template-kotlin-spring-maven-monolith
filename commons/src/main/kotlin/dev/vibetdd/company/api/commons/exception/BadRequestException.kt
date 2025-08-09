package dev.vibetdd.api.commons.exception

class BadRequestException(message: String, val params: Map<String, Any> = mapOf()) :
    RuntimeException(message + addParams(params))

private fun addParams(params: Map<String, Any>): String =
    if (params.isEmpty()) ""
    else " params: $params"
