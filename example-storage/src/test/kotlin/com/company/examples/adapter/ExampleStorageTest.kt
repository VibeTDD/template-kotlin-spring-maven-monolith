package com.company.examples.adapter

import com.company.api.commons.exception.ModelDuplicatedException
import com.company.examples.adapter.mother.ExampleMother
import com.company.examples.domain.model.Example
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.libs.testing.integration.StorageTestBase
import com.company.libs.testing.mother.ModelMother
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExampleStorageTest: StorageTestBase() {

    @Autowired
    private lateinit var storage: ExampleStoragePort

    @Test
    fun `should store example with all data and incremented version`() {
        // Given
        val givenExample = ModelMother.ofV0(
            data = ExampleMother.of()
        )

        // When
        storage.create(givenExample)

        // Then
        val storedExample = storage.getById(givenExample.id)
        storedExample shouldBe givenExample.copy(version = 1)
    }

    @Test
    fun `should reject example when it is duplicated`() {
        val givenExample = ModelMother.ofV0(
            data = ExampleMother.of()
        )
        storage.create(givenExample)

        // When
        val exception = shouldThrow<ModelDuplicatedException> {
            storage.create(givenExample)
        }

        exception.model shouldBe Example::class
    }
}