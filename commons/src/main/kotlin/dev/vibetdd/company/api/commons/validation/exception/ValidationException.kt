package dev.vibetdd.api.commons.validation.exception

class ValidationException(val errors: List<ValidationError>) : Exception()

data class ValidationError(
    val code: String,
    val message: String,
    val attributes: Map<String, Any?>
)