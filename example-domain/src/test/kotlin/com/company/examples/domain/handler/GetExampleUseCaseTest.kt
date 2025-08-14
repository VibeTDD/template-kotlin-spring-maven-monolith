package com.company.examples.domain.handler

import com.company.api.commons.exception.ModelNotFoundException
import com.company.examples.domain.model.Example
import com.company.examples.domain.mother.ExampleMother
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.libs.testing.Rand
import com.company.libs.testing.mother.ModelMother
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetExampleUseCaseTest {

    @InjectMockKs
    private lateinit var useCase: GetExampleUseCase

    @MockK
    private lateinit var storagePort: ExampleStoragePort

    @Test
    fun `should return user when it exists`() {
        // Given
        val givenId = Rand.uuid()
        val expectedResult = ModelMother.of(
            id = givenId,
            version = 1,
            data = ExampleMother.of()
        )

        every { storagePort.getById(givenId) } returns expectedResult

        // When
        val actualResult = useCase.execute(givenId)

        // Then
        actualResult shouldBe expectedResult
        verify { storagePort.getById(givenId) }
    }

    @Test
    fun `should throw exception when user does not exists`() {
        // Given
        val givenId = Rand.uuid()

        every { storagePort.getById(givenId) } returns null

        // When
        val exception = shouldThrow<ModelNotFoundException> {
            useCase.execute(givenId)
        }

        // Then
        exception.model shouldBe Example::class

        verify { storagePort.getById(givenId) }
    }
}