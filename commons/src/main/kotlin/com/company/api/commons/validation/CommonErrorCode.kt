package com.company.api.commons.validation

enum class CommonErrorCode(val message: String) : ValidationCode {
    NOT_FOUND("The requested object is not found") {

        override fun getMessage(): String = message
    }
}