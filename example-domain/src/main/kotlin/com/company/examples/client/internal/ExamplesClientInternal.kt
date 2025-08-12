package com.company.examples.client.internal

import com.company.api.commons.dto.response.ModelV1
import com.company.api.commons.mapper.toModelV1
import com.company.examples.client.ExamplesClientV1
import com.company.examples.domain.handler.CreateExampleUseCase
import com.company.examples.domain.handler.GetExampleUseCase
import com.company.examples.domain.mapper.toCommand
import com.company.examples.dto.request.CreateExampleParamsV1
import com.company.examples.dto.response.ExampleV1
import com.company.examples.mapper.toV1
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExamplesClientInternal(
    private val createExampleUseCase: CreateExampleUseCase,
    private val getExampleUseCase: GetExampleUseCase,
) : ExamplesClientV1 {

    override suspend fun create(params: CreateExampleParamsV1): ModelV1<ExampleV1> = createExampleUseCase
        .execute(params.toCommand())
        .toModelV1 { it.toV1() }

    override suspend fun get(id: UUID): ModelV1<ExampleV1> = getExampleUseCase
        .execute(id)
        .toModelV1 { it.toV1() }
}