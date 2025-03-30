package com.chess.puzzle.text2sql.web.config

import com.chess.puzzle.text2sql.web.domain.model.llm.OpenAiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
 * @property deepSeekTimeout The timeout (in milliseconds) for requests to the DeepSeek API.
 * @property mistralApiKey The API key for authenticating requests to the Mistral API.
 * @property mistralBaseUrl The base URL for the Mistral API.
 * @property mistralTimeout The timeout (in milliseconds) for requests to the Mistral API.
 */
@Configuration
class HttpClientConfig {

    @Value("\${deepseek_api_key}") lateinit var deepseekApiKey: String
    @Value("\${deepseek_base_url}") lateinit var deepSeekBaseUrl: String
    @Value("\${deepseek_timeout}") lateinit var deepSeekTimeout: Number
    @Value("\${mistral_api_key}") lateinit var mistralApiKey: String
    @Value("\${mistral_base_url}") lateinit var mistralBaseUrl: String
    @Value("\${mistral_timeout}") lateinit var mistralTimeout: Number

    /**
     * Creates and configures an HTTP client using Ktor's OkHttp engine.
     *
     * The client is configured with `ContentNegotiation` to support JSON serialization using
     * Kotlin's `kotlinx.serialization` library. This client can be used for general-purpose HTTP
     * communication.
     *
     * @return An instance of `HttpClient` configured for JSON communication.
     */
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp) { install(ContentNegotiation) { json() } }
    }

    /**
     * Creates and configures an OpenAI client for the DeepSeek API.
     *
     * The client is initialized with the provided API key, base URL, and timeout settings. It uses
     * Ktor's `HttpClient` with the OkHttp engine and is configured to handle JSON serialization and
     * deserialization using `kotlinx.serialization`. The client also includes timeout settings to
     * ensure requests do not hang indefinitely.
     *
     * @return An instance of `OpenAiClient` configured for communication with the DeepSeek API.
     */
    @Bean
    @Qualifier("deepSeekClient")
    fun deepSeekClient(): OpenAiClient {
        return OpenAiClient(
            client =
                HttpClient(OkHttp) {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                    install(HttpTimeout) {
                        requestTimeoutMillis = deepSeekTimeout.toLong()
                        socketTimeoutMillis = deepSeekTimeout.toLong()
                    }
                },
            apiKey = deepseekApiKey,
            baseUrl = deepSeekBaseUrl,
        )
    }

    /**
     * Creates and configures an OpenAI client for the Mistral API.
     *
     * The client is initialized with the provided API key, base URL, and timeout settings. It uses
     * Ktor's `HttpClient` with the OkHttp engine and is configured to handle JSON serialization and
     * deserialization using `kotlinx.serialization`. The client also includes timeout settings to
     * ensure requests do not hang indefinitely.
     *
     * @return An instance of `OpenAiClient` configured for communication with the Mistral API.
     */
    @Bean
    @Qualifier("mistralClient")
    fun mistralClient(): OpenAiClient {
        return OpenAiClient(
            client =
                HttpClient(OkHttp) {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                    install(HttpTimeout) {
                        requestTimeoutMillis = mistralTimeout.toLong()
                        socketTimeoutMillis = mistralTimeout.toLong()
                    }
                },
            apiKey = mistralApiKey,
            baseUrl = mistralBaseUrl,
        )
    }
}
