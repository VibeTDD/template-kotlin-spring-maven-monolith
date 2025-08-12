package com.company.examples.client

import com.company.api.commons.dto.response.ModelV1
import com.company.examples.dto.request.CreateExampleParamsV1
import com.company.examples.dto.response.ExampleV1
import java.util.UUID

interface ExamplesClientV1 {

    suspend fun create(params: CreateExampleParamsV1): ModelV1<ExampleV1>

    suspend fun get(id: UUID): ModelV1<ExampleV1>
}