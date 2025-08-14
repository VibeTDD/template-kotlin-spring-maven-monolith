package com.company.examples.domain.validator

import com.company.examples.domain.port.config.BusinessSpecificConfigPort
import com.company.examples.domain.constant.ExampleErrorCodes
import com.company.examples.domain.mother.CreateExampleCommandMother
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
class CountryValidatorTest {

    @InjectMockKs
    private lateinit var validator: CountryValidator

    @MockK
    private lateinit var businessSpecificConfig: BusinessSpecificConfigPort

    @Test
    fun `should return no errors when country is allowed`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of(
            country = "CA"
        )

        every { businessSpecificConfig.getAllowedCountries() } returns setOf("USA", "CA")

        // When
        val actualErrors = validator.validate(givenCommand)

        // Then
        actualErrors.shouldBeEmpty()
    }

    @Test
    fun `should return error when email already exists`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of(
            country = "ES"
        )

        every { businessSpecificConfig.getAllowedCountries() } returns setOf("USA", "CA")

        // When
        val actualErrors = validator.validate(givenCommand)

        // Then
        actualErrors shouldHaveSize 1
        actualErrors[0].code shouldBe ExampleErrorCodes.COUNTRY_NOT_ALLOWED
    }
}