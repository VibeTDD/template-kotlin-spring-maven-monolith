package com.company.api.commons.utils

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant
import java.util.*
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UUIDTest {

    @Test
    internal fun `generate random UUID 4 when params are empty`() {
        // when
        val uuid1 = uuidOf()
        val uuid2 = uuidOf()
        // then
        uuid1 shouldNotBe uuid2

        uuid1.version() shouldBe 4
    }

    @ParameterizedTest
    @MethodSource("testData")
    internal fun `always generate the same UUID 3 when params are present`(givenInput: List<Any>) {
        // when
        val uuid1 = uuidOf(givenInput)
        val uuid2 = uuidOf(givenInput)
        val uuid3 = uuidOf(givenInput)
        // then
        uuid1 shouldBe uuid2
        uuid1 shouldBe uuid3
        uuid2 shouldBe uuid3

        uuid1.version() shouldBe 3
    }

    private fun testData(): List<List<Any>> = listOf(
        listOf(UUID.randomUUID().toString(), UUID.randomUUID()),
        listOf(UUID.randomUUID().toString(), Instant.now()),
        listOf(UUID.randomUUID().toString(), Instant.now()),
        listOf(System.currentTimeMillis(), Random.nextDouble()),
    )
}