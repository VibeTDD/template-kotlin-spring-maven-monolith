package com.company.examples.adapter

import com.company.examples.adapter.mother.ExampleMother
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.libs.testing.Rand
import com.company.libs.testing.integration.StorageTestBase
import com.company.libs.testing.mother.ModelMother
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExampleRetrievalTest: StorageTestBase() {

    @Autowired
    private lateinit var storage: ExampleStoragePort

    @Test
    fun `should not retrieve example when not exists`() {
        // Given
        val givenExample = ModelMother.ofV0(
            data = ExampleMother.of()
        )
        storage.create(givenExample)

        // When
        val actualResult = storage.getById(Rand.uuid())

        // Then
        actualResult shouldBe null
    }

    @Test
    fun `should return true when example exists by email`() {
        // Given
        val givenExample = ModelMother.ofV0(
            data = ExampleMother.of()
        )
        storage.create(givenExample)

        // When
        val actualResult = storage.existsByEmail(givenExample.data.email)

        // Then
        actualResult shouldBe true
    }

    @Test
    fun `should return false when example not exists by email`() {
        // Given
        val givenExample = ModelMother.ofV0(
            data = ExampleMother.of()
        )
        storage.create(givenExample)

        // When
        val actualResult = storage.existsByEmail(Rand.email())

        // Then
        actualResult shouldBe false
    }
}