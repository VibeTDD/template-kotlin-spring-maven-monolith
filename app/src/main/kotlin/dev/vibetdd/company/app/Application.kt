package dev.vibetdd.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["dev.vibetdd"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
