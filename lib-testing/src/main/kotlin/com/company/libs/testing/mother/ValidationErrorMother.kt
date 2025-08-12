package com.company.libs.testing.mother

import com.company.api.commons.validation.exception.ValidationError
import com.company.libs.testing.Rand

object ValidationErrorMother {

    fun of(
        code: String = Rand.string(),
        message: String = Rand.string(),
        attributes: Map<String, Any> = emptyMap()
    ) = ValidationError(
        code = code,
        message = message,
        attributes = attributes,
    )
}