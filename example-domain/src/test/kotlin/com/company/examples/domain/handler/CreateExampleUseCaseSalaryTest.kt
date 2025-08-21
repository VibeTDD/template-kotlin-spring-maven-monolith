package com.company.examples.domain.handler

import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import io.mockk.every
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CreateExampleUseCaseSalaryTest : CreateExampleUseCaseBaseTest() {

    @ParameterizedTest
    @ValueSource(doubles = [200.00, 200.01, 299.99, 300.00])
    fun `should create example when salary is in range`(givenSalary: Double) {
        // Given
        val givenCommand = validCommand.copy(
            salary = givenSalary.toBigDecimal()
        )
        setupValidMocks()

        // When - Then
        shouldBeValid(givenCommand)
    }

    @ParameterizedTest
    @ValueSource(doubles = [199.99, 300.01])
    fun `should return error when salary is out of range`(givenSalary: Double) {
        // Given
        val givenCommand = validCommand.copy(
            salary = givenSalary.toBigDecimal()
        )
        every { exampleConfigPort.getSalaryRange() } returns configuredRange

        // When - Then
        shouldBeInvalid(
            command = givenCommand,
            error = ValidationError(
                code = ExampleErrorCode.SALARY_OUT_OF_RANGE,
                attributes = mapOf(
                    ExampleValidationField.SALARY to givenCommand.salary,
                    ExampleValidationField.MIN to configuredRange.from,
                    ExampleValidationField.MAX to configuredRange.to,
                )
            )
        )
    }
}