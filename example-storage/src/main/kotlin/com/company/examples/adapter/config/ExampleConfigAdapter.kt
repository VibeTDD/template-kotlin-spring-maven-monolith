package com.company.examples.adapter.config

import com.company.api.commons.model.Range
import com.company.examples.domain.port.config.ExampleConfigPort
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class ExampleConfigAdapter: ExampleConfigPort {
    override fun getAllowedCountries(): Set<String> = setOf("USA", "CA")
    override fun getSalaryRange(): Range<BigDecimal> {
        TODO("Not yet implemented")
    }
}