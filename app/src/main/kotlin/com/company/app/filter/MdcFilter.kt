package com.company.app.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class MdcFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val remoteUser = (request as HttpServletRequest).remoteUser

        if (remoteUser != null) {
            MDC.put("userId", remoteUser.substringAfter("|"))
        }

        chain.doFilter(request, response);
    }
}