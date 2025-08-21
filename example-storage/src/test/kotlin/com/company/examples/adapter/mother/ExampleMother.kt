package com.company.examples.adapter.mother

import com.company.examples.domain.model.Example
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.libs.testing.Rand
import java.math.BigDecimal

object ExampleMother {

    fun of(
        email: String = Rand.email(),
        country: String = Rand.countryCode(),
        salary: BigDecimal = Rand.amount()
    ) = Example(
        email = email,
        country = country,
        salary = salary,
    )
}