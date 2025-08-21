package com.company.examples.domain.port.config

interface ExampleConfigPort {
    fun getAllowedCountries(): Set<String>
}