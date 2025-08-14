package com.company.examples.domain.validator

import com.company.examples.domain.constant.ExampleErrorCodes
import com.company.examples.domain.mother.CreateExampleCommandMother
import com.company.examples.domain.port.storage.ExampleStoragePort
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class EmailValidatorTest {

    @InjectMockKs
    private lateinit var validator: EmailValidator

    @MockK
    private lateinit var userStorage: ExampleStoragePort

    @Test
    fun `should return no errors when email does not exist`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of()

        every { userStorage.existsByEmail(givenCommand.email) } returns false

        // When
        val actualErrors = validator.validate(givenCommand)

        // Then
        actualErrors.shouldBeEmpty()
    }

    @Test
    fun `should return error when email already exists`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of()

        every { userStorage.existsByEmail(givenCommand.email) } returns true

        // When
        val actualErrors = validator.validate(givenCommand)

        // Then
        actualErrors shouldHaveSize 1
        actualErrors[0].code shouldBe ExampleErrorCodes.EMAIL_ALREADY_EXISTS
    }
}