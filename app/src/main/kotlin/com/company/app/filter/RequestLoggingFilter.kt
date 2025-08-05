package com.company.app.filter

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.web.filter.CommonsRequestLoggingFilter

class RequestLoggingFilter : CommonsRequestLoggingFilter() {

    private val pathsToExclude: Set<String> = setOf()
    private val methodsToExclude: Set<String> = setOf(GET.name(), OPTIONS.name())

    override fun shouldLog(request: HttpServletRequest): Boolean {
        if (methodsToExclude.contains(request.method)) return false
        return super.shouldLog(request)
    }

    override fun createMessage(request: HttpServletRequest, prefix: String, suffix: String): String {
        val payload = getMessagePayload(request)

        if (pathsToExclude.contains(request.requestURI) || payload?.contains("password") == true) {
            return buildMessage(request, prefix, "*****")
        }

        return buildMessage(request, prefix, payload)
    }

    private fun buildMessage(request: HttpServletRequest, prefix: String, payload: String?): String {
        val msg = "$prefix: ${request.method} ${request.requestURI}"

        return if (payload == null) msg else "$msg payload=$payload"
    }
}