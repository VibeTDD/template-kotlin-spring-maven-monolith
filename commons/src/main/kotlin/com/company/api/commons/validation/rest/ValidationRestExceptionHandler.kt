package com.company.api.commons.validation.rest

import com.company.api.commons.validation.dto.ValidationErrorV1
import com.company.api.commons.validation.mapper.toError
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
@Order(1)
class ValidationRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: MethodArgumentNotValidException): List<ValidationErrorV1> = exception.bindingResult.fieldErrors.map { it.toError() }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: MethodArgumentTypeMismatchException): List<ValidationErrorV1> = listOf(exception.toError())

    @ExceptionHandler(MissingKotlinParameterException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: MissingKotlinParameterException) = listOf(exception.toError())
}
