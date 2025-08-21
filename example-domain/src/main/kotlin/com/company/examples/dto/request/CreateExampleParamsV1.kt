package com.company.examples.dto.request

import java.math.BigDecimal

data class CreateExampleParamsV1(
    val email: String,
    val name: String,
    val salary: BigDecimal,
)
