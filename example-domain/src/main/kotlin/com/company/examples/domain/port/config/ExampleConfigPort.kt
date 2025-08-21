package com.company.examples.domain.port.config

import com.company.api.commons.model.Range
import java.math.BigDecimal

interface ExampleConfigPort {
    fun getAllowedCountries(): Set<String>
    fun getSalaryRange(): Range<BigDecimal>
}