package com.chess.puzzle.text2sql.web

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

private val logger = KotlinLogging.logger {}

/**
 * The main entry point for the Spring Boot application.
 *
 * This class is annotated with [SpringBootApplication], which combines the following annotations:
 * - [org.springframework.context.annotation.Configuration]: Marks this class as a source of bean
 *   definitions.
 * - [org.springframework.boot.autoconfigure.EnableAutoConfiguration]: Enables Spring Boot's
 *   autoconfiguration.
 * - [org.springframework.context.annotation.ComponentScan]: Scans for components, configurations,
 *   and services in the package.
 *
 * The application starts by running [runApplication] with the [KotlinSpring] class as the entry
 * point.
 */
@SpringBootApplication
class KotlinSpring {
    /**
     * The URL of the data source, injected from the application properties file.
     *
     * This value is retrieved from the `spring.datasource.url` property in the configuration file.
     */
    @Value("\${spring.datasource.url}") lateinit var dataSourceURL: String

    /**
     * Initializes the application after the Spring context is loaded.
     *
     * This method logs the data source URL to the console for debugging purposes.
     */
    @PostConstruct
    fun init() {
        logger.info { "dataSourceURL: $dataSourceURL " }
    }
}

/**
 * The main function that starts the Spring Boot application.
 *
 * This function uses [runApplication] to bootstrap the application with the [KotlinSpring] class as
 * the entry point.
 *
 * @param args Command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
    runApplication<KotlinSpring>(*args)
}
