package com.chess.puzzle.text2sql.web.config

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for setting up HTTP client and OpenAI client.
 *
 * This class is annotated with `@Configuration` to indicate that it provides bean definitions for
 * the Spring application context. It configures an HTTP client using Ktor's OkHttp engine and an
 * OpenAI client with custom logging and API settings.
 *
 * @property apiKey The API key for authenticating requests to the OpenAI API.
 * @property baseUrl The base URL for the OpenAI API.
 */
@Configuration
class HttpClientConfig {

    @Value("\${api_key}") lateinit var apiKey: String

    @Value("\${base_url}") lateinit var baseUrl: String

    /**
     * Creates and configures an HTTP client using Ktor's OkHttp engine.
     *
     * The client is configured with `ContentNegotiation` to support JSON serialization using
     * Kotlin's `kotlinx.serialization` library.
     *
     * @return An instance of `HttpClient` configured for JSON communication.
     */
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp) { install(ContentNegotiation) { json() } }
    }

    /**
     * Creates and configures an OpenAI client.
     *
     * The client is initialized with the provided API key, base URL, and logging configuration.
     * Logging is configured to sanitize sensitive information and use a simple logger with no log
     * level.
     *
     * @return An instance of `OpenAI` configured for API communication.
     */
    @Bean
    fun openAi(): OpenAI {
        val apiKey = apiKey
        val baseUrl = baseUrl
        val loggingConfig =
            LoggingConfig(logLevel = LogLevel.None, logger = Logger.Simple, sanitize = true)
        return OpenAI(token = apiKey, host = OpenAIHost(baseUrl), logging = loggingConfig)
    }
}
