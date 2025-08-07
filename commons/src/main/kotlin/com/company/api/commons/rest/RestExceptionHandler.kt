package com.company.api.commons.rest

import com.company.api.commons.dto.response.ApiErrorV1
import com.company.api.commons.dto.response.ErrorCode.BAD_REQUEST
import com.company.api.commons.dto.response.ErrorCode.DUPLICATED_KEY
import com.company.api.commons.dto.response.ErrorCode.FORBIDDEN_ACCESS
import com.company.api.commons.dto.response.ErrorCode.INTERNAL_ERROR
import com.company.api.commons.dto.response.ErrorCode.NOT_FOUND
import com.company.api.commons.dto.response.ErrorCode.OUTDATED_VERSION
import com.company.api.commons.exception.BadRequestException
import com.company.api.commons.exception.ForbiddenException
import com.company.api.commons.exception.ModelDuplicatedException
import com.company.api.commons.exception.ModelNotFoundException
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ModelNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(exception: ModelNotFoundException) = createError(
        code = NOT_FOUND,
        exception = exception,
        attributes = mapOf("model" to exception.model.simpleName) + exception.params,
    )

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handle(exception: ForbiddenException) = createError(FORBIDDEN_ACCESS, exception, exception.attributes)

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(exception: BadRequestException) = createError(BAD_REQUEST, exception)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handle(exception: Exception): ApiErrorV1 {
        log.error(exception) { "An unexpected error occurred" }

        return ApiErrorV1(
            code = INTERNAL_ERROR,
            message = "An internal error occurred, please contact support"
        )
    }

    // TODO Wrap in custom exception with user friendly message
    @ExceptionHandler(OptimisticLockingFailureException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(exception: OptimisticLockingFailureException) = createError(OUTDATED_VERSION, exception)

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(exception: DataIntegrityViolationException) = createError(DUPLICATED_KEY, exception)

    @ExceptionHandler(ModelDuplicatedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(exception: ModelDuplicatedException) = createError(
        code = DUPLICATED_KEY,
        exception = exception,
        attributes = mapOf("model" to exception.model.simpleName) + exception.params,
    )

    private fun createError(
        code: String,
        exception: Exception,
        attributes: Map<String, Any?> = mapOf(),
    ): ApiErrorV1 {
        log.warn { exception.message }

        return ApiErrorV1(
            code = code,
            message = exception.message ?: "An error occurred, please contact support",
            attributes = attributes,
        )
    }
}
