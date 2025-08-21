package com.company.examples.domain.handler

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import io.mockk.every
import org.junit.jupiter.api.Test

class CreateExampleUseCaseEmailTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should return error when email already exists`() {
        // Given
        val givenCommand = validCommand
        every { exampleStoragePort.existsByEmail(givenCommand.email) } returns true

        // When - Then
        shouldBeInvalid(
            command = givenCommand,
            error = ValidationError(
                code = ExampleErrorCode.EMAIL_ALREADY_EXISTS,
                attributes = mapOf(
                    ExampleValidationField.EMAIL to givenCommand.email,
                )
            )
        )
    }
}