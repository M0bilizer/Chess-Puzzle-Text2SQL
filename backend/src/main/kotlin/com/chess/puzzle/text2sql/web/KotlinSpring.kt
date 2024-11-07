package com.chess.puzzle.text2sql.web

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class KotlinSpring {
    @Value("\${spring.datasource.url}")
    lateinit var dataSourceURL: String

    @PostConstruct
    fun init() {
        logger.info { "dataSourceURL: $dataSourceURL " }
    }
}

fun main(args: Array<String>) {
    runApplication<KotlinSpring>(*args)
}
