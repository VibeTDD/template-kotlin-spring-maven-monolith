package com.company.examples.adapter.config

import com.company.examples.domain.port.config.ExampleConfigPort
import org.springframework.stereotype.Repository

@Repository
class ExampleConfigAdapter: ExampleConfigPort {
    override fun getAllowedCountries(): Set<String> = setOf("USA", "CA")
}