package com.company.examples.domain.validator

import com.company.api.commons.validation.ValidationRule
import com.company.api.commons.validation.exception.ValidationError
import com.company.examples.domain.constant.ExampleErrorCode
import com.company.examples.domain.constant.ExampleValidationField
import com.company.examples.domain.port.config.ExampleConfigPort
import com.company.examples.domain.model.command.CreateExampleCommand

class SalaryValidator(
    private val exampleConfigPort: ExampleConfigPort,
) : ValidationRule<CreateExampleCommand> {
    
    override fun validate(command: CreateExampleCommand): List<ValidationError> {
        val salaryRange = exampleConfigPort.getSalaryRange()
        if (salaryRange.isIn(command.salary)) return emptyList()

        return listOf(
            ValidationError(
                code = ExampleErrorCode.SALARY_OUT_OF_RANGE,
                attributes = mapOf(
                    ExampleValidationField.SALARY to command.salary,
                    ExampleValidationField.MIN to salaryRange.from,
                    ExampleValidationField.MAX to salaryRange.to,
                )
            )
        )
    }
}