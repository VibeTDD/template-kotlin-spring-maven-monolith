package com.company.app.config

import com.company.api.main.config.ApiConfig
import com.company.module_template.config.ModuleTemplateConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    ApiConfig::class,
    ModuleTemplateConfig::class,
)
class AppConfig