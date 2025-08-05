package com.company.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.company"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
