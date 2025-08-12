package com.company.examples.domain.mother

import com.company.examples.domain.model.Example
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.libs.testing.Rand

object ExampleMother {

    fun of(
        email: String = Rand.email(),
        country: String = Rand.countryCode()
    ) = Example(
        email = email,
        country = country
    )
}

object CreateExampleCommandMother {

    fun of(
        email: String = Rand.email(),
        country: String = Rand.countryCode()
    ) = CreateExampleCommand(
        email = email,
        country = country
    )
}