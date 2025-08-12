package com.company.api.commons.utils

import java.time.Clock
import java.time.Duration
import java.time.Instant

interface TimeProvider {
    fun now(): Instant
    fun add(instant: Instant, duration: Duration): Instant
}

class SystemTimeProvider(private val clock: Clock = Clock.systemUTC()) : TimeProvider {
    override fun now(): Instant = clock.instant()
    override fun add(instant: Instant, duration: Duration): Instant = instant.plus(duration)
}