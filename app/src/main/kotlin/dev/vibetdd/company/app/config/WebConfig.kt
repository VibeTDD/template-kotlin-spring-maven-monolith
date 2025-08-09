package dev.vibetdd.app.config

import dev.vibetdd.app.filter.RequestLoggingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.filter.CorsFilter

@Configuration
class WebConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.allowedMethods = listOf("*")
        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }

    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
        val filter = RequestLoggingFilter()
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setMaxPayloadLength(500)
        filter.setBeforeMessagePrefix("Before")
        filter.setAfterMessagePrefix("After")

        return filter
    }

    @Bean
    fun logRequest(logFilter: CommonsRequestLoggingFilter): FilterRegistrationBean<CommonsRequestLoggingFilter> {
        val reg = FilterRegistrationBean(logFilter)
        reg.addUrlPatterns(
            "/v1/*",
            "/v2/*",
            "/v3/*",
            "/public/*",
        )

        return reg
    }
}