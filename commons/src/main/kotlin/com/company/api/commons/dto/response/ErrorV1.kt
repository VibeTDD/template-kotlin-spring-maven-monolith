package com.company.api.commons.dto.response

data class ErrorResponseV1(
    val errors: List<ErrorV1>,
)

data class ErrorV1(
    val code: String,
    val message: String,
    val attributes: Map<String, Any?> = mapOf(),
)

object ErrorCode {

    const val NOT_FOUND = "NotFound"
    const val BAD_REQUEST = "BadRequest"
    const val INTERNAL_ERROR = "InternalError"
    const val FORBIDDEN_ACCESS = "ForbiddenAccess"
    const val OUTDATED_VERSION = "OutdatedVersion"
    const val DUPLICATED_KEY = "DuplicatedKey"
}
