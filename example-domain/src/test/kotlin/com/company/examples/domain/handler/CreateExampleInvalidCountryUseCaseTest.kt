package com.company.examples.domain.handler

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import io.mockk.every
import org.junit.jupiter.api.Test

class CreateExampleInvalidCountryUseCaseTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should return error when country is not allowed`() {
        // Given
        val givenCommand = validCommand.copy(
            country = "ES"
        )

        every { exampleConfigPort.getAllowedCountries() } returns setOf("USA", "CA")

        // When - Then
        shouldBeInvalid(
            command = givenCommand,
            error = ValidationError(
                code = ExampleErrorCode.COUNTRY_NOT_ALLOWED,
                attributes = mapOf(
                    ExampleValidationField.COUNTRY to givenCommand.country,
                    ExampleValidationField.ALLOWED_COUNTRIES to exampleConfigPort.getAllowedCountries()
                )
            )
        )
    }
}