package com.company.examples.domain.handler

import com.company.examples.domain.constant.ExampleErrorCodes
import io.mockk.every
import org.junit.jupiter.api.Test

class CreateExampleInvalidEmailUseCaseTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should return error when email already exists`() {
        // Given
        val givenCommand = validCommand
        every { exampleStoragePort.existsByEmail(givenCommand.email) } returns true

        // When - Then
        shouldBeInvalid(givenCommand, ExampleErrorCodes.EMAIL_ALREADY_EXISTS )
    }
}