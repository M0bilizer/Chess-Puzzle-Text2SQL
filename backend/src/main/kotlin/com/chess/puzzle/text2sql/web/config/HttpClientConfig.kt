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
import org.springframework.beans.factory.annotation.Qualifier
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
 * @property deepseekApiKey The API key for authenticating requests to the DeepSeek API.
 * @property deepSeekBaseUrl The base URL for the DeepSeek API.
 * @property mistralApiKey The API key for authenticating requests to the Mistral API.
 * @property mistralBaseUrl The base URL for the Mistral API.
 */
@Configuration
class HttpClientConfig {

    @Value("\${deepseek_api_key}") lateinit var deepseekApiKey: String
    @Value("\${deepseek_base_url}") lateinit var deepSeekBaseUrl: String
    @Value("\${mistral_api_key}") lateinit var mistralApiKey: String
    @Value("\${mistral_base_url}") lateinit var mistralBaseUrl: String

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
    @Qualifier("deepSeekClient")
    fun deepSeekClient(): OpenAI {
        return OpenAI(
            token = deepseekApiKey,
            host = OpenAIHost(deepSeekBaseUrl),
            logging =
                LoggingConfig(logLevel = LogLevel.None, logger = Logger.Simple, sanitize = true),
        )
    }

    @Bean
    @Qualifier("mistralClient")
    fun mistralClient(): OpenAI {
        return OpenAI(
            token = mistralApiKey,
            host = OpenAIHost(mistralBaseUrl),
            logging =
                LoggingConfig(logLevel = LogLevel.None, logger = Logger.Simple, sanitize = true),
        )
    }
}
