package com.company.examples.domain.model.command

data class CreateExampleCommand(
    val email: String,
    val country: String
)