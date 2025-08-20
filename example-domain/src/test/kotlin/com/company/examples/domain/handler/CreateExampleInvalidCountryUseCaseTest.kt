package com.company.examples.domain.handler

import com.company.examples.domain.constant.ExampleErrorCodes
import io.mockk.every
import org.junit.jupiter.api.Test

class CreateExampleInvalidCountryUseCaseTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should return error when country is not allowed`() {
        // Given
        val givenCommand = validCommand.copy(
            country = "ES"
        )
        every { businessSpecificConfigPort.getAllowedCountries() } returns setOf("USA", "CA")

        // When - Then
        shouldBeInvalid(givenCommand, ExampleErrorCodes.COUNTRY_NOT_ALLOWED )
    }
}