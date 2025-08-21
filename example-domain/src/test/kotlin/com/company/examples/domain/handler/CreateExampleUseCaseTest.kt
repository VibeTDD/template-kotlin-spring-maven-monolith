package com.company.examples.domain.handler

import org.junit.jupiter.api.Test

open class CreateExampleUseCaseTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should create example when all business rules satisfied`() {
        // Given
        val givenCommand = validCommand
        setupValidMocks()

        // When - Then
        shouldBeValid(givenCommand)
    }
}