package com.company.examples.domain.model.command

import java.math.BigDecimal

data class CreateExampleCommand(
    val email: String,
    val country: String,
    val salary: BigDecimal,
)