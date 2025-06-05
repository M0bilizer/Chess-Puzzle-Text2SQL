package com.chesspuzzletext2sql.config

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

data class EnvironmentConfig(val name: String = System.getProperty("ENV") ?: "Unknown") {
    val database = DatabaseConfig()
    val file = FileConfig()

    data class DatabaseConfig(
        val url: String =
            System.getProperty("DB_URL") ?: throw IllegalStateException("DB_URL is required"),
        val driver: String =
            System.getProperty("DB_DRIVER") ?: throw IllegalStateException("DB_DRIVER is required"),
        val user: String =
            System.getProperty("DB_USER") ?: throw IllegalStateException("DB_USER is required"),
        val password: String =
            System.getProperty("DB_PASSWORD")
                ?: throw IllegalStateException("DB_PASSWORD is required"),
    ) {
        init {
            logger.info { "Connected to $url as $user" }
        }
    }

    data class FileConfig(val promptTemplatePath: String = "inferencePromptTemplate.md")
}
