package com.company.examples.domain.mapper

import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.dto.request.CreateExampleParamsV1

fun CreateExampleParamsV1.toCommand() = CreateExampleCommand(
    email = email,
    country = name,
)