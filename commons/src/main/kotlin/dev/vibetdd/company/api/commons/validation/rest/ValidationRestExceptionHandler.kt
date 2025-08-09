package dev.vibetdd.api.commons.validation.rest

import dev.vibetdd.api.commons.dto.response.ErrorResponseV1
import dev.vibetdd.api.commons.validation.exception.ValidationException
import dev.vibetdd.api.commons.validation.mapper.toV1
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
    fun handle(exception: MethodArgumentNotValidException) = ErrorResponseV1(exception.bindingResult.fieldErrors.map { it.toV1() })

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: MethodArgumentTypeMismatchException) = ErrorResponseV1(listOf(exception.toV1()))

    @ExceptionHandler(MissingKotlinParameterException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: MissingKotlinParameterException) = ErrorResponseV1(listOf(exception.toV1()))

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handle(exception: ValidationException) = ErrorResponseV1(exception.errors.map {it.toV1()})
}
