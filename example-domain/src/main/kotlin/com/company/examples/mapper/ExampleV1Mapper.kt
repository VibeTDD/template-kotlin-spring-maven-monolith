package com.company.examples.mapper

import com.company.examples.domain.model.Example
import com.company.examples.dto.response.ExampleV1

fun Example.toV1() = ExampleV1(
    email = email,
    country = country
)