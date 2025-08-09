package dev.vibetdd.app.config

import dev.vibetdd.api.main.config.ApiConfig
import dev.vibetdd.module_template.config.PayoutsConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    ApiConfig::class,
    PayoutsConfig::class,
)
class AppConfig