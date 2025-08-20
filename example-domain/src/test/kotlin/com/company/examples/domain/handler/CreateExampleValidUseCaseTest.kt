package com.company.examples.domain.handler

import com.company.api.commons.model.Model
import com.company.examples.domain.model.Example
import com.company.libs.testing.Rand
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.Instant

open class CreateExampleValidUseCaseTest: CreateExampleUseCaseBaseTest() {

    @Test
    fun `should create example when command is valid`() {
        // Given
        val givenCommand = validCommand
        val expectedId = Rand.uuid()
        val expectedTime = Instant.now()

        every { idProvider.generate() } returns expectedId
        every { timeProvider.now() } returns expectedTime

        // When
        val actualResult = useCase.execute(givenCommand)

        // Then
        val expectedResult = Model(
            id = expectedId,
            version = 0,
            createdAt = expectedTime,
            updatedAt = expectedTime,
            data = Example(
                email = givenCommand.email,
                country = givenCommand.country
            )
        )

        actualResult shouldBe expectedResult
        verify { exampleStoragePort.create(expectedResult) }
    }
}