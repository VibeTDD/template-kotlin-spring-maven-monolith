package com.company.libs.testing.utils

import com.company.api.commons.validation.exception.ValidationException
import io.kotest.assertions.failure

inline fun <T> shouldNotThrowAnyWithDetails(block: () -> T): T {
    return try {
        block()
    } catch (e: ValidationException) {
        val errorDetails = e.errors.joinToString(separator = "\n")
        throw failure("No exception expected, but a ValidationException was thrown with errors:\n$errorDetails", e)
    } catch (e: Exception) {
        throw failure("No exception expected, but a ${e::class.simpleName} was thrown: ${e.message}", e)
    }
}