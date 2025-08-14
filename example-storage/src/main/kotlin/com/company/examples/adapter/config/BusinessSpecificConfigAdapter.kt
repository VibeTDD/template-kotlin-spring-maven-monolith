package com.company.examples.adapter.config

import com.company.examples.domain.port.config.BusinessSpecificConfigPort
import org.springframework.stereotype.Repository

@Repository
class BusinessSpecificConfigAdapter: BusinessSpecificConfigPort {
    override fun getAllowedCountries(): Set<String> = setOf("USA", "CA")
}