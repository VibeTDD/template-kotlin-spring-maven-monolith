package com.company.examples.config.business

interface BusinessSpecificConfig {
    fun getAllowedCountries(): Set<String>
}