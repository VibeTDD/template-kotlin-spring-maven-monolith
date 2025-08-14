package com.company.examples.domain.port.config

interface BusinessSpecificConfigPort {
    fun getAllowedCountries(): Set<String>
}